package com.appodeal.test;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

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

    // Requesting Consent from European Users using Stack ConsentManager (https://wiki.appodeal.com/en/android/consent-manager).
    private void resolveUserConsent() {
        // Note: YOU MUST SPECIFY YOUR APPODEAL SDK KET HERE
        String appodealAppKey = "fee50c333ff3825fd6ad6d38cff78154de3025546d47a84f";
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
                            if (consent.getStatus() == Consent.Status.UNKNOWN) {
                                // Start our main activity with default Consent value
                                startMainActivity();
                            } else {
                                boolean hasConsent = consent.getStatus() == Consent.Status.PERSONALIZED;
                                // Start our main activity with resolved Consent value
                                startMainActivity(hasConsent);
                            }
                        }
                    }

                    @Override
                    public void onFailedToUpdateConsentInfo(ConsentManagerException e) {
                        // Start our main activity with default Consent value
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
                            Toast.makeText(
                                    SplashActivity.this,
                                    "Consent form error: " + error.getReason(),
                                    Toast.LENGTH_SHORT
                            ).show();
                            // Start our main activity with default Consent value
                            startMainActivity();
                        }

                        @Override
                        public void onConsentFormOpened() {
                            //ignore
                        }

                        @Override
                        public void onConsentFormClosed(Consent consent) {
                            boolean hasConsent = consent.getStatus() == Consent.Status.PERSONALIZED;
                            // Start our main activity with resolved Consent value
                            startMainActivity(hasConsent);
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

    // Start our main activity with default Consent value
    private void startMainActivity() {
        startMainActivity(true);
    }

    // Start our main activity with resolved Consent value
    private void startMainActivity(boolean hasConsent) {
        startActivity(MainActivity.getIntent(this, hasConsent));
    }
}
