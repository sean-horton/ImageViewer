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
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;

public class DirectoryViewController {

    public static ViewNode<Parent, DirectoryViewController> create() {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class
                .getResource("/layout/directory/directory-view.fxml"));
        try {
            return new ViewNode<>(fxmlLoader.load(), fxmlLoader.getController());
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load DirectoryViewController", e);
        }
    }

}
