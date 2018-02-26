package com.appodeal.test;

import android.app.Activity;

import com.appodeal.ads.RewardedVideoCallbacks;

class AppodealRewardedVideoCallbacks implements RewardedVideoCallbacks {
    private final Activity mActivity;

    AppodealRewardedVideoCallbacks(Activity activity) {
        mActivity = activity;
    }

    @Override
    public void onRewardedVideoLoaded(boolean isPrecache) {
        Utils.showToast(mActivity, "onRewardedVideoLoaded, isPrecache: " + isPrecache);
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
    public void onRewardedVideoFinished(double amount, String name) {
        Utils.showToast(mActivity, String.format("onRewardedVideoFinished. Reward: %d %s", amount, name));
    }

    @Override
    public void onRewardedVideoClosed(boolean finished) {
        Utils.showToast(mActivity, String.format("onRewardedVideoClosed,  finished: %s", finished));
    }
}
