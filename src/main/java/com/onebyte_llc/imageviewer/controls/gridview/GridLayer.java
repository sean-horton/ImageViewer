package com.onebyte_llc.imageviewer.controls.gridview;

import com.onebyte_llc.imageviewer.controls.CanvasLayer;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.ZoomEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class GridLayer<T> extends CanvasLayer {

    private static final int MAX_IMAGES_ON_SCREEN = 2000;

    private final DoubleProperty baseImageSize = new SimpleDoubleProperty(100);
    private final DoubleProperty minScaleFactor = new SimpleDoubleProperty(1);
    private final DoubleProperty maxScaleFactor = new SimpleDoubleProperty(1);
    private final DoubleProperty scaleFactor = new SimpleDoubleProperty();

    // Total height of content AND offset within total height
    private final DoubleProperty contentHeight = new SimpleDoubleProperty();
    private final DoubleProperty contentOffset = new SimpleDoubleProperty();
    private final ObjectProperty<Color> backgroundColor = new SimpleObjectProperty<>();

    // state
    private final ObservableList<T> items = FXCollections.observableArrayList();
    private final List<DisplayBounds<T>> cellBounds = new ArrayList<>();
    private GridCellRenderer<T> gridCellRenderer;
    private GridCellClickedListener<T> gridCellClickedListener;

    public GridLayer() {
        // Redraw when items change
        items.addListener((ListChangeListener<T>) c -> invalidate());

        // redraw on value changes
        baseImageSize.addListener((observable, oldValue, newValue) -> invalidate());
        minScaleFactor.addListener((observable, oldValue, newValue) -> invalidate());
        maxScaleFactor.addListener((observable, oldValue, newValue) -> invalidate());
        scaleFactor.addListener((observable, oldValue, newValue) -> invalidate());
    }


    //////////////////////
    // User input
    @Override
    public void onMouseClicked(MouseEvent event) {
        for (DisplayBounds<T> bound : cellBounds) {
            if (bound.bounds.contains(new Point2D(event.getX(), event.getY()))) {
                bound.action.run();
                if (gridCellClickedListener != null) {
                    gridCellClickedListener.onClicked(bound.item, bound.bounds);
                }
                event.consume();
                return;
            }
        }
    }

    @Override
    public void onZoomStarted(ZoomEvent event) {
        event.consume();
    }

    @Override
    public void onZoom(ZoomEvent event) {
        double ratio = scaleFactor.get();
        ratio *= event.getZoomFactor();
        ratio = Math.max(minScaleFactor.get(), Math.min(maxScaleFactor.get(), ratio));
        scaleFactor.setValue(ratio);
        event.consume();
    }

    @Override
    public void onZoomFinished(ZoomEvent event) {
        event.consume();
    }


    @Override
    public void onScrollStarted(ScrollEvent event) {
        event.consume();
    }

    @Override
    public void onScroll(ScrollEvent event) {
        contentOffset.setValue(contentOffset.get() + event.getDeltaY());
        event.consume();
    }

    @Override
    public void onScrollFinished(ScrollEvent event) {
        event.consume();
    }


    //////////////////////
    // Properties
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

    public DoubleProperty contentHeightProperty() {
        return contentHeight;
    }

    public DoubleProperty contentOffsetProperty() {
        return contentOffset;
    }

    public ObjectProperty<Color> backgroundColorProperty() {
        return backgroundColor;
    }

    public void setGridCellRenderer(GridCellRenderer<T> cellFactory) {
        this.gridCellRenderer = cellFactory;
    }

    public void setOnCellClicked(GridCellClickedListener<T> listener) {
        gridCellClickedListener = listener;
    }


    ////////////////////
    // Render
    @Override
    public void draw() {
        cellBounds.clear();

        double w = getW();
        double h = getH();

        double[] size = calcSize(w, h);
        double sizeW = size[0];
        double sizeH = size[1];

        double offX = 0;
        double offY = (int) this.contentOffset.get();

        // TODO - implement rubber banding
        // limit scroll bounds
        int columns = (int) (w / sizeW);
        contentHeight.setValue(Math.ceil((items.size() / (double) columns)) * sizeH);
        int totalCellCount = (int) (Math.ceil(getH() / sizeH) * columns);

        int minScroll = (int) (-contentHeight.get() + h);
        if (contentHeight.get() <= h) {
            // we have less content than can fill the screen
            offY = 0; //(int) -(h - allContentHeight);
            contentOffset.setValue(0);
        } else {
            if (offY > 0) {
                // beyond top of list
                offY = 0;
                contentOffset.setValue(0);
            }
            if (offY < minScroll) {
                // beyond bottom of list
                offY = minScroll;
                contentOffset.setValue(offY);
            }
        }

        // clear the canvas for redraw
        getGraphics2D().clearRect(0, 0, getW(), getH());
        getGraphics2D().setFill(backgroundColor.get());
        getGraphics2D().fillRect(0, 0, getW(), getH());

        // draw each image
        LocalDateTime min = null;
        LocalDateTime max = null;
        for (int i = 0; i < items.size(); i++) {
            // TODO - if there are millions of items it would be faster to use
            //  binary search to find first row that needs to be painted

            boolean draw = true;
            if (offX + sizeW < 0 || offY + sizeH < 0) draw = false;
            if (offX > getW() || offY > getH()) break; // no need to look further

            if (draw) {
                double finalOffX = offX;
                double finalOffY = offY;
                cellBounds.add(new DisplayBounds<>(items.get(i), new BoundingBox(offX, offY, sizeW, sizeH), () -> {
                    getGraphics2D().setFill(Color.RED);
                    getGraphics2D().fillRect(finalOffX, finalOffY, sizeW, sizeH);
                }));
                gridCellRenderer.draw(items.get(i), getGraphics2D(), totalCellCount, offX, offY, sizeW, sizeH);
            }

            LocalDateTime nextDate = gridCellRenderer.getDate(items.get(i));
            if (min == null || (nextDate != null && nextDate.isBefore(min))) {
                min = nextDate;
            }
            if (max == null || (nextDate != null && nextDate.isAfter(max))) {
                max = nextDate;
            }

            offX += sizeW;
            if (offX + sizeW > w) {
                offX = 0;

                // draw text
                if (min != null) {
                    // TODO - this probably should be done in a separate layer
                    //   and it should only draw text every few rows
                    final String msg = min.getYear() + "-" + max.getYear();
                    final Text text = new Text(msg);
                    Font font = Font.font("SF Pro", FontWeight.BOLD, 24);
                    text.setFont(font);
                    final double textW = text.getLayoutBounds().getWidth();
                    final double textH = text.getLayoutBounds().getHeight();

                    getGraphics2D().setFill(Color.rgb(33, 33, 33, 0.5));
                    getGraphics2D().fillRoundRect(25 - 3, offY + 32 - textH + 6, textW + 6, textH, 8, 8);

                    getGraphics2D().setFill(Color.WHITE);
                    getGraphics2D().setFont(font);
                    getGraphics2D().fillText(msg, 25, offY + 32);

                    min = null;
                    max = null;
                }

                offY += sizeH;
            }
        }


    }

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

    private static class DisplayBounds<T> {

        private T item;
        private Bounds bounds;
        private Runnable action;

        public DisplayBounds(T item, Bounds bounds, Runnable action) {
            this.item = item;
            this.bounds = bounds;
            this.action = action;
        }
    }

}
