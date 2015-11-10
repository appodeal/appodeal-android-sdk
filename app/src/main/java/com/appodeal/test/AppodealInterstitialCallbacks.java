package com.appodeal.test;

import android.app.Activity;

import com.appodeal.ads.InterstitialCallbacks;

class AppodealInterstitialCallbacks implements InterstitialCallbacks {
    private final Activity mActivity;

    AppodealInterstitialCallbacks(Activity activity) {
        mActivity = activity;
    }

    @Override
    public void onInterstitialLoaded(boolean isPrecache) {
        ((MainActivity) mActivity).showToast(String.format("onInterstitialLoaded, isPrecache: %s", isPrecache));
    }

    @Override
    public void onInterstitialFailedToLoad() {
        ((MainActivity) mActivity).showToast("onInterstitialFailedToLoad");
    }

    @Override
    public void onInterstitialShown() {
        ((MainActivity) mActivity).showToast("onInterstitialShown");
    }

    @Override
    public void onInterstitialClicked() {
        ((MainActivity) mActivity).showToast("onInterstitialClicked");
    }

    @Override
    public void onInterstitialClosed() {
        ((MainActivity) mActivity).showToast("onInterstitialClosed");
    }
}
