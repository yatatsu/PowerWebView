package com.yatatsu.powerwebview;

import android.graphics.Bitmap;
import android.webkit.WebView;


public interface LoadStateWatcher {
    void onStarted(WebView webView, String url, Bitmap favicon);
    void onFinished(WebView webView, String url);
    void onError(WebView webView, int errorCode, String description, String failingUrl);
    void onProgressChanged(WebView webView, int newProgress);
}
