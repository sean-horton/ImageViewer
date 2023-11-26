/*
 *     ImageViewer - free image viewing gui
 *     Copyright (C) 2023  Sean Horton
 *
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; version 2 of the License
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with this program; if not, write to the Free Software Foundation, Inc.,
 *     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.onebyte_llc.imageviewer.controls.scrollbar;

import com.onebyte_llc.imageviewer.controls.CanvasLayer;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;

public class ScrollBarLayer extends CanvasLayer {


    private final DoubleProperty contentHeight = new SimpleDoubleProperty();
    private final DoubleProperty contentOffset = new SimpleDoubleProperty();

    private Bounds bounds = new BoundingBox(0, 0, 0, 0);
    private MouseEvent start;
    private boolean isInGesture;
    private double offset;


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
        if (bounds.contains(new Point2D(event.getX(), event.getY()))) {
            isInGesture = true;
            start = event;
            offset = start.getY() - bounds.getMinY();
            event.consume();
        }
    }

    @Override
    public void onMouseReleased(MouseEvent event) {
        if (isInGesture) {
            event.consume();
        }
    }

    @Override
    public void onMouseDragged(MouseEvent event) {
        if (isInGesture) {
            contentOffset.setValue(contentHeight.get() * ((-event.getY() + offset) / getH()));
            event.consume();
        }
    }

    @Override
    public void onMouseClicked(MouseEvent event) {
        if (isInGesture) {
            isInGesture = false;
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

        getGraphics2D().setFill(new Color(0.9, 0.9, 0.9, 0.75));
        getGraphics2D().fillArc(xPos, scrollBarOffset, scrollBarWidth, scrollBarWidth, 0, 180, ArcType.CHORD);
        getGraphics2D().fillRect(xPos, scrollBarOffset + bubble, scrollBarWidth, scrollBarHeight);
        getGraphics2D().fillArc(xPos, scrollBarOffset + scrollBarHeight, scrollBarWidth, scrollBarWidth, 180, 180, ArcType.CHORD);

        bounds = new BoundingBox(xPos, scrollBarOffset, scrollBarWidth, scrollBarHeight + scrollBarWidth + scrollBarWidth);
    }

}
