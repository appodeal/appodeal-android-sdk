package com.appodeal.test;

import android.app.Activity;

import com.appodeal.ads.SkippableVideoCallbacks;

class AppodealSkippableVideoCallbacks implements SkippableVideoCallbacks {
    private final Activity mActivity;

    AppodealSkippableVideoCallbacks(Activity activity) {
        mActivity = activity;
    }

    @Override
    public void onSkippableVideoLoaded() {
        Utils.showToast(mActivity, "onSkippableVideoLoaded");
    }

    @Override
    public void onSkippableVideoFailedToLoad() {
        Utils.showToast(mActivity, "onSkippableVideoFailedToLoad");
    }

    @Override
    public void onSkippableVideoShown() {
        Utils.showToast(mActivity, "onSkippableVideoShown");
    }

    @Override
    public void onSkippableVideoFinished() {
        Utils.showToast(mActivity, "onSkippableVideoFinished");
    }

    @Override
    public void onSkippableVideoClosed(boolean finished) {
        Utils.showToast(mActivity, String.format("onSkippableVideoClosed,  finished: %s", finished));
    }
}
