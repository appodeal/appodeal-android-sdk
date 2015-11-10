package com.appodeal.test;

import android.app.Activity;

import com.appodeal.ads.RewardedVideoCallbacks;

class AppodealRewardedVideoCallbacks implements RewardedVideoCallbacks {
    private final Activity mActivity;

    AppodealRewardedVideoCallbacks(Activity activity) {
        mActivity = activity;
    }

    @Override
    public void onVideoLoaded() {
        ((MainActivity) mActivity).showToast("onRewardedVideoLoaded");
    }

    @Override
    public void onVideoFailedToLoad() {
        ((MainActivity) mActivity).showToast("onRewardedVideoFailedToLoad");
    }

    @Override
    public void onVideoShown() {
        ((MainActivity) mActivity).showToast("onRewardedVideoShown");
    }

    @Override
    public void onVideoFinished(int amount, String name) {
        ((MainActivity) mActivity).showToast(String.format("onRewardedVideoFinished. Reward: %d %s", amount, name));
    }

    @Override
    public void onVideoClosed() {
        ((MainActivity) mActivity).showToast("onRewardedVideoClosed");
    }
}
