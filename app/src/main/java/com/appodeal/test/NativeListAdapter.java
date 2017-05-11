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

import com.appodeal.ads.NativeMediaView;
import com.appodeal.ads.NativeAd;
import com.appodeal.ads.native_ad.views.NativeAdViewAppWall;
import com.appodeal.ads.native_ad.views.NativeAdViewContentStream;
import com.appodeal.ads.native_ad.views.NativeAdViewNewsFeed;

import java.util.LinkedList;
import java.util.List;

class NativeListAdapter {

    private List<NativeAd> mAds = new LinkedList<>();
    private final LinearLayout mNativeListView;
    private int mType = 0;

    public NativeListAdapter(LinearLayout nativeListView, int type) {
        mNativeListView = nativeListView;
        mType = type;
    }

    void addNativeAd(NativeAd nativeAd) {
        mAds.add(nativeAd);
    }

    public void setTemplate(int type) {
        mType = type;
    }

    public int getCount() {
        return mAds.size();
    }

    public Object getItem(int position) {
        return mAds.get(position);
    }

    public void rebuild() {
        mNativeListView.removeAllViews();
        for (NativeAd nativeAd : mAds) {
            mNativeListView.addView(getView(nativeAd));
        }
    }

    public void clear() {
        mAds = new LinkedList<>();
    }

    private View getView(NativeAd nativeAd) {
        ViewGroup convertView = null;
        switch (mType) {
            case 0:
                convertView = (ViewGroup) LayoutInflater.from(mNativeListView.getContext()).inflate(R.layout.include_native_ads, mNativeListView, false);
                TextView tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
                tvTitle.setText(nativeAd.getTitle());

                TextView tvDescription = (TextView) convertView.findViewById(R.id.tv_description);
                tvDescription.setText(nativeAd.getDescription());

                RatingBar ratingBar = (RatingBar) convertView.findViewById(R.id.rb_rating);
                if (nativeAd.getRating() == 0) {
                    ratingBar.setVisibility(View.INVISIBLE);
                } else {
                    ratingBar.setVisibility(View.VISIBLE);
                    ratingBar.setRating(nativeAd.getRating());
                    ratingBar.setStepSize(0.1f);
                }

                Button ctaButton = (Button) convertView.findViewById(R.id.b_cta);
                ctaButton.setText(nativeAd.getCallToAction());

                ((ImageView) convertView.findViewById(R.id.icon)).setImageBitmap(nativeAd.getIcon());

                View providerView = nativeAd.getProviderView(mNativeListView.getContext());
                if (providerView != null) {
                    if (providerView.getParent() != null && providerView.getParent() instanceof ViewGroup) {
                        ((ViewGroup) providerView.getParent()).removeView(providerView);
                    }
                    FrameLayout providerViewContainer = (FrameLayout) convertView.findViewById(R.id.provider_view);
                    ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    providerViewContainer.addView(providerView, layoutParams);
                }

                TextView tvAgeRestrictions = (TextView) convertView.findViewById(R.id.tv_age_restriction);
                if (nativeAd.getAgeRestrictions() != null) {
                    tvAgeRestrictions.setText(nativeAd.getAgeRestrictions());
                    tvAgeRestrictions.setVisibility(View.VISIBLE);
                } else {
                    tvAgeRestrictions.setVisibility(View.GONE);
                }
                NativeMediaView nativeMediaView = (NativeMediaView) convertView.findViewById(R.id.appodeal_media_view_content);
                if (nativeAd.containsVideo()) {
                    nativeAd.setNativeMediaView(nativeMediaView);
                } else {
                    nativeMediaView.setVisibility(View.GONE);
                }

                nativeAd.registerViewForInteraction(convertView);
                convertView.setVisibility(View.VISIBLE);
                break;
            case 1:
                convertView = new NativeAdViewNewsFeed(mNativeListView.getContext(), nativeAd);
                break;
            case 2:
                convertView = new NativeAdViewAppWall(mNativeListView.getContext(), nativeAd);
                break;
            case 3:
                convertView = new NativeAdViewContentStream(mNativeListView.getContext(), nativeAd);
                break;
        }
        return convertView;
    }
}
