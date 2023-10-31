package com.onebytellc.imageviewer.ui.display;

import com.onebytellc.imageviewer.ViewNode;
import com.onebytellc.imageviewer.backend.Context;
import com.onebytellc.imageviewer.backend.DisplayState;
import com.onebytellc.imageviewer.ui.display.header.HeaderMode0ViewController;
import com.onebytellc.imageviewer.ui.display.header.HeaderMode1ViewController;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.VBox;

public class DisplayViewController {

    @FXML
    private VBox toolbarVbox;

    @FXML
    private void initialize() {
        DisplayState state = Context.getInstance().getDisplayState();

        state.fullScreenImageProperty().addListener((observable, oldValue, newValue) -> {
            toolbarVbox.getChildren().clear();
            if (newValue == null) {
                ViewNode<Node, HeaderMode0ViewController> n = HeaderMode0ViewController.create();
                toolbarVbox.getChildren().add(n.getNode());
            } else {
                ViewNode<Node, HeaderMode1ViewController> n = HeaderMode1ViewController.create(newValue);
                toolbarVbox.getChildren().add(n.getNode());
            }
        });
    }

}
