package com.onebytellc.imageviewer.ui.display.grid;

import com.onebytellc.imageviewer.backend.Context;
import com.onebytellc.imageviewer.backend.DisplayState;
import com.onebytellc.imageviewer.controls.GridView;
import com.onebytellc.imageviewer.controls.ImageGridItem;
import com.onebytellc.imageviewer.logger.Logger;
import com.onebytellc.imageviewer.reactive.Executor;
import com.onebytellc.imageviewer.reactive.Subscription;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.List;

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

        subscription = Context.getInstance().getDisplayState().observeActiveImages()
                .observeOn(Executor.fxApplicationThread())
                .subscribe(images -> {
                    LOG.info("Updating images: ");

                    List<ImageGridItem> items = new ArrayList<>(images.size());
                    for (Image image : images) {
                        items.add(new ImageGridItem(image));
                    }
                    gridView.getItems().setAll(items);
                });
    }

}
