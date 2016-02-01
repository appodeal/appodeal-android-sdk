package com.appodeal.test;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.appodeal.ads.Appodeal;
import com.appodeal.ads.NativeAd;
import com.appodeal.ads.NativeCallbacks;
import com.appodeal.ads.native_ad.views.NativeAdViewAppWall;
import com.appodeal.ads.native_ad.views.NativeAdViewContentStream;
import com.appodeal.ads.native_ad.views.NativeAdViewNewsFeed;

import java.util.LinkedList;
import java.util.List;

public class NativeAdsActivity extends Activity implements NativeCallbacks {

    private NativeListViewAdapter adapter;
    private int type;
    private boolean showToast;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_ads);

        type = 0;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            type = extras.getInt("type");
            showToast = extras.getBoolean("showToast", false);
        }

        ListView nativeAdsListView = (ListView) findViewById(R.id.nativeAdsListView);
        adapter = new NativeListViewAdapter(this, type);
        nativeAdsListView.setAdapter(adapter);

        Appodeal.setAutoCacheNativeIcons(true);
        if (type < 2) {
            Appodeal.setAutoCacheNativeImages(false);
        } else {
            Appodeal.setAutoCacheNativeImages(true);
        }
        Appodeal.setNativeCallbacks(this);
        Appodeal.cache(this, Appodeal.NATIVE, 5);
    }

    @Override
    public void onNativeLoaded(List<NativeAd> nativeAds) {
        if (showToast) {
            Toast.makeText(this, "onNativeLoaded", Toast.LENGTH_SHORT).show();
        }
        for (NativeAd nativeAd : nativeAds) {
            adapter.addNativeAd(nativeAd);
        }
    }

    @Override
    public void onNativeFailedToLoad() {
        if (showToast) {
            Toast.makeText(this, "onNativeFailedToLoad", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNativeShown(NativeAd nativeAd) {
        if (showToast) {
            Toast.makeText(this, "onNativeShown", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNativeClicked(NativeAd nativeAd) {
        if (showToast) {
            Toast.makeText(this, "onNativeClicked", Toast.LENGTH_SHORT).show();
        }
    }

    private static class NativeListViewAdapter extends BaseAdapter {

        List<NativeAd> ads = new LinkedList<>();
        int type;
        Context context;

        public NativeListViewAdapter(Context context, int type) {
            this.type = type;
            this.context = context;
        }

        void addNativeAd(NativeAd nativeAd) {
            ads.add(nativeAd);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return ads.size();
        }

        @Override
        public Object getItem(int position) {
            return ads.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                switch (type) {
                    case 0:
                        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.include_native_ads, parent, false);
                        TextView tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
                        tvTitle.setText(ads.get(position).getTitle());

                        TextView tvDescription = (TextView) convertView.findViewById(R.id.tv_description);
                        tvDescription.setText(ads.get(position).getDescription());

                        RatingBar ratingBar = (RatingBar) convertView.findViewById(R.id.rb_rating);
                        if (ads.get(position).getRating() == 0) {
                            ratingBar.setVisibility(View.INVISIBLE);
                        } else {
                            ratingBar.setVisibility(View.VISIBLE);
                            ratingBar.setRating(ads.get(position).getRating());
                            ratingBar.setStepSize(0.1f);
                        }

                        Button ctaButton = (Button) convertView.findViewById(R.id.b_cta);
                        ctaButton.setText(ads.get(position).getCallToAction());

                        ((ImageView) convertView.findViewById(R.id.icon)).setImageBitmap(ads.get(position).getIcon());

                        View providerView = ads.get(position).getProviderView(context);
                        if (providerView != null) {
                            FrameLayout providerViewContainer = (FrameLayout) convertView.findViewById(R.id.provider_view);
                            providerViewContainer.addView(providerView);
                        }

                        ads.get(position).registerViewForInteraction(convertView);
                        convertView.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        convertView = new NativeAdViewNewsFeed(context, ads.get(position));
                        break;
                    case 2:
                        convertView = new NativeAdViewAppWall(context, ads.get(position));
                        break;
                    case 3:
                        convertView = new NativeAdViewContentStream(context, ads.get(position));
                        break;
                }

            }
            return convertView;
        }
    }
}
