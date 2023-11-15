package com.onebyte_llc.imageviewer.ui.directory;

import com.onebyte_llc.imageviewer.I18N;
import com.onebyte_llc.imageviewer.Theme;
import com.onebyte_llc.imageviewer.backend.CollectionService;
import com.onebyte_llc.imageviewer.backend.Context;
import com.onebyte_llc.imageviewer.logger.Logger;
import com.onebyte_llc.imageviewer.reactive.Executor;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.stage.DirectoryChooser;

import java.io.File;


public class DirectoryToolbarController {

    private static final Logger LOG = Logger.getInstance(DirectoryToolbarController.class);

    @FXML
    private Button addButton;
    @FXML
    private ImageView addButtonImage;

    @FXML
    private void initialize() {
        CollectionService collectionService = Context.getInstance().getCollectionService();

        addButton.effectProperty().bind(Theme.buttonBlueEffect());
        addButton.setOnAction(event -> {
            String defaultCollectionName = I18N.get("default.new.collection.name").get();
            DirectoryChooser directoryChooser = new DirectoryChooser();
            File selectedDirectory = directoryChooser.showDialog(addButton.getScene().getWindow());
            if (selectedDirectory == null) {
                return;
            }

            collectionService.addCollection(defaultCollectionName, 10, selectedDirectory.getAbsolutePath())
                    .observeOn(Executor.fxApplicationThread())
                    .subscribe(res -> {
                        LOG.info("Added new collection '{}', success: {}", "name", res);
                    });
        });
    }

}
