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

package com.onebyte_llc.imageviewer.ui.directory;

import com.onebyte_llc.imageviewer.I18N;
import com.onebyte_llc.imageviewer.ViewNode;
import com.onebyte_llc.imageviewer.backend.Context;
import com.onebyte_llc.imageviewer.backend.db.jooq.tables.records.CollectionRecord;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.MenuItem;

public class DirectoryCell extends ListCell<CollectionRecord> {

    ViewNode<Node, CollectionCellController> view;

    @Override
    protected void updateItem(CollectionRecord item, boolean empty) {
        super.updateItem(item, empty);
        if (view == null) {
            view = CollectionCellController.create();
        }

        if (item == null) {
            setGraphic(null);
            setContextMenu(null);
            return;
        }

        view.getController().setValue(item);
        setGraphic(view.getNode());
        configureRightClick();
    }

    private void configureRightClick() {
        ContextMenu contextMenu = new ContextMenu();

        MenuItem editItem = new MenuItem();
        editItem.textProperty().bind(I18N.get("rename"));
        editItem.setOnAction(event -> view.getController().renameMode());

        MenuItem deleteItem = new MenuItem();
        deleteItem.textProperty().bind(I18N.get("delete"));
        deleteItem.setOnAction(event -> Context.getInstance().getCollectionService().deleteCollection(getItem().getId()));

        contextMenu.getItems().add(editItem);
        contextMenu.getItems().add(deleteItem);

        setContextMenu(contextMenu);
    }

}
