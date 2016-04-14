package com.appodeal.test;

import android.app.Activity;
import android.util.Pair;
import android.view.View;
import android.widget.ListView;

import com.appodeal.ads.NativeAd;
import com.appodeal.ads.NativeCallbacks;

import java.util.ArrayList;
import java.util.List;

public class AppodealNativeCallbacks implements NativeCallbacks {
    private final Activity mActivity;

    AppodealNativeCallbacks(Activity activity) {
        mActivity = activity;
    }

    @Override
    public void onNativeLoaded(List<NativeAd> nativeAds) {
        Utils.showToast(mActivity, "onNativeLoaded");
        final ListView nativeListView = (ListView) mActivity.findViewById(R.id.nativeAdsListView);
        NativeListViewAdapter nativeListViewAdapter = ((NativeListViewAdapter) nativeListView.getAdapter());
        for (NativeAd nativeAd : nativeAds) {
            nativeListViewAdapter.addNativeAd(nativeAd);
        }
        Utils.setListViewHeightBasedOnChildren(nativeListView);
    }

    @Override
    public void onNativeFailedToLoad() {
        Utils.showToast(mActivity, "onNativeFailedToLoad");
    }

    @Override
    public void onNativeShown(NativeAd nativeAd) {
        Utils.showToast(mActivity, "onNativeShown");
    }

    @Override
    public void onNativeClicked(NativeAd nativeAd) {
        Utils.showToast(mActivity, "onNativeClicked");
    }
}
