package com.appodeal.test;

import android.app.Activity;
import android.content.pm.PackageManager;

import com.appodeal.ads.utils.PermissionsHelper;

class AppodealPermissionCallbacks implements PermissionsHelper.AppodealPermissionCallbacks {

    private final Activity activity;

    AppodealPermissionCallbacks(Activity activity) {
        this.activity = activity;
    }
    
    @Override
    public void writeExternalStorageResponse(int result) {
        if (result == PackageManager.PERMISSION_GRANTED) {
            Utils.showToast(activity, "WRITE_EXTERNAL_STORAGE permission was granted");
        } else {
            Utils.showToast(activity, "WRITE_EXTERNAL_STORAGE permission was NOT granted");
        }
    }

    @Override
    public void accessCoarseLocationResponse(int result) {
        if (result == PackageManager.PERMISSION_GRANTED) {
            Utils.showToast(activity, "ACCESS_COARSE_LOCATION permission was granted");
        } else {
            Utils.showToast(activity, "ACCESS_COARSE_LOCATION permission was NOT granted");
        }
    }

}
