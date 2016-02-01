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
import com.appodeal.ads.native_ad.views.NativeAdViewAppWall;
import com.appodeal.ads.native_ad.views.NativeAdViewContentStream;
import com.appodeal.ads.native_ad.views.NativeAdViewNewsFeed;

import java.util.List;

public class AppodealNativeCallbacks implements NativeCallbacks {
    private final Activity mActivity;
    private int type;

    AppodealNativeCallbacks(Activity activity) {
        mActivity = activity;
        this.type = 0;
    }

    AppodealNativeCallbacks(Activity activity, int type) {
        mActivity = activity;
        this.type = type;
    }

    @Override
    public void onNativeLoaded(List<NativeAd> nativeAds) {
        ((MainActivity) mActivity).showToast("onNativeLoaded");
        NativeAd nativeAd = nativeAds.get(0);
        new NativeAdFiller(mActivity, nativeAd).fillNativeView();
    }

    @Override
    public void onNativeFailedToLoad() {
        ((MainActivity) mActivity).showToast("onNativeFailedToLoad");
    }

    @Override
    public void onNativeShown(NativeAd nativeAd) {
        ((MainActivity) mActivity).showToast("onNativeShown");
    }

    @Override
    public void onNativeClicked(NativeAd nativeAd) {
        ((MainActivity) mActivity).showToast("onNativeClicked");
    }

    private class NativeAdFiller {
        private final Activity mActivity;
        private final NativeAd mNativeAd;

        public NativeAdFiller(Activity activity, NativeAd nativeAd) {
            mActivity = activity;
            mNativeAd = nativeAd;
        }

        public void fillNativeView() {
            NativeAdViewNewsFeed navNF = (NativeAdViewNewsFeed) mActivity.findViewById(R.id.native_ad_view_news_feed);
            navNF.setVisibility(View.GONE);
            NativeAdViewAppWall navAW = (NativeAdViewAppWall) mActivity.findViewById(R.id.native_ad_view_app_wall);
            navAW.setVisibility(View.GONE);
            NativeAdViewContentStream navCS = (NativeAdViewContentStream) mActivity.findViewById(R.id.native_ad_view_content_stream);
            navCS.setVisibility(View.GONE);

            switch (type) {
                case 0:
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
                    break;
                case 1:
                    navNF.setNativeAd(mNativeAd);
                    navNF.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    navAW.setNativeAd(mNativeAd);
                    navAW.setVisibility(View.VISIBLE);
                    break;
                case 3:
                    navCS.setNativeAd(mNativeAd);
                    navCS.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }
}
