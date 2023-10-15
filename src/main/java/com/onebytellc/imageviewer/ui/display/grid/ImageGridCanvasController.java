package com.onebytellc.imageviewer.ui.display.grid;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.text.Font;

import java.io.FileInputStream;

/**
 * This is the image grid that can display 1,000's of images at
 * once with fast scrolling and zooming. This was fun to make.
 */
public class ImageGridCanvasController {

    @FXML
    private AnchorPane anchor;
    @FXML
    private Canvas canvas;


    // Image size is calculated from (DEFAULT_IMAGE_SIZE * scaleFactor)
    private static final double DEFAULT_IMAGE_SIZE = 50;
    private static final double MAX_SCALE_FACTOR = 10;
    private static final double MIN_SCALE_FACTOR = 0.5;
    private static final int MAX_IMAGES_ON_SCREEN = 2000;


    // state
    private double scaleFactor = 1;


    // TODO - remove these
    static Image testImage;
    static int imageCount = 15000;


    @FXML
    private void initialize() {
        try {
            testImage = new Image(new FileInputStream("/Users/shorton/IMG_1248.jpeg"),
                    400, 400, true, true);
        } catch (Exception e) {

        }

        // resize the canvas to parent anchor
        anchor.setMinWidth(0);
        anchor.setMinHeight(0);
        canvas.widthProperty().bind(anchor.widthProperty());
        canvas.heightProperty().bind(anchor.heightProperty());

        // redraw when the canvas is resized
        canvas.heightProperty().addListener((observable, oldValue, newValue) -> redraw());
        canvas.widthProperty().addListener((observable, oldValue, newValue) -> redraw());

        // scroll listeners
        canvas.setOnScrollStarted(Event::consume);
        canvas.setOnScroll(event -> {
            offset += event.getDeltaY();
            redraw();
            event.consume();
        });
        canvas.setOnScrollFinished(Event::consume);

        // pinch to zoom listener
        canvas.setOnZoomStarted(Event::consume);
        canvas.setOnZoom(event -> {
            scaleFactor *= event.getZoomFactor();
            scaleFactor = Math.max(MIN_SCALE_FACTOR, Math.min(MAX_SCALE_FACTOR, scaleFactor));
            redraw();
            event.consume();
        });
        canvas.setOnZoomFinished(Event::consume);
    }

    private void redraw() {
        double w = canvas.getWidth();
        double h = canvas.getHeight();

        double size = calcSize(w, h);

        int offX = 0;
        int offY = (int) this.offset;

        // TODO - implement rubber banding
        // limit scroll bounds
        int columns = (int) (w / size);
        double allContentHeight = Math.ceil((imageCount / (double) columns)) * size;

        int minScroll = (int) (-allContentHeight + h);
        if (allContentHeight <= h) {
            // we have less content than can fill the screen
            offY = (int) -(h - allContentHeight);
            offset = 0;
        } else {
            if (offY > 0) {
                // beyond top of list
                offY = 0;
                offset = 0;
            }
            if (offY < minScroll) {
                // beyond bottom of list
                offY = minScroll;
                offset = offY;
            }
        }

        // clear the canvas for redraw
        canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // draw each image
        for (int i = 0; i < imageCount; i++) {

            // TODO - need to preserve ratio for size
            testImage.getWidth();
            testImage.getHeight();

            boolean draw = true;
            if (offX + size < 0 || offY + size < 0) draw = false;
            if (offX > canvas.getWidth() || offY > canvas.getHeight()) draw = false;

            if (draw) {
                canvas.getGraphicsContext2D().drawImage(testImage, offX, offY, size, size);
            }

            offX += size;
            if (offX + size > w) {
                offX = 0;
                offY += size;
            }
        }

        // draw text
        canvas.getGraphicsContext2D().setFill(Color.BLACK);
        canvas.getGraphicsContext2D().setFont(Font.font(100));
        canvas.getGraphicsContext2D().fillText("test 123", 150, this.offset + 150);

        // draw scroll bar
        double scrollBarRatio = h / allContentHeight;
        if (scrollBarRatio < 1) {
            // scrolling bar
            double scrollPadding = 2;
            double scrollBarWidth = 12;
            double bubble = scrollBarWidth / 2;
            double xPos = w - scrollBarWidth - scrollPadding;
            double scrollBarHeight = h * scrollBarRatio;
            double scrollBarOffset = Math.min(1, ((-this.offset) / allContentHeight)) * (h - (bubble + bubble));

            // scroll bar background
//            canvas.getGraphicsContext2D().setFill(new Color(0.7, 0.7, 0.7, 0.5));
//            canvas.getGraphicsContext2D().fillRect(xPos - (scrollPadding * 2), 0, scrollBarWidth + (scrollPadding * 2), h);

            // scroll bar
            canvas.getGraphicsContext2D().setFill(new Color(1, 1, 1, 0.5));
            canvas.getGraphicsContext2D().fillArc(xPos, scrollBarOffset, scrollBarWidth, scrollBarWidth, 0, 180, ArcType.ROUND);
            canvas.getGraphicsContext2D().fillRect(xPos, scrollBarOffset + bubble, scrollBarWidth, scrollBarHeight);
            canvas.getGraphicsContext2D().fillArc(xPos, scrollBarOffset + scrollBarHeight, scrollBarWidth, scrollBarWidth, 180, 180, ArcType.ROUND);

            canvas.getGraphicsContext2D().setFill(Color.BLACK);
            canvas.getGraphicsContext2D().strokeArc(xPos, scrollBarOffset, scrollBarWidth, scrollBarWidth, 0, 180, ArcType.CHORD);
            canvas.getGraphicsContext2D().strokeRect(xPos, scrollBarOffset + bubble, scrollBarWidth, scrollBarHeight);
            canvas.getGraphicsContext2D().strokeArc(xPos, scrollBarOffset + scrollBarHeight, scrollBarWidth, scrollBarWidth, 180, 180, ArcType.CHORD);
        }
    }

    double offset = 0;

    private double calcSize(double w, double h) {
        double area = w * h;
        double size = 10_000;

        while (size * size * MAX_IMAGES_ON_SCREEN > area) {
            size /= 10;
        }

        while (size * size * MAX_IMAGES_ON_SCREEN < area) {
            size += 1;
        }

        return Math.max(DEFAULT_IMAGE_SIZE * scaleFactor, size);
    }

}
