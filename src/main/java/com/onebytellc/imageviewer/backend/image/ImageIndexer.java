package com.onebytellc.imageviewer.backend.image;

import com.onebytellc.imageviewer.backend.db.Database;
import com.onebytellc.imageviewer.backend.db.jooq.tables.records.ImageRecord;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;

public class ImageIndexer {

    private final PriorityBlockingQueue<Task> queue = new PriorityBlockingQueue<>(11,
            Comparator.comparingInt(o -> o.getPriority().ordinal()));

    private final List<Thread> threads = new ArrayList<>();
    private final Database database;

    public ImageIndexer(int threadCount, Database database) {
        this.database = database;

        for (int i = 0; i < threadCount; i++) {
            Thread t = new Thread(() -> {
                while (true) {
                    try {
                        complete(queue.take());
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }, "ImageIndexer" + i);
            t.setDaemon(true);
            t.start();
        }
    }

    private void complete(Task task) {
        // TODO - read image
        //   - write indexed images to disk
        //   - update image database fields with metadata
        //   - notify an image was indexed


        ImageLoader loader = task.getLoader();
        ImageData data = loader.readFromDisk();
//        data.get();
    }

    public void asyncIndex(Priority priority, ImageRecord record, ImageLoader loader) {
        queue.offer(new Task(priority, record, loader));
    }

    private static class Task {
        private final Priority priority;
        private final ImageRecord record;
        private final ImageLoader loader;

        public Task(Priority priority, ImageRecord record, ImageLoader loader) {
            this.priority = priority;
            this.record = record;
            this.loader = loader;
        }

        public Priority getPriority() {
            return priority;
        }

        public ImageRecord getRecord() {
            return record;
        }

        public ImageLoader getLoader() {
            return loader;
        }
    }

    public enum Priority {
        HIGH,
        MEDIUM,
        LOW
    }

}
