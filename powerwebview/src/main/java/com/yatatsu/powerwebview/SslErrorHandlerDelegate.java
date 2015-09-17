package com.yatatsu.powerwebview;

import android.net.http.SslError;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;


public interface SslErrorHandlerDelegate {
    void onReceivedSslError(WebView webView, SslErrorHandler handler, SslError sslError);
}
