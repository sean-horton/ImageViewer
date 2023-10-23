package com.onebytellc.imageviewer.reactive;

import com.onebytellc.imageviewer.logger.Logger;
import javafx.application.Platform;

import java.util.PriorityQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
