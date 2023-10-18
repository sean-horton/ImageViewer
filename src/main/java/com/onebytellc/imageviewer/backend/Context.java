package com.onebytellc.imageviewer.backend;

import com.onebytellc.imageviewer.logger.Logger;
import javafx.scene.image.Image;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public final class Context {

    private static final Logger LOG = Logger.getInstance(Context.class);

    private static Context INSTANCE;

    private final DisplayState displayState;
    private final ImageExplorer imageExplorer;

    private Context() {
        this.displayState = new DisplayState();
        this.imageExplorer = new ImageExplorer();
    }


    //////////////////////
    // static
    public static synchronized void initialize() {
        if (INSTANCE == null) {
            LOG.info("Context initialized");
            INSTANCE = new Context();
        } else {
            LOG.warn("Context was already created");
        }

        new Thread(() -> {
            try {
                List<Image> imageList = new ArrayList<>();
                try (InputStream in = new FileInputStream("/Users/shorton/imageviewtest/IMG_1248-small.jpeg")) {
                    Image image = new Image(in);
                    for (int i = 0; i < 2000; i++) {
                        imageList.add(image);
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                INSTANCE.getDisplayState().setImages(imageList);

                Thread.sleep(10_000);

                imageList.clear();
                try (InputStream in = new FileInputStream("/Users/shorton/imageviewtest/2023-09-04-Erics-labor-day/DSC04105-tree-frog-at-horton-house.jpg")) {
                    Image image = new Image(in);
                    for (int i = 0; i < 1_000_000; i++) {
                        imageList.add(image);
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                INSTANCE.getDisplayState().setImages(imageList);
            } catch (Exception e) {

            }
        }).start();
    }

    public static synchronized void destroy() {
        if (INSTANCE == null) {
            return;
        }

        INSTANCE.imageExplorer.destroy();
    }

    public static Context getInstance() {
        if (INSTANCE == null) {
            throw new IllegalStateException("Context must be initialized first!");
        }
        return INSTANCE;
    }


    //////////////////////
    // public
    public DisplayState getDisplayState() {
        return displayState;
    }

}
