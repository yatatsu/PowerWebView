package com.yatatsu.powerwebview;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.support.annotation.IntDef;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.webkit.HttpAuthHandler;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

public class PowerWebView extends WebView {

    @IntDef({STOPPED, LOADING, ERROR})
    @Retention(RetentionPolicy.SOURCE)
    public @interface WebViewLoadState {}
    public static final int STOPPED = 0;
    public static final int LOADING = 1;
    public static final int ERROR = 2;

    @WebViewLoadState
    private int powerWebViewState = STOPPED;
    private final List<LoadStateWatcher> loadStateWatchers = new ArrayList<>();
    private final List<PowerWebViewInterceptor> interceptors = new ArrayList<>();
    private SslErrorHandlerDelegate sslErrorHandlerDelegate;
    private HttpAuthHandlerDelegate httpAuthHandlerDelegate;

    public List<LoadStateWatcher> loadStateWatchers() {
        return loadStateWatchers;
    }

    public List<PowerWebViewInterceptor> interceptors() {
        return interceptors;
    }

    /**
     * set delegate for
     * {@link WebViewClient#onReceivedSslError(WebView, SslErrorHandler, SslError)}
     *
     * @param sslErrorHandlerDelegate delegate
     */
    public void setSslErrorHandlerDelegate(SslErrorHandlerDelegate sslErrorHandlerDelegate) {
        this.sslErrorHandlerDelegate = sslErrorHandlerDelegate;
    }

    /**
     * set delegate for
     * {@link WebViewClient#onReceivedHttpAuthRequest(WebView, HttpAuthHandler, String, String)}
     * @param httpAuthHandlerDelegate delegate
     */
    public void setHttpAuthHandlerDelegate(HttpAuthHandlerDelegate httpAuthHandlerDelegate) {
        this.httpAuthHandlerDelegate = httpAuthHandlerDelegate;
    }

    public PowerWebView(Context context) {
        this(context, null);
    }

    public PowerWebView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PowerWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setWebViewClient(createWebViewClient());
        setWebChromeClient(createWebChromeClient());

        TypedArray args;
        if (attrs != null) {
            args = context.obtainStyledAttributes(attrs, R.styleable.PowerWebView);
        } else {
            args = context.obtainStyledAttributes(R.styleable.PowerWebView);
        }
        assignWebSettings(args);
        args.recycle();

        removeJavascriptInterface("searchBoxJavaBridge_");
    }

    protected WebViewClient createWebViewClient() {
        return new PowerWebViewClient();
    }

    protected WebChromeClient createWebChromeClient() {
        return new PowerWebChromeClient();
    }

    @SuppressLint("SetJavaScriptEnabled")
    protected void assignWebSettings(TypedArray args) {
        boolean allowContentAccess =
                args.getBoolean(R.styleable.PowerWebView_allow_content_access, true);
        boolean allowFileAccess =
                args.getBoolean(R.styleable.PowerWebView_allow_file_access, true);
        boolean allowFileAccessFromFileURLs =
                args.getBoolean(R.styleable.PowerWebView_allow_file_access_from_file_urls, true);
        boolean allowUniversalAccessFromFileURLs =
                args.getBoolean(
                        R.styleable.PowerWebView_allow_universal_access_from_file_urls, false);
        boolean appCacheEnabled =
                args.getBoolean(R.styleable.PowerWebView_app_cache_enabled, false);
        boolean blockNetworkImage =
                args.getBoolean(R.styleable.PowerWebView_block_network_image, false);
        boolean blockBlockNetworkLoads =
                args.getBoolean(R.styleable.PowerWebView_block_network_loads, false);
        boolean builtInZoomControls =
                args.getBoolean(R.styleable.PowerWebView_built_in_zoom_controls, false);
        int cacheMode = args.getInt(R.styleable.PowerWebView_cache_mode, WebSettings.LOAD_DEFAULT);
        boolean databaseEnabled =
                args.getBoolean(R.styleable.PowerWebView_database_enabled, false);
        boolean displayZoomControls =
                args.getBoolean(R.styleable.PowerWebView_display_zoom_controls, false);
        boolean domStorageEnabled =
                args.getBoolean(R.styleable.PowerWebView_dom_storage_enabled, false);
        boolean geolocationEnabled =
                args.getBoolean(R.styleable.PowerWebView_geolocation_enabled, true);
        boolean javaScriptCanOpenWindowsAutomatically =
                args.getBoolean(
                        R.styleable.PowerWebView_java_script_can_open_windows_automatically, false);
        boolean jsEnabled = args.getBoolean(R.styleable.PowerWebView_java_script_enabled, true);
        boolean loadWithOverviewMode =
                args.getBoolean(R.styleable.PowerWebView_load_with_overview_mode, false);
        boolean loadsImagesAutomatically  =
                args.getBoolean(R.styleable.PowerWebView_loads_images_automatically, true);
        boolean needInitialFocus =
                args.getBoolean(R.styleable.PowerWebView_need_initial_focus, false);
        boolean saveFormEnabled = args.getBoolean(R.styleable.PowerWebView_save_form_data, true);
        boolean supportMultipleWindows =
                args.getBoolean(R.styleable.PowerWebView_support_multiple_windows, false);
        boolean supportZoom = args.getBoolean(R.styleable.PowerWebView_support_zoom, true);
        boolean useWideViewPort =
                args.getBoolean(R.styleable.PowerWebView_use_wide_view_port, true);

        WebSettings setting = getSettings();
        setting.setAllowContentAccess(allowContentAccess);
        setting.setAllowFileAccess(allowFileAccess);
        setting.setAllowFileAccessFromFileURLs(allowFileAccessFromFileURLs);
        setting.setAllowUniversalAccessFromFileURLs(allowUniversalAccessFromFileURLs);
        setting.setAppCacheEnabled(appCacheEnabled);
        setting.setBlockNetworkImage(blockNetworkImage);
        setting.setBlockNetworkLoads(blockBlockNetworkLoads);
        setting.setBuiltInZoomControls(builtInZoomControls);
        setting.setCacheMode(cacheMode);
        setting.setDatabaseEnabled(databaseEnabled);
        setting.setDisplayZoomControls(displayZoomControls);
        setting.setDomStorageEnabled(domStorageEnabled);
        setting.setGeolocationEnabled(geolocationEnabled);
        setting.setJavaScriptCanOpenWindowsAutomatically(javaScriptCanOpenWindowsAutomatically);
        setting.setJavaScriptEnabled(jsEnabled);
        setting.setLoadWithOverviewMode(loadWithOverviewMode);
        setting.setLoadsImagesAutomatically(loadsImagesAutomatically);
        setting.setNeedInitialFocus(needInitialFocus);
        setting.setSaveFormData(saveFormEnabled);
        setting.setSupportMultipleWindows(supportMultipleWindows);
        setting.setSupportZoom(supportZoom);
        setting.setUseWideViewPort(useWideViewPort);
    }

    @Override
    public void destroy() {
        stopLoading();
        setWebChromeClient(null);
        setWebViewClient(null);
        sslErrorHandlerDelegate = null;
        httpAuthHandlerDelegate = null;
        loadStateWatchers.clear();
        interceptors.clear();
        super.destroy();
    }

    @Override
    public void loadUrl(String url) {
        if (intercept(Uri.parse(url))) {
            return;
        }
        super.loadUrl(url);
    }

    boolean intercept(Uri uri) {
        for (PowerWebViewInterceptor interceptor : interceptors) {
            if (interceptor.intercept(uri)) {
                return true;
            }
        }
        return false;
    }

    protected class PowerWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return !TextUtils.isEmpty(url) && intercept(Uri.parse(url));
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            if (powerWebViewState == STOPPED) {
                powerWebViewState = LOADING;
                for (LoadStateWatcher watcher : loadStateWatchers) {
                    watcher.onStarted(view, url, favicon);
                }
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (powerWebViewState == LOADING) {
                for (LoadStateWatcher watcher : loadStateWatchers) {
                    watcher.onProgressChanged(view, 100);
                    watcher.onFinished(view, url);
                }
            }
            powerWebViewState = STOPPED;
        }

        @SuppressWarnings("deprecation")
        @Override
        public void onReceivedError(
                WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            powerWebViewState = ERROR;
            for (LoadStateWatcher watcher : loadStateWatchers) {
                watcher.onError(view, errorCode, description, failingUrl);
            }
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            if (sslErrorHandlerDelegate != null) {
                sslErrorHandlerDelegate.onReceivedSslError(view, handler, error);
            } else {
                super.onReceivedSslError(view, handler, error);
            }
        }

        @Override
        public void onReceivedHttpAuthRequest(
                WebView view, HttpAuthHandler handler, String host, String realm) {
            if (httpAuthHandlerDelegate != null) {
                httpAuthHandlerDelegate.onReceivedHttpAuthRequest(view, handler, host, realm);
            } else {
                super.onReceivedHttpAuthRequest(view, handler, host, realm);
            }
        }
    }

    protected class PowerWebChromeClient extends WebChromeClient {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            if (powerWebViewState == LOADING) {
                for (LoadStateWatcher watcher : loadStateWatchers) {
                    watcher.onProgressChanged(view, newProgress);
                }
            }
        }
    }
}
