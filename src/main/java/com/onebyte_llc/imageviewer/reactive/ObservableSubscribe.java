package com.onebyte_llc.imageviewer.reactive;

interface ObservableSubscribe<T> {

    void onSubscribe(SubscriptionBundle<T> subscription);

}
