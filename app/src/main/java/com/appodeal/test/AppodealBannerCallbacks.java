package com.appodeal.test;

import android.app.Activity;

import com.appodeal.ads.BannerCallbacks;

class AppodealBannerCallbacks implements BannerCallbacks {
    private final Activity mActivity;

    AppodealBannerCallbacks(Activity activity) {
        mActivity = activity;
    }

    @Override
    public void onBannerLoaded() {
        ((MainActivity) mActivity).showToast("onBannerLoaded");
    }

    @Override
    public void onBannerFailedToLoad() {
        ((MainActivity) mActivity).showToast("onBannerFailedToLoad");
    }

    @Override
    public void onBannerShown() {
        ((MainActivity) mActivity).showToast("onBannerShown");
    }

    @Override
    public void onBannerClicked() {
        ((MainActivity) mActivity).showToast("onBannerClicked");
    }
}
