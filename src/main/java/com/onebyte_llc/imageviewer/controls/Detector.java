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
