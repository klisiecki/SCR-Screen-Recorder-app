package com.iwobanas.screenrecorder.settings;


import android.hardware.Camera;

public enum CameraNumber {
    FRONT(Camera.CameraInfo.CAMERA_FACING_FRONT),
    BACK(Camera.CameraInfo.CAMERA_FACING_BACK);

    private int number;

    CameraNumber(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }
}
