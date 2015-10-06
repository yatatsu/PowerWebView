package com.yatatsu.powerwebview.rx;


import android.graphics.Bitmap;
import android.os.Looper;
import android.webkit.WebView;

import com.cookpad.android.rxt4a.subscriptions.AndroidSubscriptions;
import com.yatatsu.powerwebview.LoadStateWatcher;
import com.yatatsu.powerwebview.PowerWebView;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;

final class PowerWebViewStateChangeEventOnSubscribe
        implements Observable.OnSubscribe<PowerWebViewStateChangeEvent> {

    private final PowerWebView view;

    public PowerWebViewStateChangeEventOnSubscribe(PowerWebView view) {
        this.view = view;
    }

    @Override
    public void call(final Subscriber<? super PowerWebViewStateChangeEvent> subscriber) {
        checkUiThread();

        final LoadStateWatcher watcher = new LoadStateWatcher() {
            @Override
            public void onStarted(WebView webView, String url, Bitmap favicon) {
                if (!subscriber.isUnsubscribed()) {
                    PowerWebViewStateChangeEvent event = PowerWebViewStateChangeEvent.start(
                            (PowerWebView) webView, url, favicon);
                    subscriber.onNext(event);
                }
            }

            @Override
            public void onFinished(WebView webView, String url) {
                if (!subscriber.isUnsubscribed()) {
                    PowerWebViewStateChangeEvent event = PowerWebViewStateChangeEvent.finish(
                            (PowerWebView) webView, url);
                    subscriber.onNext(event);
                }
            }

            @Override
            public void onError(
                    WebView webView, int errorCode, String description, String failingUrl) {
                if (!subscriber.isUnsubscribed()) {
                    PowerWebViewStateChangeEvent event = PowerWebViewStateChangeEvent.error(
                            (PowerWebView) webView, errorCode, description, failingUrl);
                    subscriber.onNext(event);
                }
            }

            @Override
            public void onProgressChanged(WebView webView, int newProgress) {
                if (!subscriber.isUnsubscribed()) {
                    PowerWebViewStateChangeEvent event = PowerWebViewStateChangeEvent.progress(
                            (PowerWebView) webView, newProgress);
                    subscriber.onNext(event);
                }
            }
        };
        view.loadStateWatchers().add(watcher);

        subscriber.add(AndroidSubscriptions.unsubscribeOnMainThread(new Action0() {
            @Override
            public void call() {
                view.loadStateWatchers().remove(watcher);
            }
        }));
    }

    static void checkUiThread() {
        if (Looper.getMainLooper() != Looper.myLooper()) {
            throw new IllegalStateException(
                    "Must be called from the main thread. Was: " + Thread.currentThread());
        }
    }
}
