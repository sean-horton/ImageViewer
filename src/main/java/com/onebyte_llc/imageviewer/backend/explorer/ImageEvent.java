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

package com.onebyte_llc.imageviewer.backend.explorer;

import com.onebyte_llc.imageviewer.backend.image.ImageLoader;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ImageEvent {

    private final List<ImageLoader> loaders;
    private final ImageEventType type;
    private final Path rootDir;

    public ImageEvent(Path root, List<ImageLoader> loaders, ImageEventType type) {
        this.rootDir = root;
        this.loaders = loaders;
        this.type = type;
    }

    public ImageEvent(Path root, ImageLoader loader, ImageEventType type) {
        this.rootDir = root;
        this.loaders = new ArrayList<>(1);
        loaders.add(loader);
        this.type = type;
    }

    public List<ImageLoader> getLoader() {
        return loaders;
    }

    public ImageEventType getType() {
        return type;
    }

    public Path getRootDir() {
        return rootDir;
    }
}
