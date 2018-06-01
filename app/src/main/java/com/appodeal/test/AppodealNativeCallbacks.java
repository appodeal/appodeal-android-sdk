package com.appodeal.test;

import android.app.Activity;

import com.appodeal.ads.NativeAd;
import com.appodeal.ads.NativeCallbacks;

public class AppodealNativeCallbacks implements NativeCallbacks {
    private final Activity mActivity;

    AppodealNativeCallbacks(Activity activity) {
        mActivity = activity;
    }

    @Override
    public void onNativeLoaded() {
        Utils.showToast(mActivity, "onNativeLoaded");
    }

    @Override
    public void onNativeFailedToLoad() {
        Utils.showToast(mActivity, "onNativeFailedToLoad");
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
