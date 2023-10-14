package com.onebytellc.imageviewer.ui.imagegrid;

import com.onebytellc.imageviewer.ViewNode;
import com.onebytellc.imageviewer.backend.ImageData;
import com.onebytellc.imageviewer.logger.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;

import java.io.IOException;
import java.util.UUID;

public class ImageGridCellController {

    private static final Logger LOG = Logger.getInstance(ImageGridCellController.class);


    @FXML
    private Label label;

    public static ViewNode<Node, ImageGridCellController> create() {
        FXMLLoader fxmlLoader = new FXMLLoader(ImageGridCellController.class
                .getResource("/layout/image-grid/image-grid-cell.fxml"));
        try {
            return new ViewNode<>(fxmlLoader.load(), fxmlLoader.getController());
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load ImageGridCellController", e);
        }
    }

    @FXML
    private void initialize() {
        label.setText(UUID.randomUUID() + "");
    }

    public void setImageData(ImageData imageData) {

    }

}
