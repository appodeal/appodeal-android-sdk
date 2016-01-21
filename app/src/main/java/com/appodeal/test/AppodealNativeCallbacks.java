package com.appodeal.test;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
        Toast.makeText(mActivity, "onNativeLoaded", Toast.LENGTH_SHORT).show();
        new NativeAdFiller(mActivity, nativeAds.get(0)).fillNativeView();
    }

    @Override
    public void onNativeFailedToLoad() {
        Toast.makeText(mActivity, "onNativeFailedToLoad", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNativeShown(NativeAd nativeAd) {
        Toast.makeText(mActivity, "onNativeShown", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNativeClicked(NativeAd nativeAd) {
        Toast.makeText(mActivity, "onNativeClicked", Toast.LENGTH_SHORT).show();
    }

    private static class NativeAdFiller {
        private final Activity mActivity;
        private final NativeAd mNativeAd;

        public NativeAdFiller(Activity activity, NativeAd nativeAd) {
            mActivity = activity;
            mNativeAd = nativeAd;
        }

        public void fillNativeView() {
            RelativeLayout v = (RelativeLayout) mActivity.findViewById(R.id.native_item);
            TextView tvTitle = (TextView) v.findViewById(R.id.tv_title);
            tvTitle.setText(mNativeAd.getTitle());

            TextView tvDescription = (TextView) v.findViewById(R.id.tv_description);
            tvDescription.setText(mNativeAd.getDescription());

            RatingBar ratingBar = (RatingBar) v.findViewById(R.id.rb_rating);
            if (mNativeAd.getRating() == 0) {
                ratingBar.setVisibility(View.INVISIBLE);
            } else {
                ratingBar.setVisibility(View.VISIBLE);
                ratingBar.setRating(mNativeAd.getRating());
                ratingBar.setStepSize(0.1f);
            }

            Button ctaButton = (Button) v.findViewById(R.id.b_cta);
            ctaButton.setText(mNativeAd.getCallToAction());

            ((ImageView) v.findViewById(R.id.icon)).setImageBitmap(mNativeAd.getIcon());

            View providerView = mNativeAd.getProviderView(mActivity);
            if (providerView != null) {
                FrameLayout providerViewContainer = (FrameLayout) v.findViewById(R.id.provider_view);
                providerViewContainer.addView(providerView);
            }

            mNativeAd.registerViewForInteraction(v);
            v.setVisibility(View.VISIBLE);
        }
    }
}
