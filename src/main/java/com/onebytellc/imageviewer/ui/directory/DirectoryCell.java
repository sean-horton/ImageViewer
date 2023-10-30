package com.onebytellc.imageviewer.ui.directory;

import com.onebytellc.imageviewer.ViewNode;
import com.onebytellc.imageviewer.backend.Context;
import com.onebytellc.imageviewer.backend.db.jooq.tables.records.CollectionRecord;
import javafx.beans.binding.Bindings;
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

        view.getController().setValue(item.getName());
        setGraphic(view.getNode());
        configureRightClick();
    }

    private void configureRightClick() {
        ContextMenu contextMenu = new ContextMenu();

        MenuItem editItem = new MenuItem();
        editItem.textProperty().bind(Bindings.format("Edit"));
        editItem.setOnAction(event -> {

        });

        MenuItem deleteItem = new MenuItem();
        deleteItem.textProperty().bind(Bindings.format("Delete"));
        deleteItem.setOnAction(event -> Context.getInstance().getCollectionService().deleteCollection(getItem().getId()));

        contextMenu.getItems().add(editItem);
        contextMenu.getItems().add(deleteItem);

        setContextMenu(contextMenu);
    }

}
