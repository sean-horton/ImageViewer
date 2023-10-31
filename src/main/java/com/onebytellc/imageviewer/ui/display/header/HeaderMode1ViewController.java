package com.onebytellc.imageviewer.ui.display.header;

import com.onebytellc.imageviewer.MainApplication;
import com.onebytellc.imageviewer.Theme;
import com.onebytellc.imageviewer.ViewNode;
import com.onebytellc.imageviewer.backend.Context;
import com.onebytellc.imageviewer.backend.DisplayState;
import com.onebytellc.imageviewer.backend.ImageHandle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

/**
 * Shown when an image is selected and in full screen
 */
public class HeaderMode1ViewController {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("LLLL d, yyyy 'at' h:m:s a");

    @FXML
    private Slider scaleSlider;
    @FXML
    private Label timeLabel;
    @FXML
    private Label posLabel;
    @FXML
    private Button backButton;
    @FXML
    private ImageView backButtonImage;

    public static ViewNode<Node, HeaderMode1ViewController> create(ImageHandle imageHandle) {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class
                .getResource("/layout/display/header/header-mode1-view.fxml"));
        try {
            Node node = fxmlLoader.load();
            HeaderMode1ViewController controller = fxmlLoader.getController();
            controller.init(imageHandle);
            return new ViewNode<>(node, controller);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load HeaderMode1ViewController", e);
        }
    }


    @FXML
    private void initialize() {
        DisplayState state = Context.getInstance().getDisplayState();
        backButtonImage.effectProperty().bind(Theme.buttonToolbarEffect());
        backButton.setOnAction(event -> state.fullScreenImageProperty().setValue(null));
    }

    private void init(ImageHandle imageHandle) {
        if (imageHandle.getImOriginalDate() == null) {
            timeLabel.setText("N/A");
        } else {
            timeLabel.setText(imageHandle.getImOriginalDate().format(FORMATTER));
        }
    }

}
