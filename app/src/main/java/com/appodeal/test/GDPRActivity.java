package com.appodeal.test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.TextView;

public class GDPRActivity extends Activity {

    private TextView tvText;
    private TextView tvYes;
    private TextView tvNo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gdpr);

        initViews();
        prepareGDPR();
    }

    private void initViews() {
        tvText = findViewById(R.id.tv_text);
        tvYes = findViewById(R.id.tv_yes);
        tvNo = findViewById(R.id.tv_no);
    }

    private void prepareGDPR() {
        tvText.setMovementMethod(LinkMovementMethod.getInstance());

        tvYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                Intent intent = GDPRResultActivity.getIntent(GDPRActivity.this, false);
                startActivity(intent);

                finish();
            }
        });


        String close = getString(R.string.gdpr_close).toUpperCase();
        SpannableString spannableClose = new SpannableString(close);
        spannableClose.setSpan(new UnderlineSpan(), 0, spannableClose.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

}