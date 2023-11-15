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
