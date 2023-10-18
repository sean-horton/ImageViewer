package com.onebytellc.imageviewer.reactive;

public interface Observer<T> {

    void notify(T data);

}
