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

package com.onebyte_llc.imageviewer.reactive;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ExecutorWrapper implements Executor {

    private Runner runner;
    private ScheduledExecutorService scheduler;

    public ExecutorWrapper(Runner runner, ScheduledExecutorService scheduler) {
        this.runner = runner;
        this.scheduler = scheduler;
    }

    @Override
    public void run(Runnable runnable) {
        runner.run(runnable);
    }

    @Override
    public ScheduledFuture<?> run(Runnable runnable, long time, TimeUnit timeUnit) {
        return scheduler.schedule(() -> runner.run(runnable), time, timeUnit);
    }

    public interface Runner {
        void run(Runnable runnable);
    }

}
