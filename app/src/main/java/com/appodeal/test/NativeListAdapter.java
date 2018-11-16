package com.appodeal.test;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.appodeal.ads.NativeAd;
import com.appodeal.ads.NativeAdView;
import com.appodeal.ads.NativeMediaView;
import com.appodeal.ads.native_ad.views.NativeAdViewAppWall;
import com.appodeal.ads.native_ad.views.NativeAdViewContentStream;
import com.appodeal.ads.native_ad.views.NativeAdViewNewsFeed;

import java.util.LinkedList;
import java.util.List;

class NativeListAdapter {

    private List<NativeAd> mAds = new LinkedList<>();
    private final LinearLayout mNativeListView;
    private int mType = 0;

    NativeListAdapter(LinearLayout nativeListView, int type) {
        mNativeListView = nativeListView;
        mType = type;
    }

    void addNativeAd(NativeAd nativeAd) {
        mAds.add(nativeAd);
    }

    void setTemplate(int type) {
        mType = type;
    }

    int getCount() {
        return mAds.size();
    }

    Object getItem(int position) {
        return mAds.get(position);
    }

    void rebuild() {
        mNativeListView.removeAllViews();
        for (NativeAd nativeAd : mAds) {
            mNativeListView.addView(getView(nativeAd));
        }
    }

    void clear() {
        for (NativeAd nativeAd : mAds) {
            nativeAd.destroy();
        }

        mAds = new LinkedList<>();
    }

    private View getView(NativeAd nativeAd) {
        NativeAdView nativeAdView = null;
        switch (mType) {
            case 0:
                nativeAdView = (NativeAdView) LayoutInflater.from(mNativeListView.getContext()).inflate(R.layout.include_native_ads, mNativeListView, false);
                TextView tvTitle = nativeAdView.findViewById(R.id.tv_title);
                tvTitle.setText(nativeAd.getTitle());
                nativeAdView.setTitleView(tvTitle);

                TextView tvDescription = nativeAdView.findViewById(R.id.tv_description);
                tvDescription.setText(nativeAd.getDescription());
                nativeAdView.setDescriptionView(tvDescription);

                RatingBar ratingBar = nativeAdView.findViewById(R.id.rb_rating);
                if (nativeAd.getRating() == 0) {
                    ratingBar.setVisibility(View.INVISIBLE);
                } else {
                    ratingBar.setVisibility(View.VISIBLE);
                    ratingBar.setRating(nativeAd.getRating());
                    ratingBar.setStepSize(0.1f);
                }
                nativeAdView.setRatingView(ratingBar);

                Button ctaButton = nativeAdView.findViewById(R.id.b_cta);
                ctaButton.setText(nativeAd.getCallToAction());
                nativeAdView.setCallToActionView(ctaButton);

                ImageView icon = nativeAdView.findViewById(R.id.icon);
                icon.setImageBitmap(nativeAd.getIcon());
                nativeAdView.setIconView(icon);

                View providerView = nativeAd.getProviderView(mNativeListView.getContext());
                if (providerView != null) {
                    if (providerView.getParent() != null && providerView.getParent() instanceof ViewGroup) {
                        ((ViewGroup) providerView.getParent()).removeView(providerView);
                    }
                    FrameLayout providerViewContainer = nativeAdView.findViewById(R.id.provider_view);
                    ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    providerViewContainer.addView(providerView, layoutParams);
                }
                nativeAdView.setProviderView(providerView);

                TextView tvAgeRestrictions = nativeAdView.findViewById(R.id.tv_age_restriction);
                if (nativeAd.getAgeRestrictions() != null) {
                    tvAgeRestrictions.setText(nativeAd.getAgeRestrictions());
                    tvAgeRestrictions.setVisibility(View.VISIBLE);
                } else {
                    tvAgeRestrictions.setVisibility(View.GONE);
                }
                NativeMediaView nativeMediaView = nativeAdView.findViewById(R.id.appodeal_media_view_content);
                if (nativeAd.containsVideo()) {
                    nativeAdView.setNativeMediaView(nativeMediaView);
                } else {
                    nativeMediaView.setVisibility(View.GONE);
                }

                nativeAdView.registerView(nativeAd, ((MainActivity) mNativeListView.getContext()).mPlacementName);
                nativeAdView.setVisibility(View.VISIBLE);
                break;
            case 1:
                nativeAdView = new NativeAdViewNewsFeed(mNativeListView.getContext(), nativeAd, ((MainActivity) mNativeListView.getContext()).mPlacementName);
                break;
            case 2:
                nativeAdView = new NativeAdViewAppWall(mNativeListView.getContext(), nativeAd, ((MainActivity) mNativeListView.getContext()).mPlacementName);
                break;
            case 3:
                nativeAdView = new NativeAdViewContentStream(mNativeListView.getContext(), nativeAd, ((MainActivity) mNativeListView.getContext()).mPlacementName);
                break;
        }
        return nativeAdView;
    }
}
