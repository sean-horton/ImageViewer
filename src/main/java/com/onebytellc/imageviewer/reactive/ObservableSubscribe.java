package com.onebytellc.imageviewer.reactive;

interface ObservableSubscribe<T> {

    void onSubscribe(SubscriptionBundle<T> subscription);

}
