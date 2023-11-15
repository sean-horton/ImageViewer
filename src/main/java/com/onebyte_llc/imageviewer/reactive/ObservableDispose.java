package com.onebyte_llc.imageviewer.reactive;

interface ObservableDispose<T> {

    void dispose(SubscriptionBundle<T> subscription);

}
