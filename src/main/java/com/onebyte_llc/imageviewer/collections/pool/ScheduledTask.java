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

package com.onebyte_llc.imageviewer.collections.pool;

import com.onebyte_llc.imageviewer.reactive.Observable;
import com.onebyte_llc.imageviewer.reactive.Single;

public class ScheduledTask<T> {

    private final Priority priority;
    private final ScheduledRunnable<T> runnable;
    private final Single<T> single;

    private volatile boolean canceled;
    private volatile boolean complete;

    public ScheduledTask(Priority priority, ScheduledRunnable<T> runnable) {
        this.priority = priority;
        this.runnable = runnable;
        this.single = new Single<>();
    }

    void run() {
        if (complete) {
            throw new IllegalStateException("Can't run an already completed task!");
        }
        if (canceled) {
            return;
        }

        T data = runnable.run();
        complete = true;
        single.notify(data);
    }

    /////////////////////
    // PUBLIC
    public Priority getPriority() {
        return priority;
    }

    public Observable<T> observe() {
        return single.observe();
    }

    public void cancel() {
        canceled = true;
    }

    public boolean isCanceled() {
        return canceled;
    }

    public boolean isComplete() {
        return complete;
    }
}
