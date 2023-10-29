package com.onebytellc.imageviewer.ui.directory;

import com.onebytellc.imageviewer.Theme;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;


public class DirectoryToolbarController {

    @FXML
    private Button addButton;
    @FXML
    private ImageView addButtonImage;

    @FXML
    private void initialize() {
        Theme.buttonBlue().addListener((observable, oldValue, newValue) -> configureButton(newValue));
        configureButton(Theme.buttonBlue().get());
    }

    private void configureButton(Color color) {
        Lighting lighting = new Lighting(new Light.Distant(45, 90, color));
        ColorAdjust bright = new ColorAdjust(0, 1, 1, 1);
        lighting.setContentInput(bright);
        lighting.setSurfaceScale(0.0);
        addButton.setEffect(lighting);
    }

}
