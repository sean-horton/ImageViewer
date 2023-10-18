package com.onebytellc.imageviewer.backend;

import com.onebytellc.imageviewer.reactive.Observable;
import javafx.scene.image.Image;

public interface ImageLoader {

    /**
     * Loads the image matching the desired size (i.e. it will load indexed or original images based on size)
     *
     * @param w desired width
     * @param h desired height
     * @return an image observable
     */
    Observable<Image> load(int w, int h);

}
