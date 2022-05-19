package com.appodeal.test;

import android.app.Activity;

import com.appodeal.ads.MrecCallbacks;

class AppodealMrecCallbacks implements MrecCallbacks {

    private final Activity activity;

    AppodealMrecCallbacks(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onMrecLoaded(boolean isPrecache) {
        Utils.showToast(activity, String.format("onMrecLoaded, isPrecache: %s", isPrecache));
    }

    @Override
    public void onMrecFailedToLoad() {
        Utils.showToast(activity, "onMrecFailedToLoad");
    }

    @Override
    public void onMrecShown() {
        Utils.showToast(activity, "onMrecShown");
    }

    @Override
    public void onMrecShowFailed() {
        Utils.showToast(activity, "onMrecShowFailed");
    }

    @Override
    public void onMrecClicked() {
        Utils.showToast(activity, "onMrecClicked");
    }

    @Override
    public void onMrecExpired() {
        Utils.showToast(activity, "onMrecExpired");
    }

}
