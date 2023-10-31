package com.onebytellc.imageviewer.ui.display.grid;

import com.onebytellc.imageviewer.Theme;
import com.onebytellc.imageviewer.backend.ChangeSet;
import com.onebytellc.imageviewer.backend.CollectionService;
import com.onebytellc.imageviewer.backend.Context;
import com.onebytellc.imageviewer.backend.DisplayState;
import com.onebytellc.imageviewer.backend.ImageHandle;
import com.onebytellc.imageviewer.controls.CanvasLayer;
import com.onebytellc.imageviewer.controls.CanvasView;
import com.onebytellc.imageviewer.controls.TransitionZoomExpand;
import com.onebytellc.imageviewer.controls.gridview.GridLayer;
import com.onebytellc.imageviewer.controls.gridview.ImageGridRenderer;
import com.onebytellc.imageviewer.controls.imageview.ImageLayer;
import com.onebytellc.imageviewer.controls.scrollbar.ScrollBarLayer;
import com.onebytellc.imageviewer.logger.Logger;
import com.onebytellc.imageviewer.reactive.Executor;
import com.onebytellc.imageviewer.reactive.Subscription;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.input.KeyEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * This is the image grid that can display 1,000's of images at
 * once with fast scrolling and zooming. This was fun to make.
 */
public class ImageGridCanvasController {

    private static final Logger LOG = Logger.getInstance(ImageGridCanvasController.class);


    @FXML
    private CanvasView canvasView;
    private GridLayer<ImageHandle> gridLayer = new GridLayer<>();
    private ScrollBarLayer scrollBarLayer = new ScrollBarLayer();
    private ImageLayer imageLayer = new ImageLayer();


    private Subscription cacheUpdateSub;
    private Subscription collectionImageSub;
    private ImageHandle viewingImage;
    private ImageGridRenderer imageGridRenderer = new ImageGridRenderer();

    @FXML
    private void initialize() {
        gridLayer.backgroundColorProperty().bind(Theme.imageBackground());

        DisplayState state = Context.getInstance().getDisplayState();
        CollectionService collectionService = Context.getInstance().getCollectionService();

        Bindings.bindBidirectional(gridLayer.contentHeightProperty(), scrollBarLayer.contentHeightProperty());
        Bindings.bindBidirectional(gridLayer.contentOffsetProperty(), scrollBarLayer.contentOffsetProperty());
        canvasView.attach(gridLayer);
        canvasView.attach(scrollBarLayer); // TODO - this should be added by gridLayer as an internal layer

        Bindings.bindBidirectional(gridLayer.scaleFactorProperty(), state.gridImageScaleFactorProperty());
        gridLayer.minScaleFactorProperty().bind(state.gridMinScaleFactorProperty());
        gridLayer.maxScaleFactorProperty().bind(state.gridMaxScaleFactorProperty());
        gridLayer.baseImageSizeProperty().bind(state.gridBaseImageSizeProperty());

        // grid cell config
        gridLayer.setGridCellRenderer(imageGridRenderer);
        gridLayer.setOnCellClicked(this::openFullScreenImage);

        // change render mode
        state.imageRenderModeProperty().addListener((observable, oldValue, newValue) -> {
            imageGridRenderer.setRenderMode(newValue);
            gridLayer.draw();
        });

        // when the cache has a new item (maybe a higher res was read from disk)
        cacheUpdateSub = collectionService.cacheUpdateStream()
                .observeOn(Executor.fxApplicationThread())
                .subscribe(b -> canvasView.invalidate());

        // Items have changed (added/updated/removed)
        collectionImageSub = collectionService.collectionImageRecords()
                .observeOn(Executor.fxApplicationThread())
                .subscribe(this::handeEvent);

        // keyboard press (example, move to next full screen image)
        canvasView.setOnKeyPressed(this::onKeyPress);
    }

    private void openFullScreenImage(ImageHandle item, Bounds itemBounds) {
        List<CanvasLayer> newLayers = new ArrayList<>();
        newLayers.add(imageLayer);

        canvasView.playTransition(new TransitionZoomExpand(itemBounds, new ArrayList<>(), newLayers), TimeUnit.MILLISECONDS, 250); // imageLayer
        imageLayer.imagePropertyProperty().set(item);
        viewingImage = item;
        for (int i = 0; i < gridLayer.getItems().size(); i++) {
            if (gridLayer.getItems().get(i) == viewingImage) {
                prefetchFullSizeImages(i);
                break;
            }
        }
    }

    private void prefetchFullSizeImages(int i) {
        gridLayer.getItems().get(i).getImage(Integer.MAX_VALUE, Integer.MAX_VALUE);
        if (i + 1 < gridLayer.getItems().size()) {
            gridLayer.getItems().get(i + 1).getImage(Integer.MAX_VALUE, Integer.MAX_VALUE);
        }
        if (i - 1 >= 0) {
            gridLayer.getItems().get(i - 1).getImage(Integer.MAX_VALUE, Integer.MAX_VALUE);
        }
    }

    private void onKeyPress(KeyEvent event) {
        if (viewingImage == null) {
            return;
        }

        switch (event.getCode()) {
            case ESCAPE -> {
                event.consume();
                // TODO - start transition
                canvasView.detach(imageLayer);
                viewingImage = null;
            }
            case SPACE -> {
                event.consume();
                // TODO - start sideshow
            }
            case RIGHT, UP -> { // next
                event.consume();
                for (int i = 0; i < gridLayer.getItems().size(); i++) {
                    ImageHandle handle = gridLayer.getItems().get(i);
                    if (viewingImage == handle) {
                        if (i + 1 < gridLayer.getItems().size()) {
                            imageLayer.imagePropertyProperty().set(gridLayer.getItems().get(i + 1));
                            viewingImage = imageLayer.imagePropertyProperty().get();
                            prefetchFullSizeImages(i + 1);
                        }
                        break;
                    }
                }
            }
            case LEFT, DOWN -> { // prev
                event.consume();
                for (int i = 0; i < gridLayer.getItems().size(); i++) {
                    ImageHandle handle = gridLayer.getItems().get(i);
                    if (viewingImage == handle) {
                        if (i - 1 >= 0) {
                            imageLayer.imagePropertyProperty().set(gridLayer.getItems().get(i - 1));
                            viewingImage = imageLayer.imagePropertyProperty().get();
                            prefetchFullSizeImages(i - 1);
                        }
                        break;
                    }
                }
            }
        }
    }

    private void handeEvent(ChangeSet<ImageHandle> change) {
        if (change.isReset()) {
            gridLayer.getItems().clear();
        }

        // remove items
        for (ImageHandle removed : change.getRemoved()) {
            gridLayer.getItems().removeIf(item -> item.getId() == removed.getId());
        }

        // update items
        for (ImageHandle updated : change.getUpdated()) {
            // TODO - this could be improved by using binary search on
            //  imOriginalDate, and if it is null, binary search on name
            //  in the 'null' sorted area. NOTE: in order to do this,
            //  the updated records list needs to contain the old item + new item,
            //  because we need to search for the old item as imOriginalDate may
            //  be updating from null to a date OR from an old date to a new date
            //    - OR we could just use a map as a lookup table
            for (int i = 0; i < gridLayer.getItems().size(); i++) {
                if (updated.getId() == gridLayer.getItems().get(i).getId()) {
                    gridLayer.getItems().set(i, updated);
                    break;
                }
            }
        }

        // add items
        gridLayer.getItems().addAll(change.getAdded());

        // sort items
        gridLayer.getItems().sort((o1, o2) -> {
            if (o1.getImOriginalDate() == null && o2.getImOriginalDate() == null) {
                return o1.getFileName().compareTo(o2.getFileName());
            } else if (o1.getImOriginalDate() == null) {
                return -1;
            } else if (o2.getImOriginalDate() == null) {
                return 1;
            }
            return o1.getImOriginalDate().compareTo(o2.getImOriginalDate());
        });
    }

}
