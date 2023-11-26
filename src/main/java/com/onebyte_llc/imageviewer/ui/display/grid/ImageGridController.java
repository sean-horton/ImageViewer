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

package com.onebyte_llc.imageviewer.ui.display.grid;

import com.onebyte_llc.imageviewer.backend.Context;
import com.onebyte_llc.imageviewer.backend.ImageHandle;
import com.onebyte_llc.imageviewer.ui.display.GridSizeParser;
import javafx.application.Platform;
import javafx.fxml.FXML;
import org.controlsfx.control.GridView;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ImageGridController {

    @FXML
    private GridView<ImageHandle> gridView;

    @FXML
    private void initialize() {
        gridView.setCellFactory(param -> new ImageGridCell(gridView));

        // Small should be 40 across, large should be 3 across

        gridView.setHorizontalCellSpacing(0);
        gridView.setVerticalCellSpacing(0);
        gridView.setCellWidth(44);
        gridView.setCellHeight(44);

        gridView.widthProperty().addListener((observable, oldValue, newValue) -> {
            scaleGrid();
        });

        Context.getInstance().getDisplayState().gridImageScaleFactorProperty().addListener((observable, oldValue, newValue) -> {
            scaleGrid();
        });

//        for (int i = 0; i < 2000; i++) {
//            gridView.getItems().add(new ImageHandle() {
//            });
//        }
    }

    private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    Future<?> future;

    private void scaleGrid() {
        double count = GridSizeParser.horizontalCount[Context.getInstance()
                .getDisplayState().gridImageScaleFactorProperty().intValue()];

        double area = gridView.getWidth() * gridView.getHeight();
        if (area == 0) {
            return;
        }

        if (future != null) {
            future.cancel(false);
        }
        future = executor.schedule(() -> {
            Platform.runLater(() -> {
                double size = calcSize(area);

//        gridView.setCellWidth((gridView.getWidth()) / count - 10);
//        gridView.setCellHeight((gridView.getWidth()) / count - 10);

                System.out.println(area);
                gridView.setCellWidth(size);
                gridView.setCellHeight(size);
            });
        }, 500, TimeUnit.MILLISECONDS);


    }

    private double calcSize(double area) {
        double size = 10_000;

        long start = System.currentTimeMillis();
        while (size * size * 2000 > area) {
            size /= 10;
        }

        while (size * size * 2000 < area) {
            size += 1;
        }

        System.out.println("time: " + (System.currentTimeMillis() - start));

        return size;
    }

}
