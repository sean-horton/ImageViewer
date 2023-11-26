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

import com.onebyte_llc.imageviewer.I18N;
import com.onebyte_llc.imageviewer.MainApplication;
import com.onebyte_llc.imageviewer.Theme;
import com.onebyte_llc.imageviewer.ViewNode;
import com.onebyte_llc.imageviewer.backend.Context;
import com.onebyte_llc.imageviewer.backend.DisplayState;
import com.onebyte_llc.imageviewer.backend.ImageHandle;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Shown when an image is selected and in full screen
 */
public class HeaderMode1ViewController {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("LLLL d, yyyy 'at' h:mm:ss a");

    @FXML
    private Slider scaleSlider;
    @FXML
    private Label timeLabel;
    @FXML
    private Label posLabel;
    @FXML
    private Button backButton;
    @FXML
    private ImageView backButtonImage;

    public static ViewNode<Node, HeaderMode1ViewController> create() {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class
                .getResource("/layout/display/header/header-mode1-view.fxml"));
        try {
            return new ViewNode<>(fxmlLoader.load(), fxmlLoader.getController());
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load HeaderMode1ViewController", e);
        }
    }


    @FXML
    private void initialize() {
        DisplayState state = Context.getInstance().getDisplayState();
        backButtonImage.effectProperty().bind(Theme.buttonToolbarEffect());
        backButton.setOnAction(event -> state.fullScreenImageProperty().setValue(null));
        state.fullScreenImageProperty().addListener((observable, oldValue, newValue) -> setImageHandle(newValue));

        Bindings.bindBidirectional(scaleSlider.valueProperty(), state.fullScreenScaleFactorProperty());
        scaleSlider.minProperty().bind(state.fullScreeMinScaleFactorProperty());
        scaleSlider.maxProperty().bind(state.fullScreeMaxScaleFactorProperty());
    }

    public void setImageHandle(ImageHandle imageHandle) {
        if (imageHandle == null) {
            return;
        }

        List<ImageHandle> imageHandles = Context.getInstance().getCollectionService().getCollectionImages();
        int num = -1;
        for (int i = 0; i < imageHandles.size(); i++) {
            ImageHandle handle = imageHandles.get(i);
            if (handle == imageHandle) {
                num = i + 1;
                break;
            }
        }

        if (num >= 0) {
            posLabel.textProperty().bind(I18N.get("image.num.of.num", num, imageHandles.size()));
        } else {
            posLabel.textProperty().unbind();
            posLabel.setText("");
        }

        if (imageHandle.getImOriginalDate() == null) {
            timeLabel.textProperty().bind(I18N.get("image.no.date"));
        } else {
            timeLabel.textProperty().unbind();
            timeLabel.setText(imageHandle.getImOriginalDate().format(FORMATTER));
        }
    }

}
