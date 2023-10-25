package com.onebytellc.imageviewer.ui.display.grid;

import com.onebytellc.imageviewer.backend.ChangeSet;
import com.onebytellc.imageviewer.backend.CollectionService;
import com.onebytellc.imageviewer.backend.Context;
import com.onebytellc.imageviewer.backend.DisplayState;
import com.onebytellc.imageviewer.backend.ImageHandle;
import com.onebytellc.imageviewer.controls.GridView;
import com.onebytellc.imageviewer.controls.ImageGridRenderer;
import com.onebytellc.imageviewer.logger.Logger;
import com.onebytellc.imageviewer.reactive.Executor;
import com.onebytellc.imageviewer.reactive.Subscription;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;

/**
 * This is the image grid that can display 1,000's of images at
 * once with fast scrolling and zooming. This was fun to make.
 */
public class ImageGridCanvasController {

    private static final Logger LOG = Logger.getInstance(ImageGridCanvasController.class);

    @FXML
    private GridView<ImageHandle> gridView;

    private Subscription cacheUpdateSub;
    private Subscription collectionImageSub;

    @FXML
    private void initialize() {
        DisplayState state = Context.getInstance().getDisplayState();
        CollectionService collectionService = Context.getInstance().getCollectionService();

        Bindings.bindBidirectional(gridView.scaleFactorProperty(), state.gridImageScaleFactorProperty());
        gridView.minScaleFactorProperty().bind(state.gridMinScaleFactorProperty());
        gridView.maxScaleFactorProperty().bind(state.gridMaxScaleFactorProperty());
        gridView.baseImageSizeProperty().bind(state.gridBaseImageSizeProperty());
        gridView.setGridCellRenderer(new ImageGridRenderer());

        cacheUpdateSub = collectionService.cacheUpdateStream()
                .observeOn(Executor.fxApplicationThread())
                .subscribe(b -> gridView.invalidate());

        collectionImageSub = collectionService.collectionImageRecords()
                .observeOn(Executor.fxApplicationThread())
                .subscribe(this::handeEvent);
    }

    private void handeEvent(ChangeSet<ImageHandle> change) {
        if (change.isReset()) {
            gridView.getItems().clear();
        }

        // remove items
        for (ImageHandle removed : change.getRemoved()) {
            gridView.getItems().removeIf(item -> item.getId() == removed.getId());
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
            for (int i = 0; i < gridView.getItems().size(); i++) {
                if (updated.getId() == gridView.getItems().get(i).getId()) {
                    gridView.getItems().set(i, updated);
                    break;
                }
            }
        }

        // add items
        gridView.getItems().addAll(change.getAdded());

        // sort items
        gridView.getItems().sort((o1, o2) -> {
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
