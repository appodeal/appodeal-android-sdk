package com.appodeal.test;

import android.app.Activity;

import com.appodeal.ads.RewardedVideoCallbacks;

class AppodealRewardedVideoCallbacks implements RewardedVideoCallbacks {
    private final Activity mActivity;

    AppodealRewardedVideoCallbacks(Activity activity) {
        mActivity = activity;
    }

    @Override
    public void onRewardedVideoLoaded() {
        ((MainActivity) mActivity).showToast("onRewardedVideoLoaded");
    }

    @Override
    public void onRewardedVideoFailedToLoad() {
        ((MainActivity) mActivity).showToast("onRewardedVideoFailedToLoad");
    }

    @Override
    public void onRewardedVideoShown() {
        ((MainActivity) mActivity).showToast("onRewardedVideoShown");
    }

    @Override
    public void onRewardedVideoFinished(int amount, String name) {
        ((MainActivity) mActivity).showToast(String.format("onRewardedVideoFinished. Reward: %d %s", amount, name));
    }

    @Override
    public void onRewardedVideoClosed() {
        ((MainActivity) mActivity).showToast("onRewardedVideoClosed");
    }
}
