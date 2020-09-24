package com.veer.filepicker.camera;

import android.graphics.Bitmap;
/**
 * <li>Author:Veer</li>
 * <li>Date:  2020/3/6</li>
 * <li>Description: </li>
 */
public interface CameraView {
    void resetState(int type);

    void confirmState(int type);

    void showPicture(Bitmap bitmap, boolean isVertical);

    void playVideo(Bitmap firstFrame, String url);

    void stopVideo();

    void setTip(String tip);

    void startPreviewCallback();

    boolean handlerFoucs(float x, float y);
}
