package com.appodeal.test;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.explorestack.consent.Consent;
import com.explorestack.consent.ConsentForm;
import com.explorestack.consent.ConsentFormListener;
import com.explorestack.consent.ConsentInfoUpdateListener;
import com.explorestack.consent.ConsentManager;
import com.explorestack.consent.exception.ConsentManagerException;

import static com.appodeal.test.MainActivity.APP_KEY;

public class SplashActivity extends Activity {

    private ConsentForm consentForm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        /*
        Requesting Consent from European Users
         */
        ConsentManager consentManager = ConsentManager.getInstance(this);
        consentManager.requestConsentInfoUpdate(APP_KEY, new ConsentInfoUpdateListener() {
            @Override
            public void onConsentInfoUpdated(Consent consent) {
                Log.d("Appodeal[Consent]", "onConsentInfoUpdated");
                if (consentManager.shouldShowConsentDialog() == Consent.ShouldShow.TRUE) {
                    loadConsentForm();
                } else {
                    startMainActivity();
                }
            }

            @Override
            public void onFailedToUpdateConsentInfo(ConsentManagerException e) {
                Log.d("Appodeal", "onFailedToUpdateConsentInfo - "
                        + e.getReason() + " " + e.getCode());
                startMainActivity();
            }
        });
    }

    private void loadConsentForm() {
        consentForm = new ConsentForm.Builder(this)
                .withListener(new ConsentFormListener() {
                    @Override
                    public void onConsentFormLoaded() {
                        Log.d("Appodeal[Consent]", "onConsentFormLoaded");
                        consentForm.showAsActivity();
                    }

                    @Override
                    public void onConsentFormError(ConsentManagerException e) {
                        Log.d("Appodeal[Consent]", "ConsentManagerException - "
                                + e.getReason() + " " + e.getCode());
                        startMainActivity();
                    }

                    @Override
                    public void onConsentFormOpened() {
                        Log.d("Appodeal[Consent]", "onConsentFormOpened");
                    }

                    @Override
                    public void onConsentFormClosed(Consent consent) {
                        Log.d("Appodeal[Consent]", "onConsentFormClosed");
                        startMainActivity(consent.getStatus() == Consent.Status.PERSONALIZED);
                    }
                }).build();
        consentForm.load();
    }

    private void startMainActivity() {
        startMainActivity(true);
    }

    private void startMainActivity(boolean consent) {
        startActivity(MainActivity.getIntent(this, consent));
    }
}
