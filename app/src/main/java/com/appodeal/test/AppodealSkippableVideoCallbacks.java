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
        ((MainActivity) mActivity).showToast("onSkippableVideoLoaded");
    }

    @Override
    public void onSkippableVideoFailedToLoad() {
        ((MainActivity) mActivity).showToast("onSkippableVideoFailedToLoad");
    }

    @Override
    public void onSkippableVideoShown() {
        ((MainActivity) mActivity).showToast("onSkippableVideoShown");
    }

    @Override
    public void onSkippableVideoFinished() {
        ((MainActivity) mActivity).showToast("onSkippableVideoFinished");
    }

    @Override
    public void onSkippableVideoClosed() {
        ((MainActivity) mActivity).showToast("onSkippableVideoClosed");
    }
}
