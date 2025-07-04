// This file was generated by PermissionsDispatcher. Do not modify!
package com.iyuba.toeiclistening.activity;

import androidx.core.app.ActivityCompat;

import permissions.dispatcher.PermissionUtils;

final class MainFragmentActivityPermissionsDispatcher {
  private static final int REQUEST_INITLOCATION = 0;

  private static final String[] PERMISSION_INITLOCATION = new String[] {"android.permission.WRITE_EXTERNAL_STORAGE"};
  private static final String[] PERMISSION_INITMESSAGE = new String[] {"android.permission.READ_CONTACTS"};

  private MainFragmentActivityPermissionsDispatcher() {
  }

  static void initLocationWithPermissionCheck(MainActivity target) {
    if (PermissionUtils.hasSelfPermissions(target, PERMISSION_INITLOCATION)) {
      target.initLocation();
    } else {
      ActivityCompat.requestPermissions(target, PERMISSION_INITLOCATION, REQUEST_INITLOCATION);
    }
  }

  static void initMessageWithPermissionCheck(MainActivity target) {
    if (PermissionUtils.hasSelfPermissions(target, PERMISSION_INITLOCATION)) {
      target.initLocation();
    } else {
      ActivityCompat.requestPermissions(target, PERMISSION_INITLOCATION, REQUEST_INITLOCATION);
    }
  }

  static void onRequestPermissionsResult(MainActivity target, int requestCode,
      int[] grantResults) {
    switch (requestCode) {
      case REQUEST_INITLOCATION:
      if (PermissionUtils.verifyPermissions(grantResults)) {
        target.initLocation();
      } else {
        target.locationDenied();
      }
      break;

      default:
      break;
    }
  }
}
