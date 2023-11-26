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

package com.onebyte_llc.imageviewer.backend;

import java.nio.file.Path;

public class ContextParameters {

    private Path appDataPath;

    private ContextParameters(Builder builder) {
        this.appDataPath = builder.appDataPath;
    }

    public Path getDatabaseDir() {
        return appDataPath;
    }

    public Path getImageCacheDir() {
        return appDataPath.resolve("cache");
    }

    public static class Builder {

        private Path appDataPath;

        public Builder setAppDataPath(Path path) {
            this.appDataPath = path;
            return this;
        }

        public ContextParameters build() {
            return new ContextParameters(this);
        }

    }

}
