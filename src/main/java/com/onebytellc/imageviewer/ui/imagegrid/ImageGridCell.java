package com.onebytellc.imageviewer.ui.imagegrid;

import com.onebytellc.imageviewer.ViewNode;
import com.onebytellc.imageviewer.backend.ImageData;
import javafx.scene.Node;
import org.controlsfx.control.GridCell;

public class ImageGridCell extends GridCell<ImageData> {

    private ViewNode<Node, ImageGridCellController> viewNode;

    @Override
    protected void updateItem(ImageData item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setGraphic(null);
            return;
        }

        if (viewNode == null) {
            viewNode = ImageGridCellController.create();
        }

        viewNode.getController().setImageData(item);
        setGraphic(viewNode.getNode());
    }
}
