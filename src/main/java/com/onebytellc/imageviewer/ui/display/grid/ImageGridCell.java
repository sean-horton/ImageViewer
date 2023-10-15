package com.onebytellc.imageviewer.ui.display.grid;

import com.onebytellc.imageviewer.ViewNode;
import com.onebytellc.imageviewer.backend.ImageData;
import com.onebytellc.imageviewer.ui.display.ImageViewController;
import javafx.scene.Node;
import org.controlsfx.control.GridCell;
import org.controlsfx.control.GridView;

public class ImageGridCell extends GridCell<ImageData> {

    private final GridView<ImageData> gridView;
    private ViewNode<Node, ImageViewController> viewNode;

    public ImageGridCell(GridView<ImageData> gridView) {
        this.gridView = gridView;
    }

    @Override
    protected void updateItem(ImageData item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setGraphic(null);
            return;
        }

        if (viewNode == null) {
            viewNode = ImageViewController.create();
        }

        viewNode.getController().setImageData(item);
        viewNode.getController().bindWidth(gridView);
        setGraphic(viewNode.getNode());
    }
}
