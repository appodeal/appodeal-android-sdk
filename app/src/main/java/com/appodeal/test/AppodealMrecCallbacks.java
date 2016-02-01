package com.appodeal.test;

import android.app.Activity;

import com.appodeal.ads.MrecCallbacks;

class AppodealMrecCallbacks implements MrecCallbacks {
    private final Activity mActivity;

    AppodealMrecCallbacks(Activity activity) {
        mActivity = activity;
    }

    @Override
    public void onMrecLoaded() {
        ((MainActivity) mActivity).showToast("onMrecLoaded");
    }

    @Override
    public void onMrecFailedToLoad() {
        ((MainActivity) mActivity).showToast("onMrecFailedToLoad");
    }

    @Override
    public void onMrecShown() {
        ((MainActivity) mActivity).showToast("onMrecShown");
    }

    @Override
    public void onMrecClicked() {
        ((MainActivity) mActivity).showToast("onMrecClicked");
    }
}
