package com.appodeal.test;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.appodeal.ads.NativeAd;
import com.appodeal.ads.NativeAdView;
import com.appodeal.ads.NativeIconView;
import com.appodeal.ads.NativeMediaView;
import com.appodeal.ads.native_ad.views.NativeAdViewAppWall;
import com.appodeal.ads.native_ad.views.NativeAdViewContentStream;
import com.appodeal.ads.native_ad.views.NativeAdViewNewsFeed;

import java.util.LinkedList;
import java.util.List;

class NativeListAdapter {

    private final List<NativeAd> nativeAdList = new LinkedList<>();
    private final LinearLayout nativeListView;
    private int type;

    NativeListAdapter(LinearLayout nativeListView, int type) {
        this.nativeListView = nativeListView;
        this.type = type;
    }

    void addNativeAd(NativeAd nativeAd) {
        nativeAdList.add(nativeAd);
    }

    void setTemplate(int type) {
        this.type = type;
    }

    void rebuild() {
        nativeListView.removeAllViews();
        for (NativeAd nativeAd : nativeAdList) {
            nativeListView.addView(getView(nativeAd));
        }
    }

    void clear() {
        for (NativeAd nativeAd : nativeAdList) {
            nativeAd.destroy();
        }
        nativeAdList.clear();
    }

    private View getView(NativeAd nativeAd) {
        NativeAdView nativeAdView = null;
        switch (type) {
            case 0:
                nativeAdView = fillCustomNativeAdView(nativeAd);
                break;
            case 1:
                nativeAdView = new NativeAdViewNewsFeed(nativeListView.getContext(), nativeAd, ((MainActivity) nativeListView
                        .getContext()).placementName);
                break;
            case 2:
                nativeAdView = new NativeAdViewAppWall(nativeListView.getContext(), nativeAd, ((MainActivity) nativeListView
                        .getContext()).placementName);
                break;
            case 3:
                nativeAdView = new NativeAdViewContentStream(nativeListView.getContext(), nativeAd, ((MainActivity) nativeListView
                        .getContext()).placementName);
                break;
            case 4:
                nativeAdView = fillCustomWithoutIconNativeAdView(nativeAd);
                break;
        }
        return nativeAdView;
    }

    private NativeAdView fillCustomNativeAdView(NativeAd nativeAd) {
        NativeAdView nativeAdView = (NativeAdView) LayoutInflater.from(nativeListView.getContext()).inflate(R.layout.include_native_ads,
                                                                                                            nativeListView, false);
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
        NativeIconView nativeIconView = nativeAdView.findViewById(R.id.icon);
        nativeAdView.setNativeIconView(nativeIconView);
        View providerView = nativeAd.getProviderView(nativeListView.getContext());
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
        nativeAdView.setNativeMediaView(nativeMediaView);
        nativeAdView.registerView(nativeAd, ((MainActivity) nativeListView.getContext()).placementName);
        nativeAdView.setVisibility(View.VISIBLE);
        return nativeAdView;
    }

    private NativeAdView fillCustomWithoutIconNativeAdView(NativeAd nativeAd) {
        NativeAdView nativeAdView = (NativeAdView) LayoutInflater.from(nativeListView.getContext()).inflate(R.layout.native_ads_without_icon,
                                                                                                            nativeListView, false);
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
        View providerView = nativeAd.getProviderView(nativeListView.getContext());
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
        nativeAdView.setNativeMediaView(nativeMediaView);
        nativeAdView.registerView(nativeAd, ((MainActivity) nativeListView.getContext()).placementName);
        nativeAdView.setVisibility(View.VISIBLE);
        return nativeAdView;
    }
}