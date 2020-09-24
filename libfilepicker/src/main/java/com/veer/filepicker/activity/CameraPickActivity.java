package com.veer.filepicker.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.blankj.utilcode.util.FileUtils;
import com.veer.filepicker.R;
import com.veer.filepicker.camera.ZCameraView;
import com.veer.filepicker.camera.listener.ClickListener;
import com.veer.filepicker.camera.listener.ErrorListener;
import com.veer.filepicker.camera.listener.ZCameraListener;
import com.veer.filepicker.camera.util.FileUtil;
import com.veer.filepicker.filter.entity.MediaFile;

import java.io.File;

import static com.veer.filepicker.FilePickerConstant.RESULT_CODE_CAMERA_ERROR;
import static com.veer.filepicker.FilePickerConstant.RESULT_CODE_CAMERA_PIC;
import static com.veer.filepicker.FilePickerConstant.RESULT_CODE_CAMERA_VIDEO;
import static com.veer.filepicker.FilePickerConstant.RESULT_PICK_CAMERA;

/**
 * <li>Author:Veer</li>
 * <li>Date:  2020/3/6</li>
 * <li>Description:同时支持拍摄图片和视频 </li>
 */
public class CameraPickActivity extends FilePickerActivity {
    public  static final String EXTRA_TEXT = "EXTRA_TEXT";
    public  static final String EXTRA_BTN = "EXTRA_BTN";
    private ZCameraView zCameraView;
    private String textString;  //底部文案
    private String btnString; //底部 按钮文案
    @Override
    void permissionGranted() {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vw_activity_camera_pick);
        if(getIntent()!=null&&getIntent().getExtras()!=null){
            textString = getIntent().getExtras().getString(EXTRA_TEXT);
            btnString = getIntent().getExtras().getString(EXTRA_BTN);
        }
        zCameraView = (ZCameraView) findViewById(R.id.cameraview);
        //设置视频保存缓存路径
        zCameraView.setSaveVideoPath(getExternalCacheDir().getAbsolutePath());
        zCameraView.setFeatures(ZCameraView.BUTTON_STATE_BOTH);
        zCameraView.setTip("点击拍照，长按摄像");
        zCameraView.setBottomConfirmText(textString,btnString);
        zCameraView.setMediaQuality(ZCameraView.MEDIA_QUALITY_MIDDLE);
        zCameraView.setErrorLisenter(new ErrorListener() {
            @Override
            public void onError() {
                //错误监听
                Intent intent = new Intent();
                setResult(RESULT_CODE_CAMERA_ERROR, intent);
                finish();
            }

            @Override
            public void AudioPermissionError() {
                Toast.makeText(CameraPickActivity.this, getString(R.string.permission_denied_mic), Toast.LENGTH_SHORT).show();
            }
        });
        //ZCameraView监听
        zCameraView.setZCameraLisenter(new ZCameraListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void captureSuccess(Bitmap bitmap) {
                //获取图片bitmap
                String path = FileUtil.saveBitmap(getExternalMediaDirs()[0].getAbsolutePath(),bitmap);
                FileUtil.scanPhotoAlbum(new File(path));
                MediaFile mediaFile = new MediaFile();
                mediaFile.setPath(path);
                Intent intent = new Intent();
                intent.putExtra(RESULT_PICK_CAMERA, mediaFile);
                setResult(RESULT_CODE_CAMERA_PIC, intent);
                finish();
            }

            @Override
            public void recordSuccess(String path, Bitmap firstFrame) {
                if (TextUtils.isEmpty(path)) return;
                try {
                    MediaMetadataRetriever mmr = new  MediaMetadataRetriever();
                    mmr.setDataSource(path);
                    MediaFile mediaFile = new MediaFile();
                    long duration = Long.parseLong(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
                    int width = Integer.parseInt(mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
                    int height =  Integer.parseInt(mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
                    String rotation = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);//视频的方向角度
                    //竖屏拍摄 要旋转一下 宽高
                    if("90".equals(rotation)){
                        mediaFile.setWidth(height);
                        mediaFile.setHeight(width);
                    }else{
                        mediaFile.setWidth(width);
                        mediaFile.setHeight(height);
                    }
                    mediaFile.setPath(path);
                    mediaFile.setDuration(duration);
                    mediaFile.setName(FileUtils.getFileName(path));
                    mediaFile.setSize(FileUtils.getFileLength(path));

                    Intent intent = new Intent();
                    intent.putExtra(RESULT_PICK_CAMERA, mediaFile);
                    setResult(RESULT_CODE_CAMERA_VIDEO, intent);
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        zCameraView.setLeftClickListener(new ClickListener() {
            @Override
            public void onClick() {
                CameraPickActivity.this.finish();
            }
        });
        zCameraView.setRightClickListener(new ClickListener() {
            @Override
            public void onClick() {
//                Toast.makeText(CameraPickActivity.this,"Right",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
