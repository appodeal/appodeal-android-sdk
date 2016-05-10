package com.appodeal.test;

import android.app.Activity;

import com.appodeal.ads.BannerCallbacks;

class AppodealBannerCallbacks implements BannerCallbacks {
    private final Activity mActivity;

    AppodealBannerCallbacks(Activity activity) {
        mActivity = activity;
    }

    @Override
    public void onBannerLoaded(int height) {
        Utils.showToast(mActivity, String.format("onBannerLoaded, %ddp", height));
    }

    @Override
    public void onBannerFailedToLoad() {
        Utils.showToast(mActivity, "onBannerFailedToLoad");
    }

    @Override
    public void onBannerShown() {
        Utils.showToast(mActivity, "onBannerShown");
    }

    @Override
    public void onBannerClicked() {
        Utils.showToast(mActivity, "onBannerClicked");
    }
}
