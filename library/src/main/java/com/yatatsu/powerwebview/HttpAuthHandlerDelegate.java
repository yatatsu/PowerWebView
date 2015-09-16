package com.yatatsu.powerwebview;

import android.webkit.HttpAuthHandler;
import android.webkit.WebView;


public interface HttpAuthHandlerDelegate {
    void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm);
}
