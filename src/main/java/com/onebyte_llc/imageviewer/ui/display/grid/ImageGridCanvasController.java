/*
 *     ImageViewer - free image viewing gui
 *     Copyright (C) 2023  Sean Horton
 *
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; version 2 of the License
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with this program; if not, write to the Free Software Foundation, Inc.,
 *     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.onebyte_llc.imageviewer.ui.display.grid;

import com.onebyte_llc.imageviewer.Theme;
import com.onebyte_llc.imageviewer.backend.CollectionService;
import com.onebyte_llc.imageviewer.backend.Context;
import com.onebyte_llc.imageviewer.backend.DisplayState;
import com.onebyte_llc.imageviewer.backend.ImageHandle;
import com.onebyte_llc.imageviewer.controls.CanvasLayer;
import com.onebyte_llc.imageviewer.controls.CanvasView;
import com.onebyte_llc.imageviewer.controls.LeftRightControlsLayer;
import com.onebyte_llc.imageviewer.controls.TransitionZoomExpand;
import com.onebyte_llc.imageviewer.controls.gridview.GridLayer;
import com.onebyte_llc.imageviewer.controls.gridview.ImageGridRenderer;
import com.onebyte_llc.imageviewer.controls.imageview.ImageLayer;
import com.onebyte_llc.imageviewer.controls.scrollbar.ScrollBarLayer;
import com.onebyte_llc.imageviewer.logger.Logger;
import com.onebyte_llc.imageviewer.reactive.Executor;
import com.onebyte_llc.imageviewer.reactive.Subscription;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
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
    private LeftRightControlsLayer leftRightControlLayer = new LeftRightControlsLayer();


    private Subscription cacheUpdateSub;
    private Subscription collectionImageSub;
    private ImageHandle viewingImage;
    private ImageGridRenderer imageGridRenderer = new ImageGridRenderer();
    private DisplayState state;

    private ScheduledFuture<?> nextSlideShowFuture;

    @FXML
    private void initialize() {
        state = Context.getInstance().getDisplayState();
        CollectionService collectionService = Context.getInstance().getCollectionService();

        gridLayer.backgroundColorProperty().bind(Theme.imageBackground());

        // image grid bindings
        gridLayer.setItems(collectionService.getCollectionImages());
        collectionService.cacheUpdateStream()
                .observeOn(Executor.fxApplicationThread())
                .subscribe(b -> canvasView.invalidate());
        collectionService.collectionImageRecords()
                .observeOn(Executor.fxApplicationThread())
                .subscribe(c -> {
                    if (c.isReset()) {
                        closeFullScreenImage();
                    }
                });

        // bind full screen to current displaying image
        Bindings.bindBidirectional(state.fullScreenImageProperty(), imageLayer.imagePropertyProperty());
        state.fullScreenImageProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                closeFullScreenImage();
            }
        });

        // property bindings
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

        // full screen image scale binding
        Bindings.bindBidirectional(imageLayer.zoomScaleProperty(), state.fullScreenScaleFactorProperty());
        imageLayer.minZoomScaleProperty().bind(state.fullScreeMinScaleFactorProperty());
        imageLayer.maxZoomScaleProperty().bind(state.fullScreeMaxScaleFactorProperty());
        imageLayer.slideshowProperty().bind(state.isSlideshowProperty());

        // keyboard press (example, move to next full screen image)
        canvasView.setOnKeyPressed(this::onKeyPress);

        // left / right click listeners
        leftRightControlLayer.setOnLeft(this::prevImage);
        leftRightControlLayer.setOnRight(this::nextImage);
        leftRightControlLayer.setLeftImage(new Image(getClass().getResource("/image/back-button.png").toString()));
        leftRightControlLayer.setRightImage(new Image(getClass().getResource("/image/forward-button.png").toString()));
        state.fullScreenScaleFactorProperty().addListener((observable, oldValue, newValue) ->
                leftRightControlLayer.setEnabled(newValue.doubleValue() == 1));

        // slideshow binding
        state.isSlideshowProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                playSlideshow();
            } else {
                stopSlideshow();
            }
        });
    }

    private void openFullScreenImage(ImageHandle item, Bounds itemBounds) {
        DisplayState state = Context.getInstance().getDisplayState();
        state.fullScreenScaleFactorProperty().set(1);

        List<CanvasLayer> newLayers = new ArrayList<>();
        newLayers.add(imageLayer);
        newLayers.add(leftRightControlLayer);

        List<CanvasLayer> oldLayers = new ArrayList<>();
        oldLayers.add(gridLayer);
        oldLayers.add(scrollBarLayer);

        canvasView.playTransition(new TransitionZoomExpand(itemBounds, oldLayers, newLayers), TimeUnit.MILLISECONDS, 250); // imageLayer
        imageLayer.imagePropertyProperty().set(item);
        viewingImage = item;
        for (int i = 0; i < gridLayer.getItems().size(); i++) {
            if (gridLayer.getItems().get(i) == viewingImage) {
                prefetchFullSizeImages(i);
                break;
            }
        }
    }

    private void closeFullScreenImage() {
        // TODO - start transition
        imageLayer.imagePropertyProperty().set(null);
        canvasView.detach(imageLayer);
        canvasView.detach(leftRightControlLayer);

        canvasView.attach(gridLayer);
        canvasView.attach(scrollBarLayer);

        canvasView.invalidate();
        viewingImage = null;
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
                if (state.isSlideshowProperty().get()) {
                    state.isSlideshowProperty().set(false);
                } else {
                    closeFullScreenImage();
                }
            }
            case SPACE -> {
                event.consume();
                state.isSlideshowProperty().set(!state.isSlideshowProperty().get());
            }
            case RIGHT, UP -> { // next
                event.consume();
                nextImage();
            }
            case LEFT, DOWN -> { // prev
                event.consume();
                prevImage();
            }
        }
    }

    private void nextImage() {
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

    private void prevImage() {
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

    ///////////////////////
    // slideshow
    private void playSlideshow() {
        LOG.info("Slideshow starting");
        if (viewingImage == null && !gridLayer.getItems().isEmpty()) {
            // Open full screen image if not already
            openFullScreenImage(gridLayer.getItems().get(0), new BoundingBox(0, 0, 0, 0));
        }
        if (viewingImage == null) {
            return; // It's possible we still have no image for a slideshow
        }

        slideshowNext();
    }

    private void slideshowNext() {
        nextSlideShowFuture = Executor.fxApplicationThread().run(() -> {
            nextImage();
            slideshowNext();
        }, 3, TimeUnit.SECONDS);
    }

    private void stopSlideshow() {
        LOG.info("Slideshow stopping");
        if (nextSlideShowFuture != null) {
            nextSlideShowFuture.cancel(false);
            nextSlideShowFuture = null;
        }
    }

}
