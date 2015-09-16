package com.yatatsu.powerwebview.rx;


import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.yatatsu.powerwebview.PowerWebView;

public class PowerWebViewStateChangeEvent {

    public static PowerWebViewStateChangeEvent start(
            @NonNull PowerWebView view, String url, Bitmap favicon) {
        return new PowerWebViewStateChangeEvent(
                view, State.STARTED, 0, url, favicon, 0, null);
    }

    public static PowerWebViewStateChangeEvent error(
            @NonNull PowerWebView view, int errorCode, String description, String failingUrl) {
        return new PowerWebViewStateChangeEvent(
                view, State.ERROR, 0, failingUrl, null, errorCode, description);
    }

    public static PowerWebViewStateChangeEvent finish(
            @NonNull PowerWebView view, String url) {
        return new PowerWebViewStateChangeEvent(
                view, State.FINISHED, 0, url, null, 0, null);
    }

    public static PowerWebViewStateChangeEvent progress(
            @NonNull PowerWebView view, int newProgress) {
        return new PowerWebViewStateChangeEvent(
                view, State.PROGRESS, newProgress, null, null, 0, null);
    }

    private final PowerWebView view;
    private final int progress;
    private final String url;
    private final Bitmap favicon;
    private final int errorCode;
    private final String description;
    private final State state;

    public enum State {
        STARTED, FINISHED, PROGRESS, ERROR,
        ;
    }

    private PowerWebViewStateChangeEvent(@NonNull PowerWebView view,
                                         State state,
                                         int progress,
                                         String url,
                                         Bitmap favicon,
                                         int errorCode,
                                         String description) {
        this.view = view;
        this.state = state;
        this.progress = progress;
        this.url = url;
        this.favicon = favicon;
        this.errorCode = errorCode;
        this.description = description;
    }

    public PowerWebView view() {
        return view;
    }

    public State state() {
        return state;
    }

    public int progress() {
        return progress;
    }

    public String url() {
        return url;
    }

    public Bitmap favicon() {
        return favicon;
    }

    public int errorCode() {
        return errorCode;
    }

    public String description() {
        return description;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof PowerWebViewStateChangeEvent)) return false;
        PowerWebViewStateChangeEvent other = (PowerWebViewStateChangeEvent) o;
        return other.view() == view()
                && state == other.state()
                && progress == other.progress()
                && TextUtils.equals(url, other.url())
                && favicon == other.favicon()
                && errorCode == other.errorCode()
                && TextUtils.equals(description, other.description());
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = result * 7 + view().hashCode();
        result = result * 7 + state.hashCode();
        result = result * 7 + progress;
        if (url != null) {
            result = result * 7 + url.hashCode();
        }
        if (favicon != null) {
            result = result * 7 + favicon.hashCode();
        }
        result = result * 7 + errorCode;
        if (description != null) {
            result = result * 7 + description.hashCode();
        }
        return result;
    }

    @Override
    public String toString() {
        return "PowerWebViewStateChangeEvent {state="
                + state
                + ", url="
                + url
                + ", favicon="
                + favicon
                + ", progress="
                + progress
                + ", errorCode="
                + errorCode
                + ", description="
                + description
                + "}";
    }
}
