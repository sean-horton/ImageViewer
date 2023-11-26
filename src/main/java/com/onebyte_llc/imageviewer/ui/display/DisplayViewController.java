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

package com.onebyte_llc.imageviewer.ui.display;

import com.onebyte_llc.imageviewer.ViewNode;
import com.onebyte_llc.imageviewer.backend.Context;
import com.onebyte_llc.imageviewer.backend.DisplayState;
import com.onebyte_llc.imageviewer.ui.display.header.HeaderMode0ViewController;
import com.onebyte_llc.imageviewer.ui.display.header.HeaderMode1ViewController;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.VBox;

public class DisplayViewController {

    @FXML
    private VBox toolbarVbox;

    private ViewNode<Node, HeaderMode0ViewController> mode0;
    private ViewNode<Node, HeaderMode1ViewController> mode1;

    @FXML
    private void initialize() {
        DisplayState state = Context.getInstance().getDisplayState();

        mode0 = HeaderMode0ViewController.create();
        mode1 = HeaderMode1ViewController.create();

        toolbarVbox.getChildren().add(mode0.getNode());
        state.fullScreenImageProperty().addListener((observable, oldValue, newValue) -> {
            toolbarVbox.getChildren().clear();
            if (newValue == null) {
                toolbarVbox.getChildren().add(mode0.getNode());
            } else {
                toolbarVbox.getChildren().add(mode1.getNode());
            }
        });
    }

}
