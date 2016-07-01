package com.appodeal.test;

import android.app.Activity;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.appodeal.ads.NativeAd;
import com.appodeal.ads.NativeCallbacks;

import java.util.List;

public class AppodealNativeCallbacks implements NativeCallbacks {
    private final Activity mActivity;

    AppodealNativeCallbacks(Activity activity) {
        mActivity = activity;
    }

    @Override
    public void onNativeLoaded(List<NativeAd> nativeAds) {
        Utils.showToast(mActivity, "onNativeLoaded");
        LinearLayout nativeAdsListView = (LinearLayout) mActivity.findViewById(R.id.nativeAdsListView);
        Spinner nativeTemplateSpinner = (Spinner) mActivity.findViewById(R.id.native_template_list);
        NativeListAdapter nativeListViewAdapter = new NativeListAdapter(nativeAdsListView, nativeTemplateSpinner.getSelectedItemPosition());
        for (NativeAd nativeAd : nativeAds) {
            nativeListViewAdapter.addNativeAd(nativeAd);
        }
        nativeAdsListView.setTag(nativeListViewAdapter);
        nativeListViewAdapter.rebuild();
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
