package com.onebytellc.imageviewer.ui.display.header;

import com.onebytellc.imageviewer.backend.Context;
import com.onebytellc.imageviewer.ui.display.GridSizeParser;
import javafx.fxml.FXML;
import javafx.scene.control.Slider;

public class HeaderViewController {

    @FXML
    private Slider scaleSlider;

    @FXML
    private void initialize() {
        Context context = Context.getInstance();

        scaleSlider.setMajorTickUnit(1);
        scaleSlider.setMinorTickCount(1);
        scaleSlider.setSnapToTicks(true);
        scaleSlider.setMin(0);
        scaleSlider.setMax(GridSizeParser.horizontalCount.length - 1);
        scaleSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            context.getDisplayState().cellSizeProperty().set(newValue.doubleValue());
        });
    }

}
