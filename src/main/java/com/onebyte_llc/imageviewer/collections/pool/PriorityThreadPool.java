package com.onebyte_llc.imageviewer.collections.pool;

import com.onebyte_llc.imageviewer.logger.Logger;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;

public class PriorityThreadPool {

    private static final Logger LOG = Logger.getInstance(PriorityThreadPool.class);

    private final PriorityBlockingQueue<ScheduledTask> queue = new PriorityBlockingQueue<>(11,
            Comparator.comparingInt(o -> o.getPriority().ordinal()));

    private final List<Thread> threads = new ArrayList<>();

    public PriorityThreadPool(String name, int threadCount) {
        for (int i = 0; i < threadCount; i++) {
            Thread t = new Thread(() -> {
                while (true) {
                    try {
                        queue.take().run();
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

    public <T> ScheduledTask<T> offer(Priority priority, ScheduledRunnable<T> runnable) {
        ScheduledTask<T> scheduledTask = new ScheduledTask(priority, runnable);
        queue.offer(scheduledTask);
        return scheduledTask;
    }

}
