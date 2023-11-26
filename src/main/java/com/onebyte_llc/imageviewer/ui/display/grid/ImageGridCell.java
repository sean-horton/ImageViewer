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
