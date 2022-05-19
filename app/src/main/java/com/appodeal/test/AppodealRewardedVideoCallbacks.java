package com.appodeal.test;

import android.app.Activity;

import com.appodeal.ads.RewardedVideoCallbacks;

import java.util.Locale;

class AppodealRewardedVideoCallbacks implements RewardedVideoCallbacks {

    private final Activity activity;

    AppodealRewardedVideoCallbacks(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onRewardedVideoLoaded(boolean isPrecache) {
        Utils.showToast(activity, "onRewardedVideoLoaded, isPrecache: " + isPrecache);
    }

    @Override
    public void onRewardedVideoFailedToLoad() {
        Utils.showToast(activity, "onRewardedVideoFailedToLoad");
    }

    @Override
    public void onRewardedVideoShown() {
        Utils.showToast(activity, "onRewardedVideoShown");
    }

    @Override
    public void onRewardedVideoShowFailed() {
        Utils.showToast(activity, "onRewardedVideoShowFailed");
    }

    @Override
    public void onRewardedVideoClicked() {
        Utils.showToast(activity, "onRewardedVideoClicked");
    }

    @Override
    public void onRewardedVideoFinished(double amount, String name) {
        Utils.showToast(activity, String.format(Locale.ENGLISH, "onRewardedVideoFinished. Reward: %.2f %s", amount, name));
    }

    @Override
    public void onRewardedVideoClosed(boolean finished) {
        Utils.showToast(activity, String.format("onRewardedVideoClosed,  finished: %s", finished));
    }

    @Override
    public void onRewardedVideoExpired() {
        Utils.showToast(activity, "onRewardedVideoExpired");
    }

}
