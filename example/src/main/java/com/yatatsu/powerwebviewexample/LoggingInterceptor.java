package com.yatatsu.powerwebviewexample;

import android.net.Uri;

import com.yatatsu.powerwebview.PowerWebViewInterceptor;

import timber.log.Timber;


public class LoggingInterceptor implements PowerWebViewInterceptor {

    @Override
    public boolean intercept(Uri uri) {
        Timber.d(uri.toString());
        return false;
    }
}
