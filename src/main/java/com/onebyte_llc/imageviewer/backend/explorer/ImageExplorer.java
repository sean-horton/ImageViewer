package com.onebyte_llc.imageviewer.backend.explorer;

import com.onebyte_llc.imageviewer.backend.image.ImageLoader;
import com.onebyte_llc.imageviewer.backend.image.ImageTypeDefinition;
import com.onebyte_llc.imageviewer.logger.Logger;
import com.onebyte_llc.imageviewer.reactive.Observable;
import com.onebyte_llc.imageviewer.reactive.Streamable;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

public class ImageExplorer implements Runnable {

    private static final Logger LOG = Logger.getInstance(ImageExplorer.class);

    // state
    private final List<ImageTypeDefinition> imageTypes;
    private final WatchService watchService;
    private final Map<String, Directory> directories = new HashMap<>();

    // threading
    private final Thread watchThread;
    private final BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
    private volatile boolean running;

    public ImageExplorer(List<ImageTypeDefinition> imageTypes) {
        this.imageTypes = imageTypes;

        WatchService temp = null;
        try {
            temp = FileSystems.getDefault().newWatchService();
        } catch (IOException e) {
            LOG.warn("File system watching not supported");
        }

        running = true;
        watchService = temp;
        watchThread = new Thread(this);
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

    /**
     * The returned observable acts as a stream.
     */
    public Observable<ImageEvent> register(String root, int depth) {
        Directory directory = directories.computeIfAbsent(root, k -> new Directory());
        queue.offer(() -> {
            try {
                recursiveFetch(directory, Path.of(root), Math.min(10, depth));
            } catch (IOException e) {
                LOG.error("Unable to load path {}", root);
            }
        });
        return directory.streamable.observe();
    }

    public void reset() {
        directories.forEach((s, directory) -> {
            directory.subDirectories.forEach(sub -> sub.watchKey.cancel());
        });
        directories.clear();
    }

    private void recursiveFetch(Directory directory, Path root, int depth) throws IOException {
        if (depth <= 0) {
            return;
        }

        WatchKey watchKey = root.register(watchService,
                ENTRY_CREATE,
                ENTRY_DELETE,
                ENTRY_MODIFY);

        directory.addPath(root, watchKey);

        List<ImageLoader> initialImages = new ArrayList<>();
        for (Path path : Files.list(root).toList()) {
            if (Files.isDirectory(path)) {
                recursiveFetch(directory, path, depth - 1);
                continue;
            }

            for (ImageTypeDefinition imageType : imageTypes) {
                if (imageType.isLoadable(path)) {
                    initialImages.add(imageType.createLoader(path));
                    break;
                }
            }
        }

        directory.streamable.notify(new ImageEvent(root, initialImages, ImageEventType.INIT));
    }

    ////////////////////////
    // Thread Runner
    @Override
    public void run() {
        if (watchService == null) {
            return;
        }

        try {
            while (running) {
                Runnable action = queue.poll(1, TimeUnit.SECONDS);
                if (action != null) {
                    action.run();
                }

                pollWatchKeyEvents();
            }
        } catch (InterruptedException e) {
            LOG.info("ImageExplorer thread was interrupted");
        }
    }

    private void pollWatchKeyEvents() {
        for (Directory directory : directories.values()) {
            for (SubDirectory subDirectory : directory.subDirectories) {
                for (WatchEvent<?> event : subDirectory.watchKey.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();

                    // This key is registered only
                    // for ENTRY_CREATE events,
                    // but an OVERFLOW event can
                    // occur regardless if events
                    // are lost or discarded.
                    if (kind == OVERFLOW) {
                        continue;
                    }

                    // The filename is the
                    // context of the event.
                    WatchEvent<Path> ev = (WatchEvent<Path>) event;
                    Path filename = ev.context();

                    ImageLoader loader = null;
                    for (ImageTypeDefinition type : imageTypes) {
                        if (type.isLoadable(filename)) {
                            loader = type.createLoader(subDirectory.path.resolve(filename));
                            break;
                        }
                    }

                    // TODO - need to chheck if it's a directory and thhen call init if it was added
                    //  how about a removed directory?

                    if (loader != null) {
                        if (kind == ENTRY_CREATE) {
                            directory.streamable.notify(new ImageEvent(subDirectory.path, loader, ImageEventType.ADDED));
                        } else if (kind == ENTRY_DELETE) {
                            directory.streamable.notify(new ImageEvent(subDirectory.path, loader, ImageEventType.REMOVED));
                        } else if (kind == ENTRY_MODIFY) {
                            directory.streamable.notify(new ImageEvent(subDirectory.path, loader, ImageEventType.UPDATED));
                        }
                    }
                }

                boolean valid = subDirectory.watchKey.reset();
                if (!valid) {
                    break;
                }
            }
        }
    }

    private static class Directory {

        private final Streamable<ImageEvent> streamable = new Streamable<>();
        private final List<SubDirectory> subDirectories = new ArrayList<>();

        public Directory() {

        }

        public void addPath(Path path, WatchKey watchKey) {
            subDirectories.add(new SubDirectory(path, watchKey));
        }
    }

    private static class SubDirectory {
        private final Path path;
        private final WatchKey watchKey;

        public SubDirectory(Path path, WatchKey watchKey) {
            this.path = path;
            this.watchKey = watchKey;
        }
    }

}
