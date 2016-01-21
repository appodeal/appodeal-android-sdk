package com.appodeal.test;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.appodeal.ads.MrecCallbacks;

class AppodealMrecCallbacks implements MrecCallbacks {
    private final Activity mActivity;
    private Toast mToast;

    AppodealMrecCallbacks(Activity activity) {
        mActivity = activity;
    }

    @Override
    public void onMrecLoaded() {
        showToast("onMrecLoaded");
    }

    @Override
    public void onMrecFailedToLoad() {
        showToast("onMrecFailedToLoad");
    }

    @Override
    public void onMrecShown() {
        showToast("onMrecShown");
    }

    @Override
    public void onMrecClicked() {
        showToast("onMrecClicked");
    }

    private void showToast(String text) {
        Log.d("Appodeal", text);
        if (mToast == null) {
            mToast = Toast.makeText(mActivity, text, Toast.LENGTH_SHORT);
        }
        mToast.setText(text);
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.show();
    }
}
