package com.onebytellc.imageviewer.ui.directory;

import com.onebytellc.imageviewer.MainApplication;
import com.onebytellc.imageviewer.ViewNode;
import com.onebytellc.imageviewer.backend.Context;
import com.onebytellc.imageviewer.backend.db.jooq.tables.records.CollectionRecord;
import com.onebytellc.imageviewer.logger.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

import java.io.IOException;

public class CollectionCellController {

    private static final Logger LOG = Logger.getInstance(CollectionCellController.class);

    @FXML
    private Label label;
    @FXML
    private TextField renameTextField;

    private CollectionRecord record;

    public static ViewNode<Node, CollectionCellController> create() {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class
                .getResource("/layout/directory/collection-cell-view.fxml"));
        try {
            return new ViewNode<>(fxmlLoader.load(), fxmlLoader.getController());
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load MainController", e);
        }
    }

    @FXML
    private void initialize() {
        renameTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                saveName();
            }
        });
        renameTextField.setOnKeyPressed(key -> {
            if (key.getCode() == KeyCode.ENTER) {
                saveName();
            }
        });
    }

    public void setValue(CollectionRecord value) {
        this.record = value;
        label.setText(value.getName());
    }

    public void renameMode() {
        renameTextField.setManaged(true);
        renameTextField.setVisible(true);
        renameTextField.setFocusTraversable(true);

        label.setManaged(false);
        label.setVisible(false);

        renameTextField.setText(label.getText());
        renameTextField.requestFocus();
    }

    private void saveName() {
        LOG.info("Renaming collection '{}' to '{}'", label.getText(), renameTextField.getText());

        record.setName(renameTextField.getText());
        Context.getInstance().getCollectionService().updateCollection(record);

        renameTextField.setManaged(false);
        renameTextField.setVisible(false);
        renameTextField.setFocusTraversable(false);

        label.setManaged(true);
        label.setVisible(true);
    }

}
