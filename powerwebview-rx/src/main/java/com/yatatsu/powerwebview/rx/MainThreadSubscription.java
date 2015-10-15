package com.yatatsu.powerwebview.rx;


import android.os.Handler;
import android.os.Looper;

import rx.Subscription;
import rx.functions.Action0;
import rx.subscriptions.Subscriptions;

final class MainThreadSubscription {

    public static Subscription unsubscribeOnMainThread(final Action0 unsubscribe) {
        return Subscriptions.create(new Action0() {
            @Override
            public void call() {
                if (Looper.getMainLooper() == Looper.myLooper()) {
                    unsubscribe.call();
                } else {
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            unsubscribe.call();
                        }
                    });
                }
            }
        });
    }
}
