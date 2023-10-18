package com.onebytellc.imageviewer.backend;

import com.onebytellc.imageviewer.reactive.Executor;
import com.onebytellc.imageviewer.reactive.Observable;
import com.onebytellc.imageviewer.reactive.Subscriber;
import javafx.scene.image.Image;

import java.io.FileInputStream;
import java.io.InputStream;

public class ImageLoaderJpeg implements ImageLoader {

    private Subscriber<Image> sub;

    public ImageLoaderJpeg(String file) {
        sub = new Subscriber<>(() -> {
            Executor.pool().run(() -> {
                try (InputStream in = new FileInputStream(file)) {
                    sub.notify(new Image(in));
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            });
        });
    }

    @Override
    public Observable<Image> load(int w, int h) {
        return sub.observe();
    }

}
