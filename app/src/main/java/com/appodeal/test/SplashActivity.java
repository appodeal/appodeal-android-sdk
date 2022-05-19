package com.appodeal.test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.explorestack.consent.Consent;
import com.explorestack.consent.ConsentForm;
import com.explorestack.consent.ConsentFormListener;
import com.explorestack.consent.ConsentInfoUpdateListener;
import com.explorestack.consent.ConsentManager;
import com.explorestack.consent.exception.ConsentManagerException;

public class SplashActivity extends Activity {

    @Nullable
    private ConsentForm consentForm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        resolveUserConsent();
    }

    // Requesting Consent from European Users using Stack ConsentManager (https://wiki.appodeal.com/en/android/get-started/data-protection/gdpr-and-ccpa).
    private void resolveUserConsent() {
        // Note: YOU MUST SPECIFY YOUR APPODEAL SDK KET HERE
        String appodealAppKey = BuildConfig.APP_KEY;
        ConsentManager consentManager = ConsentManager.getInstance(this);
        // Requesting Consent info update
        consentManager.requestConsentInfoUpdate(
                appodealAppKey,
                new ConsentInfoUpdateListener() {
                    @Override
                    public void onConsentInfoUpdated(Consent consent) {
                        Consent.ShouldShow consentShouldShow =
                                consentManager.shouldShowConsentDialog();
                        // If ConsentManager return Consent.ShouldShow.TRUE, than we should show consent form
                        if (consentShouldShow == Consent.ShouldShow.TRUE) {
                            showConsentForm();
                        } else {
                            // Start main activity with resolved Consent value
                            startMainActivity();
                        }
                    }

                    @Override
                    public void onFailedToUpdateConsentInfo(ConsentManagerException e) {
                        // Start main activity with default Consent value
                        startMainActivity();
                    }
                });
    }

    // Displaying ConsentManger Consent request form
    private void showConsentForm() {
        if (consentForm == null) {
            consentForm = new ConsentForm.Builder(this)
                    .withListener(new ConsentFormListener() {
                        @Override
                        public void onConsentFormLoaded() {
                            // Show ConsentManager Consent request form
                            consentForm.showAsActivity();
                        }

                        @Override
                        public void onConsentFormError(ConsentManagerException error) {
                            // Start main activity with default Consent value
                            startMainActivity();
                        }

                        @Override
                        public void onConsentFormOpened() {
                            //ignore
                        }

                        @Override
                        public void onConsentFormClosed(Consent consent) {
                            // Start main activity with resolved Consent value
                            startMainActivity();
                        }
                    }).build();
        }
        // If Consent request form is already loaded, then we can display it, otherwise, we should load it first
        if (consentForm.isLoaded()) {
            consentForm.showAsActivity();
        } else {
            consentForm.load();
        }
    }

    // Start main activity
    private void startMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
    }
}
