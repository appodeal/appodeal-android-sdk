package com.appodeal.test;

import android.app.Activity;

import com.appodeal.ads.VideoCallbacks;

class AppodealVideoCallbacks implements VideoCallbacks {
    private final Activity mActivity;

    AppodealVideoCallbacks(Activity activity) {
        mActivity = activity;
    }

    @Override
    public void onVideoLoaded() {
        ((MainActivity) mActivity).showToast("onVideoLoaded");
    }

    @Override
    public void onVideoFailedToLoad() {
        ((MainActivity) mActivity).showToast("onVideoFailedToLoad");
    }

    @Override
    public void onVideoShown() {
        ((MainActivity) mActivity).showToast("onVideoShown");
    }

    @Override
    public void onVideoFinished() {
        ((MainActivity) mActivity).showToast("onVideoFinished");
    }

    @Override
    public void onVideoClosed() {
        ((MainActivity) mActivity).showToast("onVideoClosed");
    }
}
