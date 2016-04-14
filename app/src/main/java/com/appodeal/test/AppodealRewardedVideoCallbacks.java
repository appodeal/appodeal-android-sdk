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
        Utils.showToast(mActivity, "onRewardedVideoLoaded");
    }

    @Override
    public void onRewardedVideoFailedToLoad() {
        Utils.showToast(mActivity, "onRewardedVideoFailedToLoad");
    }

    @Override
    public void onRewardedVideoShown() {
        Utils.showToast(mActivity, "onRewardedVideoShown");
    }

    @Override
    public void onRewardedVideoFinished(int amount, String name) {
        Utils.showToast(mActivity, String.format("onRewardedVideoFinished. Reward: %d %s", amount, name));
    }

    @Override
    public void onRewardedVideoClosed() {
        Utils.showToast(mActivity, "onRewardedVideoClosed");
    }
}
