package com.appodeal.test;

import android.app.Activity;
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

import java.util.LinkedList;
import java.util.List;

public class NativeAdsActivity extends Activity implements NativeCallbacks {

    private NativeListViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_ads);

        ListView nativeAdsListView = (ListView) findViewById(R.id.nativeAdsListView);
        adapter = new NativeListViewAdapter();
        nativeAdsListView.setAdapter(adapter);

        Appodeal.setAutoCacheNativeIcons(true);
        Appodeal.setAutoCacheNativeImages(false);
        Appodeal.setNativeCallbacks(this);
        Appodeal.cache(this, Appodeal.NATIVE, 5);
    }

    @Override
    public void onNativeLoaded(List<NativeAd> nativeAds) {
        Toast.makeText(this, "onNativeLoaded", Toast.LENGTH_SHORT).show();
        for (NativeAd nativeAd : nativeAds) {
            adapter.addNativeAd(nativeAd);
        }
    }

    @Override
    public void onNativeFailedToLoad() {
        Toast.makeText(this, "onNativeFailedToLoad", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNativeShown(NativeAd nativeAd) {
        Toast.makeText(this, "onNativeShown", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNativeClicked(NativeAd nativeAd) {
        Toast.makeText(this, "onNativeClicked", Toast.LENGTH_SHORT).show();
    }

    private static class NativeListViewAdapter extends BaseAdapter {

        List<NativeAd> ads = new LinkedList<>();

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
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.include_native_ads, parent, false);
            }
            fillView(convertView, ads.get(position));
            return convertView;
        }

        private void fillView(View convertView, NativeAd nativeAd) {
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

            View providerView = nativeAd.getProviderView(convertView.getContext());
            if (providerView != null) {
                FrameLayout providerViewContainer = (FrameLayout) convertView.findViewById(R.id.provider_view);
                providerViewContainer.addView(providerView);
            }

            nativeAd.registerViewForInteraction(convertView);
            convertView.setVisibility(View.VISIBLE);
        }
    }
}
