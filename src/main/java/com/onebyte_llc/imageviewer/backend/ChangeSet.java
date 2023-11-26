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

import java.util.ArrayList;
import java.util.List;

public class ChangeSet<T> {

    private final boolean reset;
    private final List<T> added;
    private final List<T> updated;
    private final List<T> removed;

    public ChangeSet(boolean reset, List<T> added, List<T> updated, List<T> removed) {
        this.reset = reset;
        this.added = added == null ? new ArrayList<>(0) : added;
        this.updated = updated == null ? new ArrayList<>(0) : updated;
        this.removed = removed == null ? new ArrayList<>(0) : removed;
    }

    public boolean isReset() {
        return reset;
    }

    public List<T> getAdded() {
        return added;
    }

    public List<T> getUpdated() {
        return updated;
    }

    public List<T> getRemoved() {
        return removed;
    }
}
