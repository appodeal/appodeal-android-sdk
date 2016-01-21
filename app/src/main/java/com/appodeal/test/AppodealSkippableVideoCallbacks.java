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
        ((MainActivity) mActivity).showToast("onVideoLoaded");
    }

    @Override
    public void onSkippableVideoFailedToLoad() {
        ((MainActivity) mActivity).showToast("onVideoFailedToLoad");
    }

    @Override
    public void onSkippableVideoShown() {
        ((MainActivity) mActivity).showToast("onVideoShown");
    }

    @Override
    public void onSkippableVideoFinished() {
        ((MainActivity) mActivity).showToast("onVideoFinished");
    }

    @Override
    public void onSkippableVideoClosed() {
        ((MainActivity) mActivity).showToast("onVideoClosed");
    }
}
