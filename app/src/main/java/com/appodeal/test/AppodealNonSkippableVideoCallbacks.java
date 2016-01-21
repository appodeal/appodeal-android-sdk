package com.appodeal.test;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.appodeal.ads.NonSkippableVideoCallbacks;

class AppodealNonSkippableVideoCallbacks implements NonSkippableVideoCallbacks {
    private final Activity mActivity;
    private Toast mToast;

    AppodealNonSkippableVideoCallbacks(Activity activity) {
        mActivity = activity;
    }

    @Override
    public void onNonSkippableVideoLoaded() {
        showToast("onNonSkippableVideoLoaded");
    }

    @Override
    public void onNonSkippableVideoFailedToLoad() {
        showToast("onNonSkippableVideoFailedToLoad");
    }

    @Override
    public void onNonSkippableVideoShown() {
        showToast("onNonSkippableVideoShown");
    }

    @Override
    public void onNonSkippableVideoFinished() {
        showToast("onNonSkippableVideoFinished");
    }

    @Override
    public void onNonSkippableVideoClosed() {
        showToast("onNonSkippableVideoClosed");
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
