package com.yatatsu.powerwebview.rx;

import android.app.Instrumentation;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.UiThreadTest;

import com.cookpad.android.rxt4a.schedulers.AndroidSchedulers;
import com.yatatsu.powerwebview.PowerWebView;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.NoSuchElementException;

import rx.Subscription;

import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class)
public final class RxPowerWebViewTest {

    final static String WEB_HOST = "http://httpbin.org/";

    @Rule
    public final ActivityTestRule<RxPowerWebViewActivity> activityTestRule =
            new ActivityTestRule<>(RxPowerWebViewActivity.class);

    private final Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();

    private PowerWebView view;

    @Before
    public void setUp() {
        view = activityTestRule.getActivity().powerWebView;
    }

    @Test
    @UiThreadTest
    public void testChangeState() {
        RecordingObserver<PowerWebViewStateChangeEvent> o = new RecordingObserver<>();
        Subscription subscription = RxPowerWebView.changeState(view)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(o);
        o.assertNoMoreEvents();

        instrumentation.runOnMainSync(new Runnable() {
            @Override
            public void run() {
                view.loadUrl(WEB_HOST);
            }
        });

        try {
            //noinspection InfiniteLoopStatement
            while (true) {
                PowerWebViewStateChangeEvent next = o.takeNext();
                switch (next.state()) {
                    case STARTED:
                        assertThat(next).isEqualTo(
                                PowerWebViewStateChangeEvent.start(view, WEB_HOST, null));
                        break;
                    case PROGRESS:
                        assertThat(next.progress()).isGreaterThan(0);
                        break;
                    case FINISHED:
                        assertThat(next).isEqualTo(
                                PowerWebViewStateChangeEvent.finish(view, WEB_HOST));
                        break;
                }
            }
        } catch (NoSuchElementException ignore) {
        }

        subscription.unsubscribe();

        instrumentation.runOnMainSync(new Runnable() {
            @Override
            public void run() {
                view.loadUrl(WEB_HOST);
            }
        });
        o.assertNoMoreEvents();
    }
}
