/#package com.appodeal.test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.google.ads.consent.ConsentInfoUpdateListener;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ConsentInformation consentInformation = ConsentInformation.getInstance(this);
        /*
        Requesting Consent from European Users
        https://developers.google.com/admob/android/eu-consent
        IMPORTANT: YOU MUST SPECIFY YOUR PUBLISHER_IDS
        HERE A TEST PUBLISHERIDS
         */
        String[] publisherIds = {"pub-0123456789012345"};
        consentInformation.requestConsentInfoUpdate(publisherIds, new ConsentInfoUpdateListener() {
            @Override
            public void onConsentInfoUpdated(ConsentStatus consentStatus) {
                if (ConsentInformation.getInstance(SplashActivity.this).isRequestLocationInEeaOrUnknown()) {
                    startActivity(new Intent(SplashActivity.this, GDPRActivity.class));
                } else {
                    startMainActivityWithDefaultConsent();
                }
            }

            @Override
            public void onFailedToUpdateConsentInfo(String errorDescription) {
                startMainActivityWithDefaultConsent();
            }
        });
    }

    private void startMainActivityWithDefaultConsent() {
        startActivity(MainActivity.getIntent(this, true));
    }
}
