package com.yatatsu.powerwebview.rx;


import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

import com.yatatsu.powerwebview.PowerWebView;

import rx.Observable;

public final class RxPowerWebView {

    @CheckResult @NonNull
    public static Observable<PowerWebViewStateChangeEvent> changeState(@NonNull PowerWebView view) {
        return Observable.create(new PowerWebViewStateChangeEventOnSubscribe(view));
    }

    private RxPowerWebView() {
        throw new AssertionError("No instances.");
    }
}
