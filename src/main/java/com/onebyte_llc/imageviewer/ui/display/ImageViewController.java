package com.onebyte_llc.imageviewer.ui.display;

import com.onebyte_llc.imageviewer.ViewNode;
import com.onebyte_llc.imageviewer.backend.ImageHandle;
import com.onebyte_llc.imageviewer.logger.Logger;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import org.controlsfx.control.GridView;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

public class ImageViewController {

    private static final Logger LOG = Logger.getInstance(ImageViewController.class);

    @FXML
    private StackPane stackPane;
    @FXML
    private Label label;
    @FXML
    private ImageView imageView;

    public static ViewNode<Node, ImageViewController> create() {
        FXMLLoader fxmlLoader = new FXMLLoader(ImageViewController.class
                .getResource("/layout/display/image-view.fxml"));
        try {
            return new ViewNode<>(fxmlLoader.load(), fxmlLoader.getController());
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load ImageGridCellController", e);
        }
    }

    static Image testImage;

    @FXML
    private void initialize() {
        label.setText(UUID.randomUUID() + "");



        try {
            if (testImage == null) {
                testImage =new Image(new FileInputStream("/Users/shorton/IMG_1248.jpeg"),
                        25, 25, true, true);
            }
            imageView.setImage(testImage);
            imageView.setCache(true);
            imageView.setCacheHint(CacheHint.SPEED);
        } catch (Exception e) {

        }

        imageView.fitWidthProperty().bind(stackPane.widthProperty());

        // Handle pinch to zoom events
        imageView.setOnZoomStarted(Event::consume);
        imageView.setOnZoom(event -> {
            imageView.setScaleX(imageView.getScaleX() * event.getZoomFactor());
            imageView.setScaleY(imageView.getScaleY() * event.getZoomFactor());
            event.consume();
        });
        imageView.setOnZoomFinished(Event::consume);

        // Handle scroll events on macos
        imageView.setOnScrollStarted(Event::consume);
        imageView.setOnScroll(event -> {
            imageView.setTranslateX(imageView.getTranslateX() - event.getDeltaX());
            imageView.setTranslateY(imageView.getTranslateY() - event.getDeltaY());
            event.consume();
        });
        imageView.setOnScrollFinished(Event::consume);
    }

    public void bindWidth(GridView<?> gridView) {
        imageView.fitWidthProperty().bind(gridView.cellWidthProperty());
        imageView.fitHeightProperty().bind(gridView.cellHeightProperty());
    }

    public void setImageData(ImageHandle imageData) {

    }

}
