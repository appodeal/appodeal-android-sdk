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
        Utils.showToast(mActivity, "onMrecLoaded");
    }

    @Override
    public void onMrecFailedToLoad() {
        Utils.showToast(mActivity, "onMrecFailedToLoad");
    }

    @Override
    public void onMrecShown() {
        Utils.showToast(mActivity, "onMrecShown");
    }

    @Override
    public void onMrecClicked() {
        Utils.showToast(mActivity, "onMrecClicked");
    }
}
