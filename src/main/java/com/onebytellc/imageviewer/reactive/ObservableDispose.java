package com.onebytellc.imageviewer.reactive;

interface ObservableDispose<T> {

    void dispose(SubscriptionBundle<T> subscription);

}
