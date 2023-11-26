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

package com.onebyte_llc.imageviewer.ui.display.header;

import com.onebyte_llc.imageviewer.backend.DisplayState;
import com.onebyte_llc.imageviewer.MainApplication;
import com.onebyte_llc.imageviewer.Theme;
import com.onebyte_llc.imageviewer.ViewNode;
import com.onebyte_llc.imageviewer.backend.Context;
import com.onebyte_llc.imageviewer.controls.gridview.ImageRenderMode;
import com.onebyte_llc.imageviewer.logger.Logger;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;

/**
 * Shown when the image grid view is displaying
 */
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

    public static ViewNode<Node, HeaderMode0ViewController> create() {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class
                .getResource("/layout/display/header/header-mode0-view.fxml"));
        try {
            return new ViewNode<>(fxmlLoader.load(), fxmlLoader.getController());
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load HeaderMode0ViewController", e);
        }
    }

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
        renderModeImage.effectProperty().bind(Theme.buttonToolbarEffect());
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
