package com.onebytellc.imageviewer.ui.directory;

import com.onebytellc.imageviewer.Theme;
import com.onebytellc.imageviewer.backend.CollectionService;
import com.onebytellc.imageviewer.backend.Context;
import com.onebytellc.imageviewer.logger.Logger;
import com.onebytellc.imageviewer.reactive.Executor;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;


public class DirectoryToolbarController {

    private static final Logger LOG = Logger.getInstance(DirectoryToolbarController.class);

    @FXML
    private Button addButton;
    @FXML
    private ImageView addButtonImage;

    @FXML
    private void initialize() {
        CollectionService collectionService = Context.getInstance().getCollectionService();

        Theme.buttonBlue().addListener((observable, oldValue, newValue) -> configureButton(newValue));
        configureButton(Theme.buttonBlue().get());

        addButton.setOnAction(event -> {
            collectionService.addCollection("SomeOther", 10, "/Users/shorton/imageviewtest2")
                    .observeOn(Executor.fxApplicationThread())
                    .subscribe(res -> {
                        LOG.info("Added new collection '{}', success: {}", "name", res);
                    });
        });
    }

    private void configureButton(Color color) {
        Lighting lighting = new Lighting(new Light.Distant(45, 90, color));
        ColorAdjust bright = new ColorAdjust(0, 1, 1, 1);
        lighting.setContentInput(bright);
        lighting.setSurfaceScale(0.0);
        addButton.setEffect(lighting);
    }

}
