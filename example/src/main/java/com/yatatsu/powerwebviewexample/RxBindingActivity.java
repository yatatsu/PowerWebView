package com.yatatsu.powerwebviewexample;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.yatatsu.powerwebview.LoadStateWatcher;
import com.yatatsu.powerwebview.PowerWebView;
import com.yatatsu.powerwebview.rx.PowerWebViewStateChangeEvent;
import com.yatatsu.powerwebview.rx.RxPowerWebView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.observers.Subscribers;
import timber.log.Timber;


public class RxBindingActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.webview)
    PowerWebView webView;

    @Bind(R.id.progress_bar)
    ProgressBar progressBar;

    @Bind(R.id.error_view)
    RelativeLayout errorView;

    private Subscription subscription = Subscribers.empty();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        setTitle("RxBinding example of PowerWebView");

        String uaOrigin = webView.getSettings().getUserAgentString();
        webView.getSettings().setUserAgentString(
                uaOrigin + " PowerWebView is a awesome wrapper for Android WebView");

        webView.interceptors().add(new LoggingInterceptor());
        webView.loadStateWatchers().add(viewBindingLoadStateWatcher);
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
        Timber.d("onResume");
        webView.onResume();
        subscription = createUrlStringSubscription();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Timber.d("onPause");
        webView.onPause();
        webView.pauseTimers();
        subscription.unsubscribe();
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

    Subscription createUrlStringSubscription() {
        return RxPowerWebView.changeState(webView)
                .subscribeOn(AndroidSchedulers.mainThread())
                .filter(new Func1<PowerWebViewStateChangeEvent, Boolean>() {
                    @Override
                    public Boolean call(PowerWebViewStateChangeEvent e) {
                        return e.state() == PowerWebViewStateChangeEvent.State.FINISHED;
                    }
                }).map(new Func1<PowerWebViewStateChangeEvent, String>() {
                    @Override
                    public String call(PowerWebViewStateChangeEvent e) {
                        return e.url();
                    }
                }).subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        setTitle(s);
                    }
                });
    }
}
