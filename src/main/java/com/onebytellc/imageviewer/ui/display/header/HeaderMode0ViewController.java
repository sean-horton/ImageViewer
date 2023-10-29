package com.onebytellc.imageviewer.ui.display.header;

import com.onebytellc.imageviewer.backend.Context;
import com.onebytellc.imageviewer.backend.DisplayState;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Slider;

public class HeaderMode0ViewController {

    @FXML
    private Slider scaleSlider;

    @FXML
    private void initialize() {
        Context context = Context.getInstance();
        DisplayState state = context.getDisplayState();

        Bindings.bindBidirectional(scaleSlider.valueProperty(), state.gridImageScaleFactorProperty());
        scaleSlider.minProperty().bind(state.gridMinScaleFactorProperty());
        scaleSlider.maxProperty().bind(state.gridMaxScaleFactorProperty());
    }

}
