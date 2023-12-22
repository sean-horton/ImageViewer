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

import com.onebyte_llc.imageviewer.I18N;
import com.onebyte_llc.imageviewer.backend.ImageHandle;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.time.LocalDateTime;
import java.time.format.TextStyle;

public class LabelLayer extends CanvasLayer {

    private final ObjectProperty<ImageHandle> image = new SimpleObjectProperty<>();

    public ObjectProperty<ImageHandle> imageProperty() {
        return image;
    }

    @Override
    public void draw() {
        ImageHandle handle = image.get();
        if (handle == null) {
            return;
        }

        LocalDateTime time = handle.getImOriginalDate();
        if (time == null) {
            return;
        }

        String msg = time.getMonth().getDisplayName(TextStyle.SHORT, I18N.getLocale()) + " " + time.getDayOfMonth() + ", " + time.getYear();
        final Text text = new Text(msg);
        Font font = Font.font("SF Pro", FontWeight.BOLD, 32);
        text.setFont(font);
        final double textW = text.getLayoutBounds().getWidth();
        final double textH = text.getLayoutBounds().getHeight();

        double x = getW() - textW - 32;
        double y = getH() - textH - 32;

        getGraphics2D().setFill(Color.rgb(33, 33, 33, 0.5));
        getGraphics2D().fillRoundRect(x - 3, y - textH + 6, textW + 6, textH, 8, 8);

        getGraphics2D().setFill(Color.WHITE);
        getGraphics2D().setFont(font);
        getGraphics2D().fillText(msg, x, y);
    }

}
