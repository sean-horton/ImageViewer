package com.onebytellc.imageviewer.backend.image;

import com.onebytellc.imageviewer.backend.db.Database;
import com.onebytellc.imageviewer.backend.db.jooq.tables.records.ImageRecord;
import com.onebytellc.imageviewer.logger.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;

public class ImageIndexer {

    private static final Logger LOG = Logger.getInstance(ImageIndexer.class);

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
                        queue.take().getRunnable().run();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    } catch (Exception e) {
                        LOG.error("Failed to run async task {}", e.getMessage());
                    }
                }
            }, "ImageIndexer" + i);
            t.setDaemon(true);
            t.start();
            threads.add(t);
        }
    }

    public void asyncRemoveIndex(Priority priority, ImageRecord record) {
        queue.offer(new Task(priority, () -> {
            // TODO - remove indexed image
            // TODO - delete record from DB
        }));
    }

    public void asyncIndex(Priority priority, ImageRecord record, ImageLoader loader) {
        queue.offer(new Task(priority, () -> {
            System.out.println("SEAN thread " + Thread.currentThread().getName() + " id: " + record.getId() + " - " + loader.getPath());
            try {
                ImageData data = loader.readFromDisk();

                double w = data.getImage().getWidth();
                double h = data.getImage().getHeight();

                double lim = 64;
                if (w > lim) {
                    double ratio = lim / w;
                    w *= ratio;
                    h *= ratio;
                }
                if (h > lim) {
                    double ratio = lim / h;
                    w *= ratio;
                    h *= ratio;
                }

                BufferedImage resizedImage = new BufferedImage((int) w, (int) h, BufferedImage.TYPE_INT_RGB);
                Graphics2D graphics2D = resizedImage.createGraphics();
                graphics2D.drawImage(data.getImage(), 0, 0, (int) w, (int) h, null);
                graphics2D.dispose();

                // TODO - change path
                File indexImg = new File("/Users/shorton/imageviewdata/cache/" + record.getId() + "_64x64.jpg");
                ImageIO.write(resizedImage, "jpg", indexImg);

                FileTime lastModified = Files.getLastModifiedTime(loader.getPath());
                record.setFsModifyTime(LocalDateTime.ofInstant(lastModified.toInstant(), ZoneId.systemDefault()));
                record.store();

                // TODO  - notify an image was indexed
            } catch (IOException e) {
                LOG.error("Unable to create image index IOException: {}", e.getMessage());
            } catch (Exception e) {
                LOG.error("Unable to create image index: {}", e.getMessage());
            }
        }));
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
