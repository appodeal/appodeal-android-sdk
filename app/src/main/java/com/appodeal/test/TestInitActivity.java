package com.appodeal.test;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.appodeal.ads.Appodeal;
import com.appodeal.ads.utils.Log;

public class TestInitActivity extends Activity {

    private static final String APP_KEY = "fee50c333ff3825fd6ad6d38cff78154de3025546d47a84f";
    private static final String RESULT_GDPR = "result_gdpr";

    public static Intent getIntent(Context context, boolean resultGDPR) {
        Intent intent = new Intent(context, TestInitActivity.class);
        intent.putExtra(RESULT_GDPR, resultGDPR);

        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_init);

        boolean resultGDPR = getIntent().getBooleanExtra(RESULT_GDPR, false);
        if (resultGDPR) {
            Appodeal.setLogLevel(Log.LogLevel.verbose);

            Appodeal.initialize(this, APP_KEY, Appodeal.BANNER);
        }

        TextView tvResultGDPR = findViewById(R.id.tv_result);
        tvResultGDPR.setText(String.format("GDPR consent: %s", resultGDPR));
    }
}