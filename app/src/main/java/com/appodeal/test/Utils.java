package com.appodeal.test;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

class Utils {

    static void showToast(Activity activity, String text) {
        Log.d("AppodealDemoApp", text);
        Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();
    }

}
