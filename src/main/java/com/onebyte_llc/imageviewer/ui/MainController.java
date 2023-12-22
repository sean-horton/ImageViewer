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

package com.onebyte_llc.imageviewer.ui;

import com.onebyte_llc.imageviewer.MainApplication;
import com.onebyte_llc.imageviewer.ViewNode;
import com.onebyte_llc.imageviewer.backend.Context;
import com.onebyte_llc.imageviewer.backend.DisplayState;
import com.onebyte_llc.imageviewer.ui.directory.DirectoryViewController;
import com.onebyte_llc.imageviewer.ui.display.DisplayViewController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class MainController {

    @FXML
    private BorderPane mainBorderPane;

    private SplitPane splitPane;
    private ViewNode<Parent, DirectoryViewController> directoryNode;
    private ViewNode<Parent, DisplayViewController> displayNode;

    private double dividerPosition = 0.25;

    public static ViewNode<Parent, MainController> create() {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class
                .getResource("/layout/main-view.fxml"));
        try {
            return new ViewNode<>(fxmlLoader.load(), fxmlLoader.getController());
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load MainController", e);
        }
    }

    @FXML
    private void initialize() {
        splitPane = new SplitPane();
        directoryNode = DirectoryViewController.create();
        displayNode = DisplayViewController.create();

        DisplayState state = Context.getInstance().getDisplayState();
        state.isSlideshowProperty().addListener((observable, oldValue, newValue) -> configureSlideshow(newValue));
        configureSlideshow(false);
    }

    private void configureSlideshow(boolean isSlideshow) {
        if (isSlideshow) {
            dividerPosition = splitPane.getDividerPositions()[0];
            splitPane.getItems().clear();
            mainBorderPane.setCenter(displayNode.getNode());
        } else {
            mainBorderPane.setCenter(splitPane);
            splitPane.getItems().add(directoryNode.getNode());
            splitPane.getItems().add(displayNode.getNode());
            splitPane.setDividerPosition(0, dividerPosition);
        }
    }

}