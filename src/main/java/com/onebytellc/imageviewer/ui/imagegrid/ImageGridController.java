package com.onebytellc.imageviewer.ui.imagegrid;

import com.onebytellc.imageviewer.backend.ImageData;
import javafx.fxml.FXML;
import org.controlsfx.control.GridView;

public class ImageGridController {

    @FXML
    private GridView<ImageData> gridView;

    @FXML
    private void initialize() {
        gridView.setCellFactory(param -> new ImageGridCell());

        for (int i = 0; i < 1000; i++) {
            gridView.getItems().add(new ImageData() {
            });
        }
    }

}
