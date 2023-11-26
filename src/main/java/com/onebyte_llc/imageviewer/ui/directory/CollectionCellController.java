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

import com.onebyte_llc.imageviewer.MainApplication;
import com.onebyte_llc.imageviewer.ViewNode;
import com.onebyte_llc.imageviewer.backend.Context;
import com.onebyte_llc.imageviewer.backend.db.jooq.tables.records.CollectionRecord;
import com.onebyte_llc.imageviewer.logger.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

import java.io.IOException;

public class CollectionCellController {

    private static final Logger LOG = Logger.getInstance(CollectionCellController.class);

    @FXML
    private Label label;
    @FXML
    private TextField renameTextField;

    private CollectionRecord record;

    public static ViewNode<Node, CollectionCellController> create() {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class
                .getResource("/layout/directory/collection-cell-view.fxml"));
        try {
            return new ViewNode<>(fxmlLoader.load(), fxmlLoader.getController());
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load MainController", e);
        }
    }

    @FXML
    private void initialize() {
        renameTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                saveName();
            }
        });
        renameTextField.setOnKeyPressed(key -> {
            if (key.getCode() == KeyCode.ENTER) {
                saveName();
            }
        });
    }

    public void setValue(CollectionRecord value) {
        this.record = value;
        label.setText(value.getName());
    }

    public void renameMode() {
        renameTextField.setManaged(true);
        renameTextField.setVisible(true);
        renameTextField.setFocusTraversable(true);

        label.setManaged(false);
        label.setVisible(false);

        renameTextField.setText(label.getText());
        renameTextField.requestFocus();
    }

    private void saveName() {
        LOG.info("Renaming collection '{}' to '{}'", label.getText(), renameTextField.getText());

        record.setName(renameTextField.getText());
        Context.getInstance().getCollectionService().updateCollection(record);

        renameTextField.setManaged(false);
        renameTextField.setVisible(false);
        renameTextField.setFocusTraversable(false);

        label.setManaged(true);
        label.setVisible(true);
    }

}
