package com.onebyte_llc.imageviewer.ui.directory;

import com.onebyte_llc.imageviewer.backend.db.jooq.tables.records.CollectionRecord;
import com.onebyte_llc.imageviewer.backend.CollectionService;
import com.onebyte_llc.imageviewer.backend.Context;
import com.onebyte_llc.imageviewer.logger.Logger;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;


public class DirectoryListViewController {

    private static final Logger LOG = Logger.getInstance(DirectoryListViewController.class);

    @FXML
    private ListView<CollectionRecord> listView;

    @FXML
    private void initialize() {
        CollectionService collectionService = Context.getInstance().getCollectionService();

        listView.setFixedCellSize(28);
        listView.setCellFactory(param -> new DirectoryCell());
        listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        listView.setItems(collectionService.collectionProperty());

        // selected item binding (needed in 3 places)
        collectionService.collectionSelected().addListener((observable, oldValue, newValue) ->
                listView.getSelectionModel().select(newValue));
        listView.getSelectionModel().getSelectedItems().addListener((ListChangeListener<CollectionRecord>) c -> {
            if (!c.getList().isEmpty()) {
                collectionService.collectionSelected().setValue(c.getList().get(0));
            }
        });
        listView.getItems().addListener((ListChangeListener<CollectionRecord>) c -> {
            CollectionRecord selected = collectionService.collectionSelected().get();
            if (selected != null) {
                listView.getSelectionModel().select(selected);
            }
        });
    }

}
