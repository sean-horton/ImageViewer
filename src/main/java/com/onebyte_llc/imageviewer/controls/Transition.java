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

package com.onebyte_llc.imageviewer.controls;

import javafx.scene.canvas.Canvas;

public interface Transition {

    void onAttach(CanvasView canvasView);

    void onTransitionComplete(CanvasView canvasView);

    void tick(Canvas canvas, double percentComplete);

    static Transition leftToRight() {
        return null;
    }

    static Transition rightToLeft() {
        return null;
    }

    static Transition zoomExpand() {
        return null;
    }

    static Transition zoomCollapse() {
        return null;
    }

}
