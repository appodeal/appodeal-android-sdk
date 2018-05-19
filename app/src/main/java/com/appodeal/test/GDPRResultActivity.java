package com.appodeal.test;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GDPRResultActivity extends Activity {

    private static final String RESULT_GDPR = "result_gdpr";

    private TextView tvText;
    private LinearLayout llButtonClose;
    private TextView tvClose;

    private boolean resultGDPR;

    public static Intent getIntent(Context context, boolean resultGDPR) {
        Intent intent = new Intent(context, GDPRResultActivity.class);
        intent.putExtra(RESULT_GDPR, resultGDPR);

        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gdpr_result);

        resultGDPR = getIntent().getBooleanExtra(RESULT_GDPR, false);

        initViews();
        prepareResult();
    }

    private void initViews() {
        tvText = findViewById(R.id.tv_text);
        llButtonClose = findViewById(R.id.ll_button_close);
        tvClose = findViewById(R.id.tv_close);
    }

    private void prepareResult() {
        tvText.setMovementMethod(LinkMovementMethod.getInstance());

        if (resultGDPR) {
            tvText.setText(getString(R.string.gdpr_agree_text));
        } else {
            tvText.setText(getString(R.string.gdpr_disagree_text));
        }


        String close = getString(R.string.gdpr_close).toUpperCase();
        SpannableString spannableClose = new SpannableString(close);
        spannableClose.setSpan(new UnderlineSpan(), 0, spannableClose.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        tvClose.setText(spannableClose);
        llButtonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = MainActivity.getIntent(GDPRResultActivity.this, resultGDPR);

                startActivity(intent);

                finish();
            }
        });
    }

}