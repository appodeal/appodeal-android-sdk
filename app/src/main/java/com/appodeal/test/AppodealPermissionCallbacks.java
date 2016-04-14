package com.appodeal.test;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.widget.Spinner;

import com.appodeal.ads.utils.PermissionsHelper;

class AppodealPermissionCallbacks implements PermissionsHelper.AppodealPermissionCallbacks {
    private final Activity mActivity;

    AppodealPermissionCallbacks(Activity activity) {
        mActivity = activity;
    }
    
    @Override
    public void writeExternalStorageResponse(int result) {
        if (result == PackageManager.PERMISSION_GRANTED) {
            Utils.showToast(mActivity, "WRITE_EXTERNAL_STORAGE permission was granted");
        } else {
            Utils.showToast(mActivity, "WRITE_EXTERNAL_STORAGE permission was NOT granted");
        }
    }

    @Override
    public void accessCoarseLocationResponse(int result) {
        if (result == PackageManager.PERMISSION_GRANTED) {
            Utils.showToast(mActivity, "ACCESS_COARSE_LOCATION permission was granted");
        } else {
            Utils.showToast(mActivity, "ACCESS_COARSE_LOCATION permission was NOT granted");
        }
    }
}
