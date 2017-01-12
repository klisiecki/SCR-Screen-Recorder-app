package com.iwobanas.screenrecorder;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import com.iwobanas.screenrecorder.settings.Settings;

public class RecorderActivity extends Activity {

    public final static int REQUEST_CODE = -1010101;

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
                startRecorderService();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && android.provider.Settings.canDrawOverlays(this)) {
                startRecorderService();
            }
        }
    }

    private void startRecorderService() {
        Intent intent = new Intent(this, RecorderService.class);
        intent.setAction(RecorderService.LOUNCHER_ACTION);
        startService(intent);
        finish();
    }
}
