package com.veer.filepicker.view;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blankj.utilcode.util.ScreenUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.veer.filepicker.R;
import com.veer.filepicker.activity.VideoPlayerActivity;
import com.veer.filepicker.filter.entity.MediaFile;


/**
 * <li>Author:Veer</li>
 * <li>Date:  2020/3/6</li>
 * <li>Description: </li>
 */
public class VideoPlayerView extends RelativeLayout {
    private Context mContext;
    private final String TAG = "VideoPlayerView";
    private MediaFile mMediaFile;
    private ImageView mIvPic,mIvPlayer;
    private int mWidth;
    private int mHeight;
    public VideoPlayerView(@NonNull Context context, MediaFile mediaFile) {
        this(context,null,mediaFile);
    }

    public VideoPlayerView(@NonNull Context context, @Nullable AttributeSet attrs, MediaFile mediaFile) {
        super(context, attrs);
        mContext = context;
        mMediaFile = mediaFile;
        initView();
    }

    private void initView(){
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_video_player,null,false);
        mIvPic = view.findViewById(R.id.iv_pic);
        mIvPlayer = view.findViewById(R.id.iv_player);
        mWidth = mMediaFile.getWidth();
        mHeight = mMediaFile.getHeight();
        if(mWidth>0&&mHeight>0){
            int screenWidth = ScreenUtils.getScreenWidth();
            int screenHeight = ScreenUtils.getScreenHeight();
            float bi =((float) screenWidth)/((float)mWidth);
            mWidth = screenWidth;
            mHeight = (int) (mHeight*bi);
            LayoutParams layoutParams = (LayoutParams) mIvPic.getLayoutParams();
            layoutParams.width = mWidth;
            layoutParams.height = mHeight;
            mIvPic.setLayoutParams(layoutParams);
        }
        RequestOptions options = new RequestOptions();
        Glide.with(mContext)
                .load(mMediaFile.getPath())
                .apply(options.centerCrop().frame(500))
                .into(mIvPic);
        mIvPlayer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, VideoPlayerActivity.class);
                intent.putExtra(VideoPlayerActivity.EXTRA_PATH,mMediaFile.getPath());
                intent.putExtra(VideoPlayerActivity.EXTRA_WIDTH,mMediaFile.getWidth());
                intent.putExtra(VideoPlayerActivity.EXTRA_HEIGHT,mMediaFile.getHeight());
                mContext.startActivity(intent);
            }
        });
        addView(view);
    }
}
