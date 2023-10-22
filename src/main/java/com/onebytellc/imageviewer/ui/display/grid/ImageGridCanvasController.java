package com.onebytellc.imageviewer.ui.display.grid;

import com.onebytellc.imageviewer.backend.Context;
import com.onebytellc.imageviewer.backend.DisplayState;
import com.onebytellc.imageviewer.controls.GridView;
import com.onebytellc.imageviewer.controls.ImageGridItem;
import com.onebytellc.imageviewer.logger.Logger;
import com.onebytellc.imageviewer.reactive.Subscription;
import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;

/**
 * This is the image grid that can display 1,000's of images at
 * once with fast scrolling and zooming. This was fun to make.
 */
public class ImageGridCanvasController {

    private static final Logger LOG = Logger.getInstance(ImageGridCanvasController.class);

    @FXML
    private GridView<ImageGridItem> gridView;

    private Subscription subscription;

    @FXML
    private void initialize() {
        DisplayState state = Context.getInstance().getDisplayState();

        Bindings.bindBidirectional(gridView.scaleFactorProperty(), state.gridImageScaleFactorProperty());
        gridView.minScaleFactorProperty().bind(state.gridMinScaleFactorProperty());
        gridView.maxScaleFactorProperty().bind(state.gridMaxScaleFactorProperty());
        gridView.baseImageSizeProperty().bind(state.gridBaseImageSizeProperty());

        state.activeCollectionProperty().addListener((ListChangeListener<ImageGridItem>) c -> {
            gridView.getItems().setAll(c.getList());
        });
    }

}
