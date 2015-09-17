package com.yatatsu.powerwebview;


import android.net.Uri;

public interface PowerWebViewInterceptor {
    boolean intercept(Uri uri);
}
