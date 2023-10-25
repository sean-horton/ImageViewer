package com.onebytellc.imageviewer.ui.display.grid;

import com.onebytellc.imageviewer.backend.Context;
import com.onebytellc.imageviewer.backend.ImageHandle;
import com.onebytellc.imageviewer.ui.display.GridSizeParser;
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
