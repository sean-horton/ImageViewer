package com.onebytellc.imageviewer.ui;

import com.onebytellc.imageviewer.MainApplication;
import com.onebytellc.imageviewer.ViewNode;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;

public class MainController {


    public static ViewNode<Parent, MainController> create() {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class
                .getResource("/layout/main-view.fxml"));
        try {
            return new ViewNode<>(fxmlLoader.load(), fxmlLoader.getController());
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load MainController", e);
        }
    }

}