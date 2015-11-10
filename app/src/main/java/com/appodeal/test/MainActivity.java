package com.appodeal.test;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.appodeal.ads.Appodeal;
import com.appodeal.ads.UserSettings;
import com.appodeal.test.layout.AdTypeViewPager;
import com.appodeal.test.layout.SlidingTabLayout;


public class MainActivity extends FragmentActivity {
    private static final String APP_KEY = "fee50c333ff3825fd6ad6d38cff78154de3025546d47a84f";

    private String[] networks;
    boolean[] interstitialNetworks;
    boolean[] bannerNetworks;
    boolean[] nonRewardedViewNetworks;
    boolean[] rewardedViewNetworks;
    boolean[] checkedValues;

    boolean showToast = false;
    private Toast mToast;

    public enum AdType {
        All(Appodeal.ALL), Interstitial(Appodeal.INTERSTITIAL), Video(Appodeal.VIDEO), RVideo(Appodeal.REWARDED_VIDEO), Banner(Appodeal.BANNER);
        private final int mValue;

        AdType(int value) {
            mValue = value;
        }

        public int getValue() {
            return mValue;
        }

        public static AdType fromInteger(Integer x) {
            if (x == null) {
                return null;
            }
            switch(x) {
                case Appodeal.ALL: return All;
                case Appodeal.INTERSTITIAL: return Interstitial;
                case Appodeal.VIDEO: return Video;
                case Appodeal.REWARDED_VIDEO: return RVideo;
                case Appodeal.BANNER: return Banner;
            }
            return null;
        }
    }

    public enum BannerPosition {
        BANNER(Appodeal.BANNER), BOTTOM(Appodeal.BANNER_BOTTOM), TOP(Appodeal.BANNER_TOP), CENTER(Appodeal.BANNER_CENTER), VIEW(Appodeal.BANNER_VIEW);
        private final int mValue;

        BannerPosition(int value) {
            mValue = value;
        }

        public int getValue() {
            return mValue;
        }
    }

    public enum AdTypePages {
        Interstitial(R.layout.interstitial, R.id.interstitialLayout), Video(R.layout.video, R.id.videoLayout),
        RVideo(R.layout.rewarded_video, R.id.rewardedVideoLayout), Banner(R.layout.banner, R.id.bannerLayout);

        private final int mLayout;
        private final int mId;

        AdTypePages(int layout, int id) {
            mLayout = layout;
            mId = id;
        }

        public int getLayout() {
            return mLayout;
        }

        public int getId() {
            return mId;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        networks = getResources().getStringArray(R.array.networks);
        interstitialNetworks = new boolean[networks.length];
        bannerNetworks = new boolean[networks.length];
        nonRewardedViewNetworks = new boolean[networks.length];
        rewardedViewNetworks = new boolean[networks.length];
        for (int i = 0; i < networks.length; i++) {
            interstitialNetworks[i] = true;
            bannerNetworks[i] = true;
            nonRewardedViewNetworks[i] = true;
            rewardedViewNetworks[i] = true;
        }


        if (Build.VERSION.SDK_INT >= 23 && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }

        TextView sdkTextView = (TextView) findViewById(R.id.sdkTextView);
        sdkTextView.setText(getString(R.string.sdkTextView, Appodeal.getVersion()));

        CompoundButton testModeSwitch = (CompoundButton) findViewById(R.id.testModeSwitch);
        testModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Appodeal.setTesting(isChecked);
            }
        });

        CompoundButton toastSwitch = (CompoundButton) findViewById(R.id.toastSwitch);
        toastSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                showToast = isChecked;
            }
        });

        CompoundButton loggingSwitch = (CompoundButton) findViewById(R.id.loggingSwitch);
        loggingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Appodeal.setLogging(isChecked);
            }
        });

        ViewPager pager = (AdTypeViewPager) findViewById(R.id.pager);
        pager.setOffscreenPageLimit(AdTypePages.values().length);
        pager.setOnHierarchyChangeListener(new ViewGroup.OnHierarchyChangeListener() {
            @Override
            public void onChildViewAdded(View parent, View child) {
                if (child.findViewById(AdTypePages.Interstitial.getId()) != null && child.getTag() == null) {
                    child.setTag(true);
                    CompoundButton autoCacheInterstitialSwitch = (CompoundButton) findViewById(R.id.autoCacheInterstitialSwitch);
                    autoCacheInterstitialSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            Appodeal.setAutoCache(Appodeal.INTERSTITIAL, isChecked);
                            Button interstitialCacheButton = (Button) findViewById(R.id.interstitialCacheButton);
                            if (isChecked) {
                                interstitialCacheButton.setVisibility(View.GONE);
                            } else {
                                interstitialCacheButton.setVisibility(View.VISIBLE);
                            }
                        }
                    });

                    final CompoundButton onLoadedSwitch = (CompoundButton) findViewById(R.id.onLoadedInterstitialSwitch);
                    onLoadedSwitch.setText(getString(R.string.onLoadedInterstitialSwitch, "expensive"));
                    onLoadedSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) {
                                onLoadedSwitch.setText(getString(R.string.onLoadedInterstitialSwitch, "both"));
                            } else {
                                onLoadedSwitch.setText(getString(R.string.onLoadedInterstitialSwitch, "expensive"));
                            }
                            Appodeal.setOnLoadedTriggerBoth(Appodeal.INTERSTITIAL, isChecked);
                        }
                    });
                }

                if (child.findViewById(AdTypePages.Video.getId()) != null && child.getTag() == null) {
                    child.setTag(true);
                    CompoundButton autoCacheVideoSwitch = (CompoundButton) findViewById(R.id.autoCacheVideoSwitch);
                    autoCacheVideoSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            Appodeal.setAutoCache(Appodeal.VIDEO, isChecked);
                            Button videoCacheButton = (Button) findViewById(R.id.videoCacheButton);
                            if (isChecked) {
                                videoCacheButton.setVisibility(View.GONE);
                            } else {
                                videoCacheButton.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }

                if (child.findViewById(AdTypePages.RVideo.getId()) != null && child.getTag() == null) {
                    child.setTag(true);
                    CompoundButton autoCacheRewardedVideoSwitch = (CompoundButton) findViewById(R.id.autoCacheRewardedVideoSwitch);
                    autoCacheRewardedVideoSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            Appodeal.setAutoCache(Appodeal.REWARDED_VIDEO, isChecked);
                            Button rewardedVideoCacheButton = (Button) findViewById(R.id.rewardedVideoCacheButton);
                            if (isChecked) {
                                rewardedVideoCacheButton.setVisibility(View.GONE);
                            } else {
                                rewardedVideoCacheButton.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }

                if (child.findViewById(AdTypePages.Banner.getId()) != null && child.getTag() == null) {
                    child.setTag(true);
                    CompoundButton autoCacheBannerSwitch = (CompoundButton) findViewById(R.id.autoCacheBannerSwitch);
                    autoCacheBannerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            Appodeal.setAutoCache(Appodeal.BANNER, isChecked);
                            Button bannerCacheButton = (Button) findViewById(R.id.bannerCacheButton);
                            if (isChecked) {
                                bannerCacheButton.setVisibility(View.GONE);
                            } else {
                                bannerCacheButton.setVisibility(View.VISIBLE);
                            }
                        }
                    });

                    Spinner bannerPositionSpinner = (Spinner) findViewById(R.id.bannerPositionList);
                    ArrayAdapter<BannerPosition> bannerPositionsAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, BannerPosition.values());
                    bannerPositionSpinner.setAdapter(bannerPositionsAdapter);
                }
            }

            @Override
            public void onChildViewRemoved(View parent, View child) {

            }
        });
        AdTypeAdapter adTypeAdapter = new AdTypeAdapter(getSupportFragmentManager());
        pager.setAdapter(adTypeAdapter);

        SlidingTabLayout slidingTabLayout = (SlidingTabLayout) findViewById(R.id.slidingTabLayout);
        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setViewPager(pager);
    }


    @Override
    public void onResume() {
        super.onResume();
        Appodeal.onResume(this, Appodeal.BANNER);
    }

    @Override
    public void onBackPressed() {
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
        for (int i = 0; i < root.getChildCount(); i++) {
            View child = root.getChildAt(i);
            Object tag = child.getTag();
            if (tag != null && tag.equals("appodeal")) {
                root.removeView(child);
                return;
            }
        }
        super.onBackPressed();
    }

    public void initSdkButton(View v) {

        //Add user settings
        Appodeal.getUserSettings(this)
                .setAge(25)
                .setAlcohol(UserSettings.Alcohol.NEGATIVE)
                .setSmoking(UserSettings.Smoking.NEUTRAL)
                .setBirthday("17/06/1990") .setEmail("ru@appodeal.com")
                .setFacebookId("1623169517896758") .setVkId("91918219")
                .setGender(UserSettings.Gender.MALE)
                .setRelation(UserSettings.Relation.DATING)
                .setInterests("reading, games, movies, snowboarding")
                .setOccupation(UserSettings.Occupation.WORK);
        Appodeal.trackInAppPurchase(this, 10.0, "UAH");
        Appodeal.initialize(this, APP_KEY, Appodeal.NONE);
    }

    public void disableNetworks(boolean[] adNetworks, AdType adType) {
        for (int i = 0; i < adNetworks.length; i++) {
            if (!adNetworks[i]) {
                Appodeal.disableNetwork(this, networks[i], adType.getValue());
            }
        }
    }


    public void interstitialChooseNetworks(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        checkedValues = interstitialNetworks.clone();
        builder.setTitle("Select networks").setMultiChoiceItems(networks, checkedValues,
                new DialogInterface.OnMultiChoiceClickListener() {
                    public void onClick(DialogInterface dialog, int item, boolean isChecked) {
                    }
                });
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                interstitialNetworks = checkedValues;
                disableNetworks(interstitialNetworks, AdType.Interstitial);
            }
        });

        Dialog dialog = builder.create();
        dialog.show();
    }

    public void initInterstitialSdkButton(View v) {
        Appodeal.initialize(this, APP_KEY, Appodeal.INTERSTITIAL);
        Appodeal.setInterstitialCallbacks(new AppodealInterstitialCallbacks(this));
    }

    public void isInterstitialLoadedButton(View v) {
        if (Appodeal.isLoaded(Appodeal.INTERSTITIAL)) {
            Toast.makeText(this, "true", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "false", Toast.LENGTH_SHORT).show();
        }
    }

    public void isInterstitialLoadedWithPriceFloorButton(View v) {
        if (Appodeal.isLoadedWithPriceFloor(Appodeal.INTERSTITIAL)) {
            Toast.makeText(this, "true", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "false", Toast.LENGTH_SHORT).show();
        }
    }

    public void isInterstitialLoadedPrecacheButton(View v) {
        if (Appodeal.isPrecache(Appodeal.INTERSTITIAL)) {
            Toast.makeText(this, "true", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "false", Toast.LENGTH_SHORT).show();
        }
    }

    public void interstitialCacheButton(View v) {
        Appodeal.cache(this, Appodeal.INTERSTITIAL);
    }

    public void interstitialShowButton(View v) {
        boolean isShown = Appodeal.show(this, Appodeal.INTERSTITIAL);
        Toast.makeText(this, String.valueOf(isShown), Toast.LENGTH_SHORT).show();
    }

    public void interstitialShowWithPriceFloorButton(View v) {
        boolean isShown = Appodeal.showWithPriceFloor(this, Appodeal.INTERSTITIAL);
        Toast.makeText(this, String.valueOf(isShown), Toast.LENGTH_SHORT).show();
    }


    public void videoChooseNetworks(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        checkedValues = nonRewardedViewNetworks.clone();
        builder.setTitle("Select networks").setMultiChoiceItems(networks, checkedValues,
                new DialogInterface.OnMultiChoiceClickListener() {
                    public void onClick(DialogInterface dialog, int item, boolean isChecked) {

                    }
                });
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                nonRewardedViewNetworks = checkedValues;
                disableNetworks(nonRewardedViewNetworks, AdType.Video);
            }
        });

        Dialog dialog = builder.create();
        dialog.show();
    }

    public void initVideoSdkButton(View v) {
        Appodeal.initialize(this, APP_KEY, Appodeal.VIDEO);
        Appodeal.setVideoCallbacks(new AppodealVideoCallbacks(this));
    }

    public void isVideoLoadedButton(View v) {
        if (Appodeal.isLoaded(Appodeal.VIDEO)) {
            Toast.makeText(this, "true", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "false", Toast.LENGTH_SHORT).show();
        }
    }

    public void videoCacheButton(View v) {
        Appodeal.cache(this, Appodeal.VIDEO);
    }

    public void videoShowButton(View v) {
        boolean isShown = Appodeal.show(this, Appodeal.VIDEO);
        Toast.makeText(this, String.valueOf(isShown), Toast.LENGTH_SHORT).show();
    }


    public void rewardedVideoChooseNetworks(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        checkedValues = rewardedViewNetworks.clone();
        builder.setTitle("Select networks").setMultiChoiceItems(networks, checkedValues,
                new DialogInterface.OnMultiChoiceClickListener() {
                    public void onClick(DialogInterface dialog, int item, boolean isChecked) {
                    }
                });
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                rewardedViewNetworks = checkedValues;
                disableNetworks(rewardedViewNetworks, AdType.RVideo);
            }
        });

        Dialog dialog = builder.create();
        dialog.show();
    }

    public void initRewardedVideoSdkButton(View v) {
        Appodeal.initialize(this, APP_KEY, Appodeal.REWARDED_VIDEO);
        Appodeal.setRewardedVideoCallbacks(new AppodealRewardedVideoCallbacks(this));
    }

    public void isRewardedVideoLoadedButton(View v) {
        if (Appodeal.isLoaded(Appodeal.REWARDED_VIDEO)) {
            Toast.makeText(this, "true", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "false", Toast.LENGTH_SHORT).show();
        }
    }

    public void rewardedVideoCacheButton(View v) {
        Appodeal.cache(this, Appodeal.REWARDED_VIDEO);
    }

    public void rewardedVideoShowButton(View v) {
        boolean isShown = Appodeal.show(this, Appodeal.REWARDED_VIDEO);
        Toast.makeText(this, String.valueOf(isShown), Toast.LENGTH_SHORT).show();
    }


    public void bannerChooseNetworks(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        checkedValues = bannerNetworks.clone();
        builder.setTitle("Select networks").setMultiChoiceItems(networks, checkedValues,
                new DialogInterface.OnMultiChoiceClickListener() {
                    public void onClick(DialogInterface dialog, int item, boolean isChecked) {
                    }
                });
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                bannerNetworks = checkedValues;
                disableNetworks(bannerNetworks, AdType.Banner);
            }
        });

        Dialog dialog = builder.create();
        dialog.show();
    }

    public void initBannerSdkButton(View v) {
        Appodeal.setBannerViewId(R.id.appodealBannerView);
        Appodeal.initialize(this, APP_KEY, Appodeal.BANNER);
        Appodeal.setBannerCallbacks(new AppodealBannerCallbacks(this));
    }

    public void isBannerLoadedButton(View v) {
        if (Appodeal.isLoaded(Appodeal.BANNER)) {
            Toast.makeText(this, "true", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "false", Toast.LENGTH_SHORT).show();
        }
    }

    public void bannerCacheButton(View v) {
        Appodeal.cache(this, Appodeal.BANNER);
    }

    public void bannerShowButton(View v) {
        Spinner bannerPositionSpinner = (Spinner) findViewById(R.id.bannerPositionList);
        BannerPosition bannerPosition = (BannerPosition) bannerPositionSpinner.getSelectedItem();
        boolean isShown = Appodeal.show(this, bannerPosition.getValue());
        Toast.makeText(this, String.valueOf(isShown), Toast.LENGTH_SHORT).show();
    }

    public void bannerHideButton(View v) {
        Appodeal.hide(this, Appodeal.BANNER);
    }


    public static class AdTypeAdapter extends FragmentPagerAdapter {

        public AdTypeAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return AdTypePages.values().length;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = new AdTypeFragment();
            Bundle args = new Bundle();
            args.putInt("layout", AdTypePages.values()[position].getLayout());
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return AdTypePages.values()[position].name();
        }
    }

    public static class AdTypeFragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            Bundle args = getArguments();
            int layoutId = args.getInt("layout");
            return inflater.inflate(layoutId, container, false);
        }
    }

    public void showToast(String text) {
        if (showToast) {
            Log.d("Appodeal", text);
            if (mToast == null) {
                mToast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
            }
            mToast.setText(text);
            mToast.setDuration(Toast.LENGTH_SHORT);
            mToast.show();
        }
    }
}
