package com.appodeal.test;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

public class GDPRErrorActivity extends Activity {

    private static final String ERROR_MESSAGE = "error_message";

    public static Intent getIntent(Context context, String errorMessage) {
        Intent intent = new Intent(context, GDPRErrorActivity.class);
        intent.putExtra(ERROR_MESSAGE, errorMessage);

        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_init);

        String errorMessage = getIntent().getStringExtra(ERROR_MESSAGE);


        TextView tvResultGDPR = findViewById(R.id.tv_result);
        tvResultGDPR.setText(errorMessage);
    }
}