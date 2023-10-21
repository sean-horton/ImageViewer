package com.onebytellc.imageviewer.controls;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.List;

public class GridView<T extends GridItem> extends AnchorPane {

    private static final int MAX_IMAGES_ON_SCREEN = 2000;

    private final DoubleProperty baseImageSize = new SimpleDoubleProperty(100);
    private final DoubleProperty minScaleFactor = new SimpleDoubleProperty(1);
    private final DoubleProperty maxScaleFactor = new SimpleDoubleProperty(1);
    private final DoubleProperty scaleFactor = new SimpleDoubleProperty();

    // state
    private final Canvas canvas;
    private ObservableList<T> items = FXCollections.observableArrayList();
    private List<DisplayBounds> bounds = new ArrayList<>();
    private ScrollBarDetector scrollBarDetector = new ScrollBarDetector();

    public GridView() {
        canvas = new Canvas();

        // redraw on value changes
        baseImageSize.addListener((observable, oldValue, newValue) -> redraw());
        minScaleFactor.addListener((observable, oldValue, newValue) -> redraw());
        maxScaleFactor.addListener((observable, oldValue, newValue) -> redraw());
        scaleFactor.addListener((observable, oldValue, newValue) -> redraw());

        // allow anchor pane to resize
        setMinWidth(0);
        setMinHeight(0);

        // add the canvas to this AnchorPane
        AnchorPane.setTopAnchor(canvas, 0.0);
        AnchorPane.setRightAnchor(canvas, 0.0);
        AnchorPane.setBottomAnchor(canvas, 0.0);
        AnchorPane.setLeftAnchor(canvas, 0.0);
        getChildren().add(canvas);

        // bind canvas size to this anchor pane
        canvas.widthProperty().bind(widthProperty());
        canvas.heightProperty().bind(heightProperty());

        // redraw when the canvas is resized
        canvas.heightProperty().addListener((observable, oldValue, newValue) -> redraw());
        canvas.widthProperty().addListener((observable, oldValue, newValue) -> redraw());

        // Redraw when items change
        items.addListener((ListChangeListener<GridItem>) c -> redraw());

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
            double ratio = scaleFactor.get();
            ratio *= event.getZoomFactor();
            ratio = Math.max(minScaleFactor.get(), Math.min(maxScaleFactor.get(), ratio));
            scaleFactor.setValue(ratio);
            redraw();
            event.consume();
        });
        canvas.setOnZoomFinished(Event::consume);

        // click listener
        canvas.setOnMouseClicked(event -> {
            if (scrollBarDetector.onMouseClicked(event)) {
                event.consume();
                return;
            }

            for (DisplayBounds bound : bounds) {
                if (bound.bounds.contains(new Point2D(event.getX(), event.getY()))) {
                    bound.action.run();
                }
            }
            event.consume();
        });

        canvas.setOnMousePressed(event -> {
            if (scrollBarDetector.onPressed(event)) {
                event.consume();
                return;
            }
        });

        canvas.setOnMouseReleased(event -> {
            if (scrollBarDetector.onMouseReleased(event)) {
                redraw();
                event.consume();
                return;
            }
        });

        canvas.setOnMouseDragged(event -> {
            if (scrollBarDetector.onMouseDragged(event)) {
                event.consume();
                return;
            }
        });

        // scroll bar listener
        scrollBarDetector.setOnScroll((start, current, off) -> {
            offset = allContentHeight() * ((-current.getY() + off) / getHeight());
            redraw();
        });
    }


    ////////////////////
    // Public
    public ObservableList<T> getItems() {
        return items;
    }

    public DoubleProperty baseImageSizeProperty() {
        return baseImageSize;
    }

    public DoubleProperty minScaleFactorProperty() {
        return minScaleFactor;
    }

    public DoubleProperty maxScaleFactorProperty() {
        return maxScaleFactor;
    }

    public DoubleProperty scaleFactorProperty() {
        return scaleFactor;
    }

    ////////////////////
    // Private
    private double allContentHeight() {
        double w = canvas.getWidth();
        double h = canvas.getHeight();
        double[] size = calcSize(w, h);
        double sizeW = size[0];
        double sizeH = size[1];
        int columns = (int) (w / sizeW);
        double allContentHeight = Math.ceil((items.size() / (double) columns)) * sizeH;
        return allContentHeight;
    }

    private void redraw() {
        bounds.clear();

        double w = canvas.getWidth();
        double h = canvas.getHeight();

        double[] size = calcSize(w, h);
        double sizeW = size[0];
        double sizeH = size[1];

        double offX = 0;
        double offY = (int) this.offset;

        // TODO - implement rubber banding
        // limit scroll bounds
        int columns = (int) (w / sizeW);
        double allContentHeight = Math.ceil((items.size() / (double) columns)) * sizeH;

        int minScroll = (int) (-allContentHeight + h);
        if (allContentHeight <= h) {
            // we have less content than can fill the screen
            offY = 0; //(int) -(h - allContentHeight);
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
        canvas.getGraphicsContext2D().setFill(Color.GRAY);
        canvas.getGraphicsContext2D().fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // draw each image
        for (int i = 0; i < items.size(); i++) {
            // TODO - if there are millions of items it would be faster to use
            //  binary search to find first row that needs to be painted

            GridItem item = items.get(i);

            boolean draw = true;
            if (offX + sizeW < 0 || offY + sizeH < 0) draw = false;
            if (offX > canvas.getWidth() || offY > canvas.getHeight()) break; // no need to look further

            if (draw) {
                double finalOffX = offX;
                double finalOffY = offY;
                bounds.add(new DisplayBounds(new BoundingBox(offX, offY, sizeW, sizeH), () -> {
                    canvas.getGraphicsContext2D().setFill(Color.RED);
                    canvas.getGraphicsContext2D().fillRect(finalOffX, finalOffY, sizeW, sizeH);
                }));
                item.draw(canvas, offX, offY, sizeW, sizeH);
            }

            offX += sizeW;
            if (offX + sizeW > w) {
                offX = 0;
                offY += sizeH;
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
            double scrollBarWidth = 8;
            double bubble = scrollBarWidth / 2;
            double xPos = w - scrollBarWidth - scrollPadding;
            double scrollBarHeight = h * scrollBarRatio;
            double scrollBarOffset = Math.min(1, ((-this.offset) / allContentHeight)) * (h - (bubble + bubble));

            // scroll bar background
//            canvas.getGraphicsContext2D().setFill(new Color(0.7, 0.7, 0.7, 0.5));
//            canvas.getGraphicsContext2D().fillRect(xPos - (scrollPadding * 2), 0, scrollBarWidth + (scrollPadding * 2), h);

            // scroll bar
            canvas.getGraphicsContext2D().setFill(Color.BLACK);
            canvas.getGraphicsContext2D().strokeArc(xPos, scrollBarOffset, scrollBarWidth, scrollBarWidth, 0, 180, ArcType.OPEN);
            canvas.getGraphicsContext2D().strokeRect(xPos, scrollBarOffset + bubble, scrollBarWidth, scrollBarHeight);
            canvas.getGraphicsContext2D().strokeArc(xPos, scrollBarOffset + scrollBarHeight, scrollBarWidth, scrollBarWidth, 180, 180, ArcType.OPEN);

            canvas.getGraphicsContext2D().setFill(new Color(1, 1, 1, 0.75));
            canvas.getGraphicsContext2D().fillArc(xPos, scrollBarOffset, scrollBarWidth, scrollBarWidth, 0, 180, ArcType.CHORD);
            canvas.getGraphicsContext2D().fillRect(xPos, scrollBarOffset + bubble, scrollBarWidth, scrollBarHeight);
            canvas.getGraphicsContext2D().fillArc(xPos, scrollBarOffset + scrollBarHeight, scrollBarWidth, scrollBarWidth, 180, 180, ArcType.CHORD);

            scrollBarDetector.setBound(new BoundingBox(xPos, scrollBarOffset, scrollBarWidth, scrollBarHeight + scrollBarWidth + scrollBarWidth));
        }
    }

    double offset = 0;

    private double[] calcSize(double w, double h) {
        double area = w * h;
        double size = 10_000;

        while (size * size * MAX_IMAGES_ON_SCREEN > area) {
            size /= 10;
        }

        while (size * size * MAX_IMAGES_ON_SCREEN < area) {
            size += 1;
        }

        size--;
        double prefSize = Math.max(baseImageSize.get() * scaleFactor.get(), size);
        double count = (int) (w / prefSize);

        return new double[]{(w / count) - 0.01, prefSize};
    }

    private static class DisplayBounds {
        private Bounds bounds;
        private Runnable action;

        public DisplayBounds(Bounds bounds, Runnable action) {
            this.bounds = bounds;
            this.action = action;
        }


    }


}
