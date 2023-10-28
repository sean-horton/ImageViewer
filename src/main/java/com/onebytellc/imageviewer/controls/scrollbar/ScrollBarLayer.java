package com.onebytellc.imageviewer.controls.scrollbar;

import com.onebytellc.imageviewer.controls.CanvasLayer;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.BoundingBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;

public class ScrollBarLayer extends CanvasLayer {

    private final ScrollBarDetector scrollBarDetector = new ScrollBarDetector();

    private final DoubleProperty contentHeight = new SimpleDoubleProperty();
    private final DoubleProperty contentOffset = new SimpleDoubleProperty();

    public ScrollBarLayer() {
        // scroll bar listener
        scrollBarDetector.setOnScroll((start, current, off) ->
                contentOffset.setValue(contentHeight.get() * ((-current.getY() + off) / getH())));
    }


    //////////////////////
    // Properties
    public DoubleProperty contentHeightProperty() {
        return contentHeight;
    }

    public DoubleProperty contentOffsetProperty() {
        return contentOffset;
    }


    //////////////////////
    // User input
    @Override
    public void onMousePressed(MouseEvent event) {
        if (scrollBarDetector.onPressed(event)) {
            event.consume();
        }
    }

    @Override
    public void onMouseReleased(MouseEvent event) {
        if (scrollBarDetector.onMouseReleased(event)) {
            event.consume();
        }
    }

    @Override
    public void onMouseDragged(MouseEvent event) {
        if (scrollBarDetector.onMouseDragged(event)) {
            event.consume();
        }
    }

    @Override
    public void onMouseClicked(MouseEvent event) {
        if (scrollBarDetector.onMouseClicked(event)) {
            event.consume();
        }
    }


    //////////////////////
    // Render
    @Override
    public void draw() {

        double scrollBarRatio = getH() / contentHeight.get();
        if (scrollBarRatio >= 1) {
            // available canvas is larger than content height
            return;
        }

        // scrolling bar
        double scrollPadding = 2;
        double scrollBarWidth = 8;
        double bubble = scrollBarWidth / 2;
        double xPos = getW() - scrollBarWidth - scrollPadding;
        double scrollBarHeight = getH() * scrollBarRatio;
        double scrollBarOffset = Math.min(1, ((-contentOffset.get()) / contentHeight.get())) * (getH() - (bubble + bubble));

        // scroll bar background
//            canvas.getGraphicsContext2D().setFill(new Color(0.7, 0.7, 0.7, 0.5));
//            canvas.getGraphicsContext2D().fillRect(xPos - (scrollPadding * 2), 0, scrollBarWidth + (scrollPadding * 2), h);

        // scroll bar
        getGraphics2D().setFill(Color.BLACK);
        getGraphics2D().strokeArc(xPos, scrollBarOffset, scrollBarWidth, scrollBarWidth, 0, 180, ArcType.OPEN);
        getGraphics2D().strokeRect(xPos, scrollBarOffset + bubble, scrollBarWidth, scrollBarHeight);
        getGraphics2D().strokeArc(xPos, scrollBarOffset + scrollBarHeight, scrollBarWidth, scrollBarWidth, 180, 180, ArcType.OPEN);

        getGraphics2D().setFill(new Color(1, 1, 1, 0.75));
        getGraphics2D().fillArc(xPos, scrollBarOffset, scrollBarWidth, scrollBarWidth, 0, 180, ArcType.CHORD);
        getGraphics2D().fillRect(xPos, scrollBarOffset + bubble, scrollBarWidth, scrollBarHeight);
        getGraphics2D().fillArc(xPos, scrollBarOffset + scrollBarHeight, scrollBarWidth, scrollBarWidth, 180, 180, ArcType.CHORD);

        scrollBarDetector.setBound(new BoundingBox(xPos, scrollBarOffset, scrollBarWidth, scrollBarHeight + scrollBarWidth + scrollBarWidth));
    }

}
