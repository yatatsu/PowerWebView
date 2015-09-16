package com.yatatsu.powerwebviewexample;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.HttpAuthHandler;
import android.webkit.URLUtil;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.yatatsu.powerwebview.HttpAuthHandlerDelegate;
import com.yatatsu.powerwebview.LoadStateWatcher;
import com.yatatsu.powerwebview.PowerWebView;
import com.yatatsu.powerwebview.PowerWebViewInterceptor;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.webview)
    PowerWebView webView;

    @Bind(R.id.progress_bar)
    ProgressBar progressBar;

    @Bind(R.id.error_view)
    RelativeLayout errorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        String uaOrigin = webView.getSettings().getUserAgentString();
        webView.getSettings().setUserAgentString(
                uaOrigin + " PowerWebView is a awesome wrapper for Android WebView");

        webView.interceptors().add(new LoggingInterceptor());
        webView.interceptors().add(urlSchemeHandler);
        webView.loadStateWatchers().add(viewBindingLoadStateWatcher);
        webView.setHttpAuthHandlerDelegate(new HttpAuthHandlerDelegate() {
            @Override
            public void onReceivedHttpAuthRequest(
                    WebView view, HttpAuthHandler handler, String host, String realm) {
                Timber.d("basic auth: " + realm);
                handler.proceed("user", "passwd");
            }
        });

        webView.loadUrl("http://httpbin.org/");
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        webView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        webView.onPause();
        webView.pauseTimers();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webView.destroy();
    }

    @OnClick(R.id.reload_button)
    public void onClickReloadButton() {
        webView.reload();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_reload:
                webView.reload();
                return true;
            case R.id.action_playstore:
                webView.loadUrl("market://details?id=com.android.chrome&hl=ja");
                return true;
            case R.id.action_basic_auth:
                webView.loadUrl("https://httpbin.org/basic-auth/user/passwd");
                return true;
            case R.id.action_rx:
                startActivity(new Intent(this, RxBindingActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    final PowerWebViewInterceptor urlSchemeHandler = new PowerWebViewInterceptor() {
        @Override
        public boolean intercept(Uri uri) {
            if (!URLUtil.isNetworkUrl(uri.toString())) {
                Timber.d("other protocol.");
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
                return true;
            }
            return false;
        }
    };

    final LoadStateWatcher viewBindingLoadStateWatcher = new LoadStateWatcher() {
        @Override
        public void onStarted(WebView view, String url, Bitmap favicon) {
            Timber.d("onStarted");
            progressBar.setProgress(0);
            progressBar.setVisibility(View.VISIBLE);
            errorView.setVisibility(View.GONE);
        }

        @Override
        public void onFinished(WebView view, String url) {
            Timber.d("onFinished");
            webView.setVisibility(View.VISIBLE);
            errorView.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onError(WebView view, int errorCode, String description, String failingUrl) {
            Timber.d("onError");
            progressBar.setVisibility(View.GONE);
            errorView.setVisibility(View.VISIBLE);
            webView.setVisibility(View.GONE);
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            Timber.d("onProgressChanged");
            if (newProgress > 80 && webView.getVisibility() != View.VISIBLE) {
                webView.setVisibility(View.VISIBLE);
            }
            progressBar.setProgress(newProgress);
        }
    };
}
