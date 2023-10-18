package com.onebytellc.imageviewer.reactive;

import javafx.application.Platform;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public interface Executor {

    ExecutorService thread = Executors.newSingleThreadExecutor(r -> {
        Thread thread = new Thread(r);
        thread.setDaemon(true);
        return thread;
    });

    ExecutorService pool = Executors.newScheduledThreadPool(10, r -> {
        Thread thread = new Thread(r);
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
        return thread::submit;
    }

    static Executor pool() {
        return pool::submit;
    }
}
