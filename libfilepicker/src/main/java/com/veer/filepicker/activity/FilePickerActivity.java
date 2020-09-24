package com.veer.filepicker.activity;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.constant.PermissionConstants;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.veer.filepicker.DirConstants;
import com.veer.filepicker.FolderListHelper;
import com.veer.filepicker.R;


/**
 * <li>Author:Veer</li>
 * <li>Date:  2020/3/6</li>
 * <li>Description: </li>
 */
public abstract class FilePickerActivity extends AppCompatActivity {
    private static final int RC_READ_EXTERNAL_STORAGE = 123;
    private static final String TAG = FilePickerActivity.class.getName();
    protected FolderListHelper mFolderHelper;
    protected boolean isNeedFolderList;
    public static final String IS_NEED_FOLDER_LIST = "isNeedFolderList";

    abstract void permissionGranted();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isNeedFolderList = getIntent().getBooleanExtra(IS_NEED_FOLDER_LIST, false);
        if (isNeedFolderList) {
            mFolderHelper = new FolderListHelper();
            mFolderHelper.initFolderListView(this);
        }

    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        PermissionUtils.permission(PermissionConstants.STORAGE,PermissionConstants.CAMERA).callback(new PermissionUtils.SimpleCallback() {
            @Override
            public void onGranted() {
                permissionGranted();
                FileUtils.createOrExistsDir(DirConstants.DIR_PICTURE);
                FileUtils.createOrExistsDir(DirConstants.DIR_VIDEOS);
                FileUtils.createOrExistsDir(DirConstants.DIR_AUDIOS);
                FileUtils.createOrExistsDir(DirConstants.DIR_FILE);
            }

            @Override
            public void onDenied() {
                ToastUtils.showShort(R.string.permission_denied_camera_storage);
            }
        }).request();
    }

    public void onBackClick(View view) {
        onBackPressed();
    }
}
