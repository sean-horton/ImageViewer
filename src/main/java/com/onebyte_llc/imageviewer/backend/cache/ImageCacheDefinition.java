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

package com.onebyte_llc.imageviewer.backend.cache;

/**
 * Definition of cached images. Specify
 * <ul>
 *     <li>resolution of cached images on disk</li>
 *     <li>How many of these images we will store in RAM for in memory cache</li>
 * </ul>
 */
public class ImageCacheDefinition {

    private final int w;
    private final int h;
    private final int maxCacheSize;

    public ImageCacheDefinition(int w, int h, int maxCacheSize) {
        this.w = w;
        this.h = h;
        this.maxCacheSize = maxCacheSize;
    }

    public int getW() {
        return w;
    }

    public int getH() {
        return h;
    }

    public int getMaxCacheSize() {
        return maxCacheSize;
    }

    // TODO - rotate cache directories based on file count?
    public String getFileName(String name) {
        return name + "_" + w + "x" + h + ".jpg";
    }
}
