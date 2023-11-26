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

import com.onebyte_llc.imageviewer.Theme;
import com.onebyte_llc.imageviewer.backend.CollectionService;
import com.onebyte_llc.imageviewer.backend.Context;
import com.onebyte_llc.imageviewer.logger.Logger;
import com.onebyte_llc.imageviewer.reactive.Executor;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.stage.DirectoryChooser;

import java.io.File;


public class DirectoryToolbarController {

    private static final Logger LOG = Logger.getInstance(DirectoryToolbarController.class);

    @FXML
    private Button addButton;
    @FXML
    private ImageView addButtonImage;

    @FXML
    private void initialize() {
        CollectionService collectionService = Context.getInstance().getCollectionService();

        addButton.effectProperty().bind(Theme.buttonBlueEffect());
        addButton.setOnAction(event -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            File selectedDirectory = directoryChooser.showDialog(addButton.getScene().getWindow());
            if (selectedDirectory == null) {
                return;
            }

            collectionService.addCollection(selectedDirectory.getName(), 10, selectedDirectory.getAbsolutePath())
                    .observeOn(Executor.fxApplicationThread())
                    .subscribe(res -> {
                        LOG.info("Added new collection '{}', success: {}", "name", res);
                    });
        });
    }

}
