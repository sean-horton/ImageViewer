package com.onebytellc.imageviewer.ui.display.header;

import com.onebytellc.imageviewer.Theme;
import com.onebytellc.imageviewer.backend.Context;
import com.onebytellc.imageviewer.backend.DisplayState;
import com.onebytellc.imageviewer.controls.gridview.ImageRenderMode;
import com.onebytellc.imageviewer.logger.Logger;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


public class HeaderMode0ViewController {

    private static final Logger LOG = Logger.getInstance(HeaderMode0ViewController.class);

    private static final Image fitImage = new Image(HeaderMode0ViewController.class.getResource("/image/image-render-fit.png").toString());
    private static final Image fullImage = new Image(HeaderMode0ViewController.class.getResource("/image/image-render-full.png").toString());

    @FXML
    private Slider scaleSlider;
    @FXML
    private Button renderModeButton;
    @FXML
    private ImageView renderModeImage;

    @FXML
    private void initialize() {
        Context context = Context.getInstance();
        DisplayState state = context.getDisplayState();

        // slider image scale size
        Bindings.bindBidirectional(scaleSlider.valueProperty(), state.gridImageScaleFactorProperty());
        scaleSlider.minProperty().bind(state.gridMinScaleFactorProperty());
        scaleSlider.maxProperty().bind(state.gridMaxScaleFactorProperty());

        // render mode button
        state.imageRenderModeProperty().addListener((observable, oldValue, newValue) -> configureImageRenderImage());
        configureImageRenderImage();
        renderModeButton.effectProperty().bind(Theme.buttonToolbarEffect());
        renderModeButton.setOnAction(e -> {
            ImageRenderMode mode = state.imageRenderModeProperty().get();
            state.imageRenderModeProperty().setValue(mode == ImageRenderMode.FIT ? ImageRenderMode.FULL : ImageRenderMode.FIT);
        });
    }

    private void configureImageRenderImage() {
        ImageRenderMode mode = Context.getInstance().getDisplayState().imageRenderModeProperty().get();
        if (mode == ImageRenderMode.FIT) {
            renderModeImage.setImage(fullImage);
        } else if (mode == ImageRenderMode.FULL) {
            renderModeImage.setImage(fitImage);
        } else {
            LOG.warn("Unknown ImageRenderMode: {}", mode);
        }
    }

}
