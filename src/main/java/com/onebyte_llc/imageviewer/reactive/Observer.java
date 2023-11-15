package com.onebyte_llc.imageviewer.reactive;

public interface Observer<T> {

    void notify(T data);

}
