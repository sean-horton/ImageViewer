package com.onebyte_llc.imageviewer.ui.display.grid;

import com.onebyte_llc.imageviewer.ViewNode;
import com.onebyte_llc.imageviewer.backend.ImageHandle;
import com.onebyte_llc.imageviewer.ui.display.ImageViewController;
import javafx.scene.Node;
import org.controlsfx.control.GridCell;
import org.controlsfx.control.GridView;

public class ImageGridCell extends GridCell<ImageHandle> {

    private final GridView<ImageHandle> gridView;
    private ViewNode<Node, ImageViewController> viewNode;

    public ImageGridCell(GridView<ImageHandle> gridView) {
        this.gridView = gridView;
    }

    @Override
    protected void updateItem(ImageHandle item, boolean empty) {
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
