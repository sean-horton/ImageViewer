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

import java.util.HashSet;
import java.util.Set;

public class FetchLock {

    private final Set<Integer> inProgress = new HashSet<>();

    public synchronized boolean lock(int key) {
        if (inProgress.contains(key)) {
            return false;
        }
        inProgress.add(key);
        return true;
    }

    public synchronized void unlock(int key) {
        inProgress.remove(key);
    }

}
