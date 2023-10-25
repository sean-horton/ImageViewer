package com.onebytellc.imageviewer.backend.cache;

import com.onebytellc.imageviewer.logger.Logger;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;

public class PriorityThreadPool {

    private static final Logger LOG = Logger.getInstance(PriorityThreadPool.class);

    private final PriorityBlockingQueue<Task> queue = new PriorityBlockingQueue<>(11,
            Comparator.comparingInt(o -> o.getPriority().ordinal()));

    private final List<Thread> threads = new ArrayList<>();

    public PriorityThreadPool(String name, int threadCount) {
        for (int i = 0; i < threadCount; i++) {
            Thread t = new Thread(() -> {
                while (true) {
                    try {
                        queue.take().getRunnable().run();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    } catch (Exception e) {
                        LOG.error("Failed to run async task {}", e.getMessage());
                    }
                }
            }, name + i);
            t.setDaemon(true);
            t.start();
            threads.add(t);
        }
    }

    public void offer(Priority priority, Runnable runnable) {
        queue.offer(new Task(priority, runnable));
    }

    private static class Task {
        private final Priority priority;
        private final Runnable runnable;

        public Task(Priority priority, Runnable runnable) {
            this.priority = priority;
            this.runnable = runnable;
        }

        public Priority getPriority() {
            return priority;
        }

        public Runnable getRunnable() {
            return runnable;
        }
    }

    public enum Priority {
        HIGH,
        MEDIUM,
        LOW
    }
}
