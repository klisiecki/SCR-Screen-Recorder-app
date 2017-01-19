package com.iwobanas.screenrecorder;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.iwobanas.screenrecorder.settings.Settings;

import java.util.ArrayList;
import java.util.List;

public class RecorderActivity extends Activity {

    public final static int REQUEST_CODE = -1010101;
    private final static String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.RECORD_AUDIO};

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Settings.initialize(this);
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null && GusherDialogFragment.shouldShow(this)) {
            new GusherDialogFragment().show(getFragmentManager(), GusherDialogFragment.FRAGMENT_TAG);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !android.provider.Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, REQUEST_CODE);
            } else {
                checkDangerousPermissions();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && android.provider.Settings.canDrawOverlays(this)) {
                checkDangerousPermissions();
            }
        }
    }

    private void checkDangerousPermissions() {
        List<String> toRequestPermissions = new ArrayList<>();
        for (String permission : PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                toRequestPermissions.add(permission);
            }
        }
        if (toRequestPermissions.isEmpty()) {
            startRecorderService();
        } else {
            ActivityCompat.requestPermissions(this, toRequestPermissions.toArray(new String[toRequestPermissions.size()]), 0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        startRecorderService();
    }

    private void startRecorderService() {
        Intent intent = new Intent(this, RecorderService.class);
        intent.setAction(RecorderService.LOUNCHER_ACTION);
        startService(intent);
        finish();
    }
}
