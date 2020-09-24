package com.veer.filepicker.camera.listener;

import android.graphics.Bitmap;

public interface ZCameraListener {

    void captureSuccess(Bitmap bitmap);

    void recordSuccess(String url, Bitmap firstFrame);

}
