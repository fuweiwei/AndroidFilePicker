package com.veer.filepicker.activity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.veer.filepicker.DividerGridItemDecoration;
import com.veer.filepicker.FilePickerConstant;
import com.veer.filepicker.R;
import com.veer.filepicker.adapter.FolderListAdapter;
import com.veer.filepicker.adapter.OnSelectStateListener;
import com.veer.filepicker.adapter.MediaPickAdapter;
import com.veer.filepicker.filter.FileFilter;
import com.veer.filepicker.filter.callback.FilterResultCallback;
import com.veer.filepicker.filter.entity.BaseFile;
import com.veer.filepicker.filter.entity.Directory;
import com.veer.filepicker.filter.entity.MediaFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * <li>Author:Veer</li>
 * <li>Date:  2020/3/6</li>
 * <li>Description:仿制微信选择媒体器 包括图片和视频 暂时不扩展支持相机 没有必要 </li>
 */
public class MediaPickActivity extends FilePickerActivity {
    public static final String THUMBNAIL_PATH = "MediaPick";
    public static final String IS_NEED_CAMERA = "IsNeedCamera";
    public static final String IS_TAKEN_AUTO_SELECTED = "IsTakenAutoSelected";

    public static final int DEFAULT_MAX_NUMBER = 9;
    public static final int COLUMN_NUMBER = 3;
    private int mMaxNumber;
    private int mCurrentNumber = 0;
    private RecyclerView mRecyclerView;
    private MediaPickAdapter mAdapter;
    private boolean isNeedCamera;
    private boolean isTakenAutoSelected;
    public ArrayList<MediaFile> mSelectedList = new ArrayList<>();
    private List<Directory<MediaFile>> mAll;
    private ProgressBar mProgressBar;

    private TextView tv_count;
    private TextView tv_folder;
    private LinearLayout ll_folder;
    private RelativeLayout rl_done;
    private RelativeLayout tb_pick;
    public  String mSelectDirectoryPath = null;

    @Override
    void permissionGranted() {
        loadData();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vw_activity_video_pick);

        mMaxNumber = getIntent().getIntExtra(FilePickerConstant.MAX_NUMBER, DEFAULT_MAX_NUMBER);
        isNeedCamera = getIntent().getBooleanExtra(IS_NEED_CAMERA, false);
        isTakenAutoSelected = getIntent().getBooleanExtra(IS_TAKEN_AUTO_SELECTED, true);
        initView();
    }

    private void initView() {
        tv_count = (TextView) findViewById(R.id.tv_count);
        tv_count.setText(mCurrentNumber + "/" + mMaxNumber);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_video_pick);
        GridLayoutManager layoutManager = new GridLayoutManager(this, COLUMN_NUMBER);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new DividerGridItemDecoration(this));

        mAdapter = new MediaPickAdapter(this, isNeedCamera, mMaxNumber);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnSelectStateListener(new OnSelectStateListener<MediaFile>() {
            @Override
            public void OnSelectStateChanged(boolean state, MediaFile file) {
                if (state) {
                    mSelectedList.add(file);
                    mCurrentNumber++;
                } else {
                    mSelectedList.remove(file);
                    mCurrentNumber--;
                }
                tv_count.setText(mCurrentNumber + "/" + mMaxNumber);
            }
        });

        mProgressBar = (ProgressBar) findViewById(R.id.pb_video_pick);
        File folder = new File(getExternalCacheDir().getAbsolutePath() + File.separator + THUMBNAIL_PATH);
        if (!folder.exists()) {
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.GONE);
        }

        rl_done = (RelativeLayout) findViewById(R.id.rl_done);
        rl_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putParcelableArrayListExtra(FilePickerConstant.RESULT_PICK_MEDIA, mSelectedList);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        tb_pick = (RelativeLayout) findViewById(R.id.tb_pick);
        ll_folder = (LinearLayout) findViewById(R.id.ll_folder);
        if (isNeedFolderList) {
            ll_folder.setVisibility(View.VISIBLE);
            ll_folder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mFolderHelper.toggle(tb_pick);
                }
            });
            tv_folder = (TextView) findViewById(R.id.tv_folder);
            tv_folder.setText(getResources().getString(R.string.vw_all));

            mFolderHelper.setFolderListListener(new FolderListAdapter.FolderListListener() {
                @Override
                public void onFolderListClick(Directory directory) {
                    mSelectDirectoryPath = directory.getPath();
                    mFolderHelper.toggle(tb_pick);
                    tv_folder.setText(directory.getName());

                    if (TextUtils.isEmpty(directory.getPath())) { //All
                        refreshData(mAll);
                    } else {
                        for (Directory<MediaFile> dir : mAll) {
                            if (dir.getPath().equals(directory.getPath())) {
                                List<Directory<MediaFile>> list = new ArrayList<>();
                                list.add(dir);
                                refreshData(list);
                                break;
                            }
                        }
                    }
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            //通过相机拍摄
            case FilePickerConstant.REQUEST_CODE_TAKE_MEDIA:
//                if (resultCode == RESULT_OK) {
////                    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
////                    File file = new File(mAdapter.mVideoPath);
////                    Uri contentUri = Uri.fromFile(file);
////                    mediaScanIntent.setData(contentUri);
////                    sendBroadcast(mediaScanIntent);
////
////                    loadData();
//
//                    Uri uri = data.getData();
//                    ContentResolver cr = this.getContentResolver();
//                    /** 数据库查询操作。
//                     * 第一个参数 uri：为要查询的数据库+表的名称。
//                     * 第二个参数 projection ： 要查询的列。
//                     * 第三个参数 selection ： 查询的条件，相当于SQL where。
//                     * 第三个参数 selectionArgs ： 查询条件的参数，相当于 ？。
//                     * 第四个参数 sortOrder ： 结果排序。
//                     */
//                    assert uri != null;
//                    Cursor cursor = cr.query(uri, null, null, null, null);
//                    if (cursor != null) {
//                        if (cursor.moveToFirst()) {
//                            // 视频ID:MediaStore.Audio.Media._ID
//                            int videoId = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID));
//                            // 视频名称：MediaStore.Audio.Media.TITLE
//                            String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
//                            // 视频路径：MediaStore.Audio.Media.DATA
//                            String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
////                            // 视频时长：MediaStore.Audio.Media.DURATION
////                            int duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));
////                            // 视频大小：MediaStore.Audio.Media.SIZE
////                            long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));
////                            // 视频缩略图路径：MediaStore.Images.Media.DATA
////                            String imagePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
////                            // 缩略图ID:MediaStore.Audio.Media._ID
////                            int imageId = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
//                            VideoFile videoFile = new VideoFile();
//                            videoFile.setPath(path);
//                            videoFile.setName(title);
//                            mSelectedList.add(videoFile);
//                            Intent intent = new Intent();
//                            intent.putParcelableArrayListExtra(FilePickerConstant.RESULT_PICK_VIDEO, mSelectedList);
//                            setResult(RESULT_OK, intent);
//                        }
//                        cursor.close();
//                        finish();
//                    }


                break;

            case FilePickerConstant.REQUEST_CODE_BROWSER_MEDIA:
                if (resultCode == RESULT_OK) {
                    ArrayList<MediaFile> list = data.getParcelableArrayListExtra(FilePickerConstant.RESULT_BROWSER_MEDIA);
                    mCurrentNumber = list.size();
                    mAdapter.setCurrentNumber(mCurrentNumber);
                    tv_count.setText(mCurrentNumber + "/" + mMaxNumber);
                    mSelectedList.clear();
                    mSelectedList.addAll(list);

                    for (MediaFile file : mAdapter.getDataSet()) {
                        if (mSelectedList.contains(file)) {
                            file.setSelected(true);
                        } else {
                            file.setSelected(false);
                        }
                    }
                    mAdapter.notifyDataSetChanged();
                }
                break;
        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }
    private void loadData() {
        FileFilter.getMedias(this, new FilterResultCallback<MediaFile>() {
            @Override
            public void onResult(List<Directory<MediaFile>> directories) {
                mProgressBar.setVisibility(View.GONE);
                // Refresh folder list
                if (isNeedFolderList) {
                    ArrayList<Directory> list = new ArrayList<>();
                    Directory all = new Directory();
                    all.setName(getResources().getString(R.string.vw_all));
                    list.add(all);
                    list.addAll(directories);
                    mFolderHelper.fillData(list);
                }

                mAll = directories;
                if (TextUtils.isEmpty(mSelectDirectoryPath)) { //All
                    refreshData(mAll);
                } else {
                    for (Directory<MediaFile> dir : mAll) {
                        if (dir.getPath().equals(mSelectDirectoryPath)) {
                            List<Directory<MediaFile>> list = new ArrayList<>();
                            list.add(dir);
                            refreshData(list);
                            break;
                        }
                    }
                }
            }
        });

    }

    private void refreshData(List<Directory<MediaFile>> directories) {
        boolean tryToFindTaken = isTakenAutoSelected;

        // if auto-select taken file is enabled, make sure requirements are met
        if (tryToFindTaken && !TextUtils.isEmpty(mAdapter.mVideoPath)) {
            File takenFile = new File(mAdapter.mVideoPath);
            tryToFindTaken = !mAdapter.isUpToMax() && takenFile.exists(); // try to select taken file only if max isn't reached and the file exists
        }

        List<MediaFile> list = new ArrayList<>();
        for (Directory<MediaFile> directory : directories) {
            list.addAll(directory.getFiles());

            // auto-select taken file?
            if (tryToFindTaken) {
                tryToFindTaken = findAndAddTaken(directory.getFiles());   // if taken file was found, we're done
            }
        }

        for (BaseFile file : mSelectedList) {
            int index = list.indexOf(file);
            if (index != -1) {
                list.get(index).setSelected(true);
            }
        }
        mAdapter.refresh(list);
    }

    private boolean findAndAddTaken(List<MediaFile> list) {
        for (MediaFile mediaFile : list) {
            if (mediaFile.getPath().equals(mAdapter.mVideoPath)) {
                mSelectedList.add(mediaFile);
                mCurrentNumber++;
                mAdapter.setCurrentNumber(mCurrentNumber);
                tv_count.setText(mCurrentNumber + "/" + mMaxNumber);

                return true;   // taken file was found and added
            }
        }
        return false;    // taken file wasn't found
    }
}
