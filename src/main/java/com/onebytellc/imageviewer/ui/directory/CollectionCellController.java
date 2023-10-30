package com.onebytellc.imageviewer.ui.directory;

import com.onebytellc.imageviewer.MainApplication;
import com.onebytellc.imageviewer.ViewNode;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;

import java.io.IOException;

public class CollectionCellController {

    @FXML
    private Label label;

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

    }

    public void setValue(String value) {
        label.setText(value);
    }

}
