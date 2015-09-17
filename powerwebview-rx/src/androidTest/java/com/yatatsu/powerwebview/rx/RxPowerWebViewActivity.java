package com.yatatsu.powerwebview.rx;

import android.app.Activity;
import android.os.Bundle;

import com.yatatsu.powerwebview.PowerWebView;


public class RxPowerWebViewActivity extends Activity {

    PowerWebView powerWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        powerWebView = new PowerWebView(this);
        setContentView(powerWebView);
    }
}
