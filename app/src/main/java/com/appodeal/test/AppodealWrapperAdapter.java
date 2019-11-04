package com.appodeal.test;

import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.appodeal.ads.Appodeal;
import com.appodeal.ads.NativeAd;
import com.appodeal.ads.NativeAdView;
import com.appodeal.ads.NativeCallbacks;
import com.appodeal.ads.NativeIconView;
import com.appodeal.ads.NativeMediaView;
import com.appodeal.ads.native_ad.views.NativeAdViewAppWall;
import com.appodeal.ads.native_ad.views.NativeAdViewContentStream;
import com.appodeal.ads.native_ad.views.NativeAdViewNewsFeed;

import java.util.List;

/**
 * Wrapper adapter to show Native Ad in recycler view with fixed step
 */
public class AppodealWrapperAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements NativeCallbacks {

    private static final int DEFAULT_NATIVE_STEP = 5;

    private static final int NATIVE_TYPE_NEWS_FEED = 1;
    private static final int NATIVE_TYPE_APP_WALL = 2;
    private static final int NATIVE_TYPE_CONTENT_STREAM = 3;
    private static final int NATIVE_WITHOUT_ICON = 4;

    private static final int VIEW_HOLDER_NATIVE_AD_TYPE = 600;


    private RecyclerView.Adapter<RecyclerView.ViewHolder> userAdapter;
    private int nativeStep = DEFAULT_NATIVE_STEP;
    private int nativeTemplateType = 0;

    private SparseArray<NativeAd> nativeAdList = new SparseArray<>();

    /**
     * @param userAdapter user adapter
     * @param nativeStep  step show {@link com.appodeal.ads.NativeAd}
     */
    public AppodealWrapperAdapter(RecyclerView.Adapter<RecyclerView.ViewHolder> userAdapter, int nativeStep, int nativeTemplateType) {
        this.userAdapter = userAdapter;
        this.nativeStep = nativeStep + 1;
        this.nativeTemplateType = nativeTemplateType;

        userAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {

            @Override
            public void onChanged() {
                super.onChanged();

                AppodealWrapperAdapter.this.notifyDataSetChanged();

                fillListWithAd();
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);

                AppodealWrapperAdapter.this.notifyDataSetChanged();

                fillListWithAd();
            }
        });

        Appodeal.setNativeCallbacks(this);

        fillListWithAd();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_HOLDER_NATIVE_AD_TYPE) {
            View view;
            switch (nativeTemplateType) {
                case NATIVE_TYPE_NEWS_FEED:
                    view = new NativeAdViewNewsFeed(parent.getContext());
                    return new NativeCreatedAdViewHolder(view);
                case NATIVE_TYPE_APP_WALL:
                    view = new NativeAdViewAppWall(parent.getContext());
                    return new NativeCreatedAdViewHolder(view);
                case NATIVE_TYPE_CONTENT_STREAM:
                    view = new NativeAdViewContentStream(parent.getContext());
                    return new NativeCreatedAdViewHolder(view);
                case NATIVE_WITHOUT_ICON:
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.native_ads_without_icon, parent, false);
                    return new NativeWithoutIconHolder(view);
                default:
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.include_native_ads, parent, false);
                    return new NativeCustomAdViewHolder(view);
            }
        } else {
            return userAdapter.onCreateViewHolder(parent, viewType);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof NativeAdViewHolder) {
            ((NativeAdViewHolder) holder).fillNative(nativeAdList.get(position));
        } else {
            userAdapter.onBindViewHolder(holder, getPositionInUserAdapter(position));
        }
    }

    @Override
    public int getItemCount() {
        int resultCount = 0;

        resultCount += getNativeAdsCount();
        resultCount += getUserAdapterItemCount();

        return resultCount;
    }

    @Override
    public int getItemViewType(int position) {
        if (isNativeAdPosition(position)) {
            return VIEW_HOLDER_NATIVE_AD_TYPE;
        } else {
            return userAdapter.getItemViewType(getPositionInUserAdapter(position));
        }
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);

        if (holder instanceof NativeAdViewHolder) {
            ((NativeAdViewHolder) holder).unregisterViewForInteraction();
        }
    }

    /**
     * Destroy all used native ads
     */
    public void destroyNativeAds() {
        if (nativeAdList != null) {
            for (int i = 0; i < nativeAdList.size(); i++) {
                NativeAd nativeAd = nativeAdList.valueAt(i);
                nativeAd.destroy();
            }

            nativeAdList.clear();
        }
    }

    @Override
    public void onNativeLoaded() {
        fillListWithAd();
    }

    @Override
    public void onNativeFailedToLoad() {

    }

    @Override
    public void onNativeShown(NativeAd nativeAd) {

    }

    @Override
    public void onNativeClicked(NativeAd nativeAd) {

    }

    @Override
    public void onNativeExpired() {

    }


    /**
     * @return count of loaded ads {@link com.appodeal.ads.NativeAd}
     */
    private int getNativeAdsCount() {
        if (nativeAdList != null) {
            return nativeAdList.size();
        }

        return 0;
    }

    /**
     * @return user items count
     */
    private int getUserAdapterItemCount() {
        if (userAdapter != null) {
            return userAdapter.getItemCount();
        }

        return 0;
    }

    /**
     * @param position index in wrapper adapter
     * @return {@code true} if item by position is {@link com.appodeal.ads.NativeAd}
     */
    private boolean isNativeAdPosition(int position) {
        return nativeAdList.get(position) != null;
    }

    /**
     * Method for searching position in user adapter
     *
     * @param position index in wrapper adapter
     * @return index in user adapter
     */
    private int getPositionInUserAdapter(int position) {
        return position - Math.min(nativeAdList.size(), position / nativeStep);
    }

    /**
     * Method for filling list with {@link com.appodeal.ads.NativeAd}
     */
    private void fillListWithAd() {
        int insertPosition = findNextAdPosition();

        NativeAd nativeAd;
        while (canUseThisPosition(insertPosition) && (nativeAd = getNativeAdItem()) != null) {
            nativeAdList.put(insertPosition, nativeAd);
            notifyItemInserted(insertPosition);

            insertPosition = findNextAdPosition();
        }
    }

    /**
     * Get native ad item
     *
     * @return {@link com.appodeal.ads.NativeAd}
     */
    @Nullable
    private NativeAd getNativeAdItem() {
        List<NativeAd> ads = Appodeal.getNativeAds(1);
        return !ads.isEmpty() ? ads.get(0) : null;
    }

    /**
     * Method for finding next position suitable for {@link com.appodeal.ads.NativeAd}
     *
     * @return position for next native ad view
     */
    private int findNextAdPosition() {
        if (nativeAdList.size() > 0) {
            return nativeAdList.keyAt(nativeAdList.size() - 1) + nativeStep;
        }
        return nativeStep - 1;
    }

    /**
     * @param position index in wrapper adapter
     * @return {@code true} if you can add {@link com.appodeal.ads.NativeAd} to this position
     */
    private boolean canUseThisPosition(int position) {
        return nativeAdList.get(position) == null && getItemCount() > position;
    }


    /**
     * View holder for create custom NativeAdView
     */
    static class NativeCustomAdViewHolder extends NativeAdViewHolder {

        private NativeAdView nativeAdView;
        private TextView tvTitle;
        private TextView tvDescription;
        private RatingBar ratingBar;
        private Button ctaButton;
        private NativeIconView nativeIconView;
        private TextView tvAgeRestrictions;
        private NativeMediaView nativeMediaView;
        private FrameLayout providerViewContainer;

        NativeCustomAdViewHolder(View itemView) {
            super(itemView);
            nativeAdView = itemView.findViewById(R.id.native_item);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvDescription = itemView.findViewById(R.id.tv_description);
            ratingBar = itemView.findViewById(R.id.rb_rating);
            ctaButton = itemView.findViewById(R.id.b_cta);
            nativeIconView = itemView.findViewById(R.id.icon);
            providerViewContainer = itemView.findViewById(R.id.provider_view);
            tvAgeRestrictions = itemView.findViewById(R.id.tv_age_restriction);
            nativeMediaView = itemView.findViewById(R.id.appodeal_media_view_content);
        }

        @Override
        void fillNative(NativeAd nativeAd) {
            tvTitle.setText(nativeAd.getTitle());
            tvDescription.setText(nativeAd.getDescription());
            if (nativeAd.getRating() == 0) {
                ratingBar.setVisibility(View.INVISIBLE);
            } else {
                ratingBar.setVisibility(View.VISIBLE);
                ratingBar.setRating(nativeAd.getRating());
                ratingBar.setStepSize(0.1f);
            }
            ctaButton.setText(nativeAd.getCallToAction());
            View providerView = nativeAd.getProviderView(nativeAdView.getContext());
            if (providerView != null) {
                if (providerView.getParent() != null && providerView.getParent() instanceof ViewGroup) {
                    ((ViewGroup) providerView.getParent()).removeView(providerView);
                }
                providerViewContainer.removeAllViews();
                ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                providerViewContainer.addView(providerView, layoutParams);
            }
            if (nativeAd.getAgeRestrictions() != null) {
                tvAgeRestrictions.setText(nativeAd.getAgeRestrictions());
                tvAgeRestrictions.setVisibility(View.VISIBLE);
            } else {
                tvAgeRestrictions.setVisibility(View.GONE);
            }
            if (nativeAd.containsVideo()) {
                nativeAdView.setNativeMediaView(nativeMediaView);
                nativeMediaView.setVisibility(View.VISIBLE);
            } else {
                nativeMediaView.setVisibility(View.GONE);
            }
            nativeAdView.setTitleView(tvTitle);
            nativeAdView.setDescriptionView(tvDescription);
            nativeAdView.setRatingView(ratingBar);
            nativeAdView.setCallToActionView(ctaButton);
            nativeAdView.setNativeIconView(nativeIconView);
            nativeAdView.setProviderView(providerView);
            nativeAdView.registerView(nativeAd);
            nativeAdView.setVisibility(View.VISIBLE);
        }

        @Override
        void unregisterViewForInteraction() {
            nativeAdView.unregisterViewForInteraction();
        }
    }

    /**
     * View holder for create custom NativeAdView without NativeIconView
     */
    static class NativeWithoutIconHolder extends NativeAdViewHolder {
        private NativeAdView nativeAdView;
        private TextView tvTitle;
        private TextView tvDescription;
        private RatingBar ratingBar;
        private Button ctaButton;
        private TextView tvAgeRestrictions;
        private NativeMediaView nativeMediaView;
        private FrameLayout providerViewContainer;

        NativeWithoutIconHolder(View itemView) {
            super(itemView);
            nativeAdView = itemView.findViewById(R.id.native_item);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvDescription = itemView.findViewById(R.id.tv_description);
            ratingBar = itemView.findViewById(R.id.rb_rating);
            ctaButton = itemView.findViewById(R.id.b_cta);
            providerViewContainer = itemView.findViewById(R.id.provider_view);
            tvAgeRestrictions = itemView.findViewById(R.id.tv_age_restriction);
            nativeMediaView = itemView.findViewById(R.id.appodeal_media_view_content);
        }

        @Override
        void fillNative(NativeAd nativeAd) {
            tvTitle.setText(nativeAd.getTitle());
            tvDescription.setText(nativeAd.getDescription());

            if (nativeAd.getRating() == 0) {
                ratingBar.setVisibility(View.INVISIBLE);
            } else {
                ratingBar.setVisibility(View.VISIBLE);
                ratingBar.setRating(nativeAd.getRating());
                ratingBar.setStepSize(0.1f);
            }

            ctaButton.setText(nativeAd.getCallToAction());

            View providerView = nativeAd.getProviderView(nativeAdView.getContext());
            if (providerView != null) {
                if (providerView.getParent() != null && providerView.getParent() instanceof ViewGroup) {
                    ((ViewGroup) providerView.getParent()).removeView(providerView);
                }
                providerViewContainer.removeAllViews();
                ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                providerViewContainer.addView(providerView, layoutParams);
            }

            if (nativeAd.getAgeRestrictions() != null) {
                tvAgeRestrictions.setText(nativeAd.getAgeRestrictions());
                tvAgeRestrictions.setVisibility(View.VISIBLE);
            } else {
                tvAgeRestrictions.setVisibility(View.GONE);
            }

            if (nativeAd.containsVideo()) {
                nativeAdView.setNativeMediaView(nativeMediaView);
                nativeMediaView.setVisibility(View.VISIBLE);
            } else {
                nativeMediaView.setVisibility(View.GONE);
            }


            nativeAdView.setTitleView(tvTitle);
            nativeAdView.setDescriptionView(tvDescription);
            nativeAdView.setRatingView(ratingBar);
            nativeAdView.setCallToActionView(ctaButton);
            nativeAdView.setProviderView(providerView);

            nativeAdView.registerView(nativeAd);
            nativeAdView.setVisibility(View.VISIBLE);
        }

        @Override
        void unregisterViewForInteraction() {
            nativeAdView.unregisterViewForInteraction();
        }

    }

    /**
     * View holder for create NativeAdView by template
     */
    static class NativeCreatedAdViewHolder extends NativeAdViewHolder {

        NativeCreatedAdViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        void fillNative(NativeAd nativeAd) {
            if (itemView instanceof NativeAdViewNewsFeed) {
                com.appodeal.ads.native_ad.views.NativeAdViewNewsFeed nativeAdView = (NativeAdViewNewsFeed) itemView;
                nativeAdView.setNativeAd(nativeAd);
            } else if (itemView instanceof NativeAdViewAppWall) {
                com.appodeal.ads.native_ad.views.NativeAdViewAppWall nativeAdView = (NativeAdViewAppWall) itemView;
                nativeAdView.setNativeAd(nativeAd);
            } else if (itemView instanceof NativeAdViewContentStream) {
                com.appodeal.ads.native_ad.views.NativeAdViewContentStream nativeAdView = (NativeAdViewContentStream) itemView;
                nativeAdView.setNativeAd(nativeAd);
            }
        }

        @Override
        void unregisterViewForInteraction() {
            if (itemView instanceof NativeAdViewNewsFeed) {
                com.appodeal.ads.native_ad.views.NativeAdViewNewsFeed nativeAdView = (NativeAdViewNewsFeed) itemView;
                nativeAdView.unregisterViewForInteraction();
            } else if (itemView instanceof NativeAdViewAppWall) {
                com.appodeal.ads.native_ad.views.NativeAdViewAppWall nativeAdView = (NativeAdViewAppWall) itemView;
                nativeAdView.unregisterViewForInteraction();
            } else if (itemView instanceof NativeAdViewContentStream) {
                com.appodeal.ads.native_ad.views.NativeAdViewContentStream nativeAdView = (NativeAdViewContentStream) itemView;
                nativeAdView.unregisterViewForInteraction();
            }
        }
    }

    /**
     * Abstract view holders for create NativeAdView
     */
    abstract static class NativeAdViewHolder extends RecyclerView.ViewHolder {

        NativeAdViewHolder(View itemView) {
            super(itemView);
        }

        abstract void fillNative(NativeAd nativeAd);

        abstract void unregisterViewForInteraction();
    }

}