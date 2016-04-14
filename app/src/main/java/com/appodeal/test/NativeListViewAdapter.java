package com.appodeal.test;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.appodeal.ads.NativeAd;
import com.appodeal.ads.native_ad.views.NativeAdViewAppWall;
import com.appodeal.ads.native_ad.views.NativeAdViewContentStream;
import com.appodeal.ads.native_ad.views.NativeAdViewNewsFeed;

import java.util.LinkedList;
import java.util.List;

public class NativeListViewAdapter extends BaseAdapter {

    private List<NativeAd> mAds = new LinkedList<>();
    private Context mContext;
    private int mType = 0;

    public NativeListViewAdapter(Context context, int type) {
        mContext = context;
        mType = type;
    }

    void addNativeAd(NativeAd nativeAd) {
        mAds.add(nativeAd);
        notifyDataSetChanged();
    }

    public void setTemplate(int type) {
        mType = type;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mAds.size();
    }

    @Override
    public Object getItem(int position) {
        return mAds.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        switch (mType) {
            case 0:
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.include_native_ads, parent, false);
                TextView tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
                tvTitle.setText(mAds.get(position).getTitle());

                TextView tvDescription = (TextView) convertView.findViewById(R.id.tv_description);
                tvDescription.setText(mAds.get(position).getDescription());

                RatingBar ratingBar = (RatingBar) convertView.findViewById(R.id.rb_rating);
                if (mAds.get(position).getRating() == 0) {
                    ratingBar.setVisibility(View.INVISIBLE);
                } else {
                    ratingBar.setVisibility(View.VISIBLE);
                    ratingBar.setRating(mAds.get(position).getRating());
                    ratingBar.setStepSize(0.1f);
                }

                Button ctaButton = (Button) convertView.findViewById(R.id.b_cta);
                ctaButton.setText(mAds.get(position).getCallToAction());

                ((ImageView) convertView.findViewById(R.id.icon)).setImageBitmap(mAds.get(position).getIcon());

                View providerView = mAds.get(position).getProviderView(mContext);
                if (providerView != null) {
                    FrameLayout providerViewContainer = (FrameLayout) convertView.findViewById(R.id.provider_view);
                    providerViewContainer.addView(providerView);
                }

                TextView tvAgeRestrictions = (TextView) convertView.findViewById(R.id.tv_age_restriction);
                if (mAds.get(position).getAgeRestrictions() != null) {
                    tvAgeRestrictions.setText(mAds.get(position).getAgeRestrictions());
                    tvAgeRestrictions.setVisibility(View.VISIBLE);
                } else {
                    tvAgeRestrictions.setVisibility(View.GONE);
                }

                mAds.get(position).registerViewForInteraction(convertView);
                convertView.setVisibility(View.VISIBLE);
                break;
            case 1:
                convertView = new NativeAdViewNewsFeed(mContext, mAds.get(position));
                break;
            case 2:
                convertView = new NativeAdViewAppWall(mContext, mAds.get(position));
                break;
            case 3:
                convertView = new NativeAdViewContentStream(mContext, mAds.get(position));
                break;
        }
        return convertView;
    }
}
