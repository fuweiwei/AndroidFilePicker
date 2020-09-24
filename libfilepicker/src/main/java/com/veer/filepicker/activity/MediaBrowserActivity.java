package com.veer.filepicker.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bm.library.PhotoView;
import com.bumptech.glide.Glide;
import com.veer.filepicker.FPToastUtil;
import com.veer.filepicker.FilePickerConstant;
import com.veer.filepicker.R;
import com.veer.filepicker.filter.FileFilter;
import com.veer.filepicker.filter.callback.FilterResultCallback;
import com.veer.filepicker.filter.entity.Directory;
import com.veer.filepicker.filter.entity.MediaFile;
import com.veer.filepicker.view.VideoPlayerView;

import java.util.ArrayList;
import java.util.List;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * <li>Author:Veer</li>
 * <li>Date:  2020/3/6</li>
 * <li>Description: </li>
 */

public class MediaBrowserActivity extends FilePickerActivity {
    public static final String MEDIA_BROWSER_INIT_INDEX = "MediaBrowserInitIndex";
    public static final String MEDIA_BROWSER_SELECTED_LIST = "MediaBrowserSelectedList";
    public static final String MEDIA_BROWSER_SELECTED_DIRECTORY = "MediaBrowserSelectedDirectory";
    private int mMaxNumber;
    private int mCurrentNumber = 0;
    private int initIndex = 0;
    private int mCurrentIndex = 0;

    private ViewPager mViewPager;
    private Toolbar mTbImagePick;
    private ArrayList<MediaFile> mList = new ArrayList<>();
    private ImageView mSelectView;
    private ArrayList<MediaFile> mSelectedFiles;
    public String mSelectDirectoryPath ;

    @Override
    void permissionGranted() {
        loadData();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.vw_activity_media_browser);

        mMaxNumber = getIntent().getIntExtra(FilePickerConstant.MAX_NUMBER, ImagePickActivity.DEFAULT_MAX_NUMBER);
        initIndex = getIntent().getIntExtra(MEDIA_BROWSER_INIT_INDEX, 0);
        mCurrentIndex = initIndex;
        mSelectedFiles = getIntent().getParcelableArrayListExtra(MEDIA_BROWSER_SELECTED_LIST);
        mSelectDirectoryPath =  getIntent().getStringExtra(MEDIA_BROWSER_SELECTED_DIRECTORY);
        mCurrentNumber = mSelectedFiles.size();


        super.onCreate(savedInstanceState);
    }

    private void initView() {
        mTbImagePick = (Toolbar) findViewById(R.id.tb_image_pick);
        mTbImagePick.setTitle(mCurrentNumber + "/" + mMaxNumber);
        setSupportActionBar(mTbImagePick);
        mTbImagePick.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishThis();
            }
        });

        mSelectView = (ImageView) findViewById(R.id.cbx);
        mSelectView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!v.isSelected() && isUpToMax()) {
                    FPToastUtil.getInstance(MediaBrowserActivity.this).showToast(R.string.vw_up_to_max);
                    return;
                }

                if (v.isSelected()) {
                    mList.get(mCurrentIndex).setSelected(false);
                    mCurrentNumber--;
                    v.setSelected(false);
                    mSelectedFiles.remove(mList.get(mCurrentIndex));
                } else {
                    mList.get(mCurrentIndex).setSelected(true);
                    mCurrentNumber++;
                    v.setSelected(true);
                    mSelectedFiles.add(mList.get(mCurrentIndex));
                }

                mTbImagePick.setTitle(mCurrentNumber + "/" + mMaxNumber);
            }
        });

        mViewPager = (ViewPager) findViewById(R.id.vp_image_pick);
        mViewPager.setPageMargin((int) (getResources().getDisplayMetrics().density * 15));
        mViewPager.setAdapter(new ImageBrowserAdapter());
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mCurrentIndex = position;
                mSelectView.setSelected(mList.get(mCurrentIndex).isSelected());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mViewPager.setCurrentItem(initIndex, false);
        mSelectView.setSelected(mList.get(mCurrentIndex).isSelected());
    }

    private void loadData() {
        FileFilter.getMedias(this, new FilterResultCallback<MediaFile>() {
            @Override
            public void onResult(List<Directory<MediaFile>> directories) {
                mList.clear();
                if(mSelectDirectoryPath!=null){
                    for(Directory directory:directories){
                        if(directory.getPath().equals(mSelectDirectoryPath)){
                            mList.addAll(directory.getFiles());
                            break;
                        }
                    }
                }else{
                    for (Directory<MediaFile> directory : directories) {
                        mList.addAll(directory.getFiles());
                    }
                }
                for (MediaFile file : mList) {
                    if (mSelectedFiles.contains(file)) {
                        file.setSelected(true);
                    }
                }

                initView();
                mViewPager.getAdapter().notifyDataSetChanged();
            }
        });
    }

    private class ImageBrowserAdapter extends PagerAdapter {
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            MediaFile mediaFile = mList.get(position);
            if(mediaFile.getDuration()>0){
                //视频
                VideoPlayerView view = new VideoPlayerView(MediaBrowserActivity.this,mediaFile);
                container.addView(view);
                return view;
            }else{
                //图片
                PhotoView view = new PhotoView(MediaBrowserActivity.this);
                view.enable();
                view.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

                Glide.with(MediaBrowserActivity.this)
                        .load(mediaFile.getPath())
                        .into(view);
                container.addView(view);
                return view;
            }

        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.vw_menu_image_pick, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_done) {
            finishThis();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isUpToMax() {
        return mCurrentNumber >= mMaxNumber;
    }

    private void finishThis() {
        Intent intent = new Intent();
        intent.putParcelableArrayListExtra(FilePickerConstant.RESULT_BROWSER_MEDIA, mSelectedFiles);
//        intent.putExtra(IMAGE_BROWSER_SELECTED_NUMBER, mCurrentNumber);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        finishThis();
    }
}
