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

import javafx.geometry.Bounds;
import javafx.scene.input.MouseEvent;

/**
 * Order for mouse click is
 * <ul>
 *     <li>pressed</li>
 *     <li>released</li>
 *     <li>clicked</li>
 * </ul>
 */
public interface Detector {

    void setBound(Bounds bounds);

    boolean onPressed(MouseEvent event);

    boolean onMouseReleased(MouseEvent event);

    boolean onMouseDragged(MouseEvent event);

    boolean onMouseClicked(MouseEvent event);

}
