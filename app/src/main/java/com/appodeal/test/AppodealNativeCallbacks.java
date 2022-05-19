package com.appodeal.test;

import android.app.Activity;

import com.appodeal.ads.NativeAd;
import com.appodeal.ads.NativeCallbacks;

public class AppodealNativeCallbacks implements NativeCallbacks {

    private final Activity activity;

    AppodealNativeCallbacks(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onNativeLoaded() {
        Utils.showToast(activity, "onNativeLoaded");
    }

    @Override
    public void onNativeFailedToLoad() {
        Utils.showToast(activity, "onNativeFailedToLoad");
    }

    @Override
    public void onNativeShown(NativeAd nativeAd) {
        Utils.showToast(activity, "onNativeShown");
    }

    @Override
    public void onNativeShowFailed(NativeAd nativeAd) {
        Utils.showToast(activity, "onNativeShowFailed");
    }

    @Override
    public void onNativeClicked(NativeAd nativeAd) {
        Utils.showToast(activity, "onNativeClicked");
    }

    @Override
    public void onNativeExpired() {
        Utils.showToast(activity, "onNativeExpired");
    }

}
