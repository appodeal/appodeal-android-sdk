package com.appodeal.test;

import android.app.Activity;

import com.appodeal.ads.BannerCallbacks;

class AppodealBannerCallbacks implements BannerCallbacks {

    private final Activity activity;

    AppodealBannerCallbacks(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onBannerLoaded(int height, boolean isPrecache) {
        Utils.showToast(activity, String.format("onBannerLoaded, %sdp, isPrecache: %s", height, isPrecache));
    }

    @Override
    public void onBannerFailedToLoad() {
        Utils.showToast(activity, "onBannerFailedToLoad");
    }

    @Override
    public void onBannerShown() {
        Utils.showToast(activity, "onBannerShown");
    }

    @Override
    public void onBannerShowFailed() {
        Utils.showToast(activity, "onBannerShowFailed");
    }

    @Override
    public void onBannerClicked() {
        Utils.showToast(activity, "onBannerClicked");
    }

    @Override
    public void onBannerExpired() {
        Utils.showToast(activity, "onBannerExpired");
    }

}
