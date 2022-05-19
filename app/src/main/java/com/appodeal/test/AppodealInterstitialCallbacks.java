package com.appodeal.test;

import android.app.Activity;

import com.appodeal.ads.InterstitialCallbacks;

class AppodealInterstitialCallbacks implements InterstitialCallbacks {

    private final Activity activity;

    AppodealInterstitialCallbacks(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onInterstitialLoaded(boolean isPrecache) {
        Utils.showToast(activity, String.format("onInterstitialLoaded, isPrecache: %s", isPrecache));
    }

    @Override
    public void onInterstitialFailedToLoad() {
        Utils.showToast(activity, "onInterstitialFailedToLoad");
    }

    @Override
    public void onInterstitialShown() {
        Utils.showToast(activity, "onInterstitialShown");
    }

    @Override
    public void onInterstitialShowFailed() {
        Utils.showToast(activity, "onInterstitialShowFailed");
    }

    @Override
    public void onInterstitialClicked() {
        Utils.showToast(activity, "onInterstitialClicked");
    }

    @Override
    public void onInterstitialClosed() {
        Utils.showToast(activity, "onInterstitialClosed");
    }

    @Override
    public void onInterstitialExpired() {
        Utils.showToast(activity, "onInterstitialExpired");
    }

}
