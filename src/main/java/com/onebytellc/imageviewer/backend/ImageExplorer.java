package com.onebytellc.imageviewer.backend;

import com.onebytellc.imageviewer.logger.Logger;
import com.onebytellc.imageviewer.reactive.Observer;
import com.onebytellc.imageviewer.reactive.Subscriber;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

public class ImageExplorer {

    private static final Logger LOG = Logger.getInstance(ImageExplorer.class);

    private final WatchService watchService;
    private final Thread watchThread;
    private final BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();

    public ImageExplorer() {
        WatchService temp = null;
        try {
            temp = FileSystems.getDefault().newWatchService();
        } catch (IOException e) {
            LOG.warn("File system watching not supported");
        }

        watchService = temp;
        watchThread = new Thread(new WatchLoop(watchService, queue));
        watchThread.start();
    }

    public void destroy() {
        LOG.info("Destroying ImageExplorer");

        watchThread.interrupt();
        if (watchService != null) {
            try {
                watchService.close();
            } catch (IOException e) {
                LOG.warn("Unable to destroy WatchService");
            }
        }
    }

    public Observer<List<ImageLoader>> register(String path, int depth) {
        int d = Math.min(depth, 10);
        queue.offer(new RegisterAction());
        return null;
    }

    private static class RegisterAction implements Runnable {

        @Override
        public void run() {
//            Path dir = Path.of(path);
//            WatchKey watchKey = dir.register(watchService,
//                    ENTRY_CREATE,
//                    ENTRY_DELETE,
//                    ENTRY_MODIFY);
        }

    }

    private static class WatchLoop implements Runnable {

        private final WatchService watchService;
        private final BlockingQueue<Runnable> queue;

        public WatchLoop(WatchService watchService, BlockingQueue<Runnable> actionQueue) {
            this.watchService = watchService;
            this.queue = actionQueue;
        }

        @Override
        public void run() {
//            try {
//                if (watchService == null) {
//                    return;
//                }
//
//                for (; ; ) {
//                    for (WatchEvent<?> event : watchKey.pollEvents()) {
//                        WatchEvent.Kind<?> kind = event.kind();
//
//                        // This key is registered only
//                        // for ENTRY_CREATE events,
//                        // but an OVERFLOW event can
//                        // occur regardless if events
//                        // are lost or discarded.
//                        if (kind == OVERFLOW) {
//                            continue;
//                        }
//
//                        // The filename is the
//                        // context of the event.
//                        WatchEvent<Path> ev = (WatchEvent<Path>) event;
//                        Path filename = ev.context();
//
//                        Path child = dir.resolve(filename);
//                        System.out.println(kind + " " + filename);
//                    }
//
//                    Thread.sleep(1000);
//                    boolean valid = watchKey.reset();
//                    if (!valid) {
//                        break;
//                    }
//                }
//            } catch (InterruptedException e) {
//                return;
//            } catch (IOException e) {
//                LOG.error("IOException in WatchLoop", e.getMessage());
//            }
        }

    }

}
