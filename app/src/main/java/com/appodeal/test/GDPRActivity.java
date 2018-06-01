package com.appodeal.test;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;

public class GDPRActivity extends Activity implements AdvertisingInfoListener {

    private static final String RESULT_GDPR = "result_gdpr";

    private TextView tvText;
    private TextView tvYes;
    private TextView tvNo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new AdvertisingInfo(this).execute(this);
    }

    @Override
    public void onInfoReceived(AdvertisingIdClient.Info info) {
        if (info != null && !info.isLimitAdTrackingEnabled()) {
            if (!wasConsentShowing(this)) {
                initViews();
                prepareGDPR();
            } else {
                Intent intent;

                boolean resultGDPR = getResultGDPR(this);
                if (resultGDPR) {
                    intent = new Intent(this, MainActivity.class);
                } else {
                    intent = GDPRErrorActivity.getIntent(this, getString(R.string.gdpr_consent_not_granted));
                }

                startActivity(intent);

                finish();
            }
        } else {
            Intent intent = GDPRErrorActivity.getIntent(this, getString(R.string.gdpr_opt_out_enabled));
            startActivity(intent);

            finish();
        }
    }

    public static String getApplicationName(Context context) {
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        int stringId = applicationInfo.labelRes;
        return stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : context.getString(stringId);
    }

    private boolean wasConsentShowing(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("appodeal", Context.MODE_PRIVATE);

        return sharedPreferences.contains(RESULT_GDPR);
    }

    private boolean getResultGDPR(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("appodeal", Context.MODE_PRIVATE);

        return sharedPreferences.getBoolean(RESULT_GDPR, false);
    }

    private void saveResultGDPR(Context context, boolean resultGDPR) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("appodeal", Context.MODE_PRIVATE);
        sharedPreferences.edit()
                .putBoolean(RESULT_GDPR, resultGDPR)
                .apply();
    }

    private void initViews() {
        setContentView(R.layout.activity_gdpr);

        tvText = findViewById(R.id.tv_text);
        tvYes = findViewById(R.id.tv_yes);
        tvNo = findViewById(R.id.tv_no);
    }

    private void prepareGDPR() {
        String learnMore = "Learn more.";
        String mainText = getString(R.string.gdpr_main_text, getApplicationName(this));
        int startPosition = mainText.indexOf(learnMore);
        int endPosition = startPosition + learnMore.length();
        SpannableString spannableMain = new SpannableString(mainText);
        spannableMain.setSpan(new URLSpan("https://www.appodeal.com/privacy-policy"), startPosition, endPosition, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);


        tvText.setMovementMethod(LinkMovementMethod.getInstance());
        tvText.setText(spannableMain);

        tvYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveResultGDPR(GDPRActivity.this, true);

                Intent intent = GDPRResultActivity.getIntent(GDPRActivity.this, true);
                startActivity(intent);

                finish();
            }
        });


        String no = getString(R.string.gdpr_disagree).toUpperCase();
        SpannableString spannableNo = new SpannableString(no);
        spannableNo.setSpan(new UnderlineSpan(), 0, spannableNo.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        tvNo.setText(spannableNo, TextView.BufferType.SPANNABLE);
        tvNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveResultGDPR(GDPRActivity.this, false);

                Intent intent = GDPRResultActivity.getIntent(GDPRActivity.this, false);
                startActivity(intent);

                finish();
            }
        });


        String close = getString(R.string.gdpr_close).toUpperCase();
        SpannableString spannableClose = new SpannableString(close);
        spannableClose.setSpan(new UnderlineSpan(), 0, spannableClose.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }



    private static class AdvertisingInfo extends AsyncTask<Context, Void, AdvertisingIdClient.Info> {

        private AdvertisingInfoListener advertisingInfoListener;

        public AdvertisingInfo(AdvertisingInfoListener advertisingInfoListener) {
            this.advertisingInfoListener = advertisingInfoListener;
        }

        @Override
        protected AdvertisingIdClient.Info doInBackground(Context... contexts) {
            try {
                return AdvertisingIdClient.getAdvertisingIdInfo(contexts[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(AdvertisingIdClient.Info info) {
            super.onPostExecute(info);

            advertisingInfoListener.onInfoReceived(info);
        }

    }

}