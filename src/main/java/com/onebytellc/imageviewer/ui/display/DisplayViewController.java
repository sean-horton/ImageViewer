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

    private ViewNode<Node, HeaderMode0ViewController> mode0;
    private ViewNode<Node, HeaderMode1ViewController> mode1;

    @FXML
    private void initialize() {
        DisplayState state = Context.getInstance().getDisplayState();

        mode0 = HeaderMode0ViewController.create();
        mode1 = HeaderMode1ViewController.create();

        toolbarVbox.getChildren().add(mode0.getNode());
        state.fullScreenImageProperty().addListener((observable, oldValue, newValue) -> {
            toolbarVbox.getChildren().clear();
            if (newValue == null) {
                toolbarVbox.getChildren().add(mode0.getNode());
            } else {
                toolbarVbox.getChildren().add(mode1.getNode());
            }
        });
    }

}
