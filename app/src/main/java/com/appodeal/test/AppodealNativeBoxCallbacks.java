package com.appodeal.test;

import android.app.Activity;

import com.appodeal.ads.NativeAd;
import com.appodeal.ads.NativeAdBoxListener;

class AppodealNativeBoxCallbacks implements NativeAdBoxListener {
    private final Activity mActivity;

    AppodealNativeBoxCallbacks(Activity activity) {
        mActivity = activity;
    }

    @Override
    public void onNativeAdBoxLoaded(int size) {
        Utils.showToast(mActivity, String.format("onNativeAdBoxLoaded, size: %s", size));
    }

    @Override
    public void onNativeShown(NativeAd nativeAd) {
        Utils.showToast(mActivity, "onNativeShown");
    }

    @Override
    public void onNativeClicked(NativeAd nativeAd) {
        Utils.showToast(mActivity, "onNativeClicked");
    }
}
