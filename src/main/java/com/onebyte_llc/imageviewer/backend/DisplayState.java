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

package com.onebyte_llc.imageviewer.backend;

import com.onebyte_llc.imageviewer.controls.gridview.ImageRenderMode;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;

public class DisplayState {

    // Image size is calculated from (gridBaseImageSize * scaleFactor)
    private final DoubleProperty gridBaseImageSize = new SimpleDoubleProperty();
    private final DoubleProperty gridMinScaleFactor = new SimpleDoubleProperty();
    private final DoubleProperty gridMaxScaleFactor = new SimpleDoubleProperty();
    private final DoubleProperty gridImageScaleFactor = new SimpleDoubleProperty();

    private final DoubleProperty fullScreenScaleFactor = new SimpleDoubleProperty(1);
    private final DoubleProperty fullScreeMinScaleFactor = new SimpleDoubleProperty(1);
    private final DoubleProperty fullScreeMaxScaleFactor = new SimpleDoubleProperty(5);

    private final ObjectProperty<ImageRenderMode> imageRenderMode = new SimpleObjectProperty<>();
    private final ObjectProperty<ImageHandle> fullScreenImage = new SimpleObjectProperty<>();
    private final ObjectProperty<Boolean> isSlideshow = new SimpleObjectProperty<>();
    private final LongProperty slideshowDurationMs = new SimpleLongProperty();

    public DisplayState() {
        gridBaseImageSize.setValue(50);
        gridMinScaleFactor.setValue(0.5);
        gridMaxScaleFactor.setValue(10);
        gridImageScaleFactor.setValue(1);
        imageRenderMode.setValue(ImageRenderMode.FIT);
        isSlideshow.setValue(false);
        slideshowDurationMs.setValue(4000);
    }

    public DoubleProperty gridBaseImageSizeProperty() {
        return gridBaseImageSize;
    }

    public DoubleProperty gridMinScaleFactorProperty() {
        return gridMinScaleFactor;
    }

    public DoubleProperty gridMaxScaleFactorProperty() {
        return gridMaxScaleFactor;
    }

    public DoubleProperty gridImageScaleFactorProperty() {
        return gridImageScaleFactor;
    }

    public ObjectProperty<ImageRenderMode> imageRenderModeProperty() {
        return imageRenderMode;
    }

    public ObjectProperty<ImageHandle> fullScreenImageProperty() {
        return fullScreenImage;
    }

    public DoubleProperty fullScreenScaleFactorProperty() {
        return fullScreenScaleFactor;
    }

    public DoubleProperty fullScreeMinScaleFactorProperty() {
        return fullScreeMinScaleFactor;
    }

    public DoubleProperty fullScreeMaxScaleFactorProperty() {
        return fullScreeMaxScaleFactor;
    }

    public ObjectProperty<Boolean> isSlideshowProperty() {
        return isSlideshow;
    }

    public LongProperty slideshowDurationMsProperty() {
        return slideshowDurationMs;
    }
}
