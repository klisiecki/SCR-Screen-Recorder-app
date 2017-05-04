package com.iwobanas.screenrecorder.settings;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.*;
import android.provider.Settings.System;

public class ShowTouchesController {
    private final String SHOW_TOUCHES_SETTING = "show_touches";

    private ContentResolver contentResolver;
    private final Context context;

    public ShowTouchesController(Context context) {
        this.context = context;
        contentResolver = context.getContentResolver();
    }

    public boolean getShowTouches() {
        int setting = System.getInt(contentResolver, SHOW_TOUCHES_SETTING, 0);
        return setting == 1;
    }

    public void setShowTouches(boolean show) {
        if (getShowTouches() != show) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !android.provider.Settings.System.canWrite(context)) {
                Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS,
                        Uri.parse("package:" + context.getPackageName()));
                context.startActivity(intent);
            } else {
                System.putInt(contentResolver, SHOW_TOUCHES_SETTING, show ? 1 : 0);
            }
        }
    }
}
