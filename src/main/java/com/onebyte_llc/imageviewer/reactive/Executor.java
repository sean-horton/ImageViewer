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

import com.onebyte_llc.imageviewer.logger.Logger;
import javafx.application.Platform;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public interface Executor {

    Logger LOG = Logger.getInstance(Executor.class);

    ExecutorService processThread = Executors.newSingleThreadExecutor(r -> {
        Thread thread = new Thread(r, "ProcessThread");
        thread.setDaemon(true);
        return thread;
    });

    void run(Runnable runnable);

    static Executor fxApplicationThread() {
        return runnable -> {
            if (Platform.isFxApplicationThread()) {
                runnable.run();
            } else {
                Platform.runLater(runnable);
            }
        };
    }

    static Executor processThread() {
        return runnable -> {
            try {
                processThread.submit(runnable);
            } catch (Exception e) {
                LOG.error("Failed to submit processThread runnable: {}", e.getMessage());
            }
        };
    }
}
