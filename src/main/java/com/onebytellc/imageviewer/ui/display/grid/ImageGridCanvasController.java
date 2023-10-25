package com.onebytellc.imageviewer.ui.display.grid;

import com.onebytellc.imageviewer.backend.ChangeSet;
import com.onebytellc.imageviewer.backend.CollectionService;
import com.onebytellc.imageviewer.backend.Context;
import com.onebytellc.imageviewer.backend.DisplayState;
import com.onebytellc.imageviewer.backend.ImageHandle;
import com.onebytellc.imageviewer.controls.GridView;
import com.onebytellc.imageviewer.controls.ImageGridCell;
import com.onebytellc.imageviewer.logger.Logger;
import com.onebytellc.imageviewer.reactive.Executor;
import com.onebytellc.imageviewer.reactive.Subscription;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;

/**
 * This is the image grid that can display 1,000's of images at
 * once with fast scrolling and zooming. This was fun to make.
 */
public class ImageGridCanvasController {

    private static final Logger LOG = Logger.getInstance(ImageGridCanvasController.class);

    @FXML
    private GridView<ImageHandle> gridView;

    private Subscription sub;

    @FXML
    private void initialize() {
        DisplayState state = Context.getInstance().getDisplayState();
        CollectionService collectionService = Context.getInstance().getCollectionService();

        Bindings.bindBidirectional(gridView.scaleFactorProperty(), state.gridImageScaleFactorProperty());
        gridView.minScaleFactorProperty().bind(state.gridMinScaleFactorProperty());
        gridView.maxScaleFactorProperty().bind(state.gridMaxScaleFactorProperty());
        gridView.baseImageSizeProperty().bind(state.gridBaseImageSizeProperty());
        gridView.setGridCellFactory(ImageGridCell::new);

        sub = collectionService.collectionImageRecords()
                .observeOn(Executor.fxApplicationThread())
                .subscribe(this::handeEvent);
    }

    private void handeEvent(ChangeSet<ImageHandle> change) {
        if (change.isReset()) {
            gridView.getItems().clear();
        }

        // remove items
        for (ImageHandle removed : change.getRemoved()) {
            gridView.getItems().removeIf(item -> item.getId() == removed.getId());
        }

        // update items
        for (ImageHandle removed : change.getUpdated()) {
            // not sure we need to update, just causing a refresh should be enough?
        }

        // add items
        gridView.getItems().addAll(change.getAdded());

        // sort items
        gridView.getItems().sort((o1, o2) -> {
            if (o1.getImOriginalDate() == null || o2.getImOriginalDate() == null) {
                return 0;
            }
            return o1.getImOriginalDate().compareTo(o2.getImOriginalDate());
        });
    }

}
