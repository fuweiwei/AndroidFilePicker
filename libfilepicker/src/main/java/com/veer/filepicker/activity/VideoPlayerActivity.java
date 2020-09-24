package com.veer.filepicker.activity;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.blankj.utilcode.constant.PermissionConstants;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.veer.filepicker.DirConstants;
import com.veer.filepicker.FilePickerUtil;
import com.veer.filepicker.R;

import java.io.File;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * <li>Author:Veer</li>
 * <li>Date:  2020/3/6</li>
 * <li>Description: 视频播放页面</li>
 */
public class VideoPlayerActivity extends FilePickerActivity {
    public static final String EXTRA_PATH = "extra_path";
    public static final String EXTRA_WIDTH = "extra_width";
    public static final String EXTRA_HEIGHT = "extra_height";
    private Context mContext;
    private final String TAG = "VideoPlayerView";
    private SurfaceView sv;
    private ImageView ivPlayStatus;
    private ImageView ivPlayer;
    private ImageView ivClose;
    private ImageView ivSave;
    private TextView tvStartTime,tvEndTime;
    private MediaPlayer mediaPlayer;
    private SeekBar seekBar;
    private int currentPosition = 0;
    //是否已经播放
    private boolean isHasPlaying;
    private String mPath;
    private int mWidth;
    private int mHeight;
    @Override
    void permissionGranted() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vw_activity_video_player);
        if(getIntent()!=null){
            mPath = getIntent().getExtras().getString(EXTRA_PATH,"");
            mWidth = getIntent().getExtras().getInt(EXTRA_WIDTH,0);
            mHeight = getIntent().getExtras().getInt(EXTRA_HEIGHT,0);
        }
        if(mWidth>0&&mHeight>0){
            int screenWidth = ScreenUtils.getScreenWidth();
            int screenHeight = ScreenUtils.getScreenHeight();
            float bi =((float) screenWidth)/((float)mWidth);
            mWidth = screenWidth;
            mHeight = (int) (mHeight*bi);
        }

        if(TextUtils.isEmpty(mPath)){
            ToastUtils.showShort("文件丢失");
            return;
        }
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        sv = (SurfaceView) findViewById(R.id.sv);
        ivPlayStatus = (ImageView) findViewById(R.id.iv_play_status);
        ivPlayer = (ImageView) findViewById(R.id.iv_player);
        ivClose = (ImageView) findViewById(R.id.iv_close);
        ivSave = (ImageView) findViewById(R.id.iv_save);
        tvStartTime = (TextView) findViewById(R.id.tv_time_start);
        tvEndTime = (TextView) findViewById(R.id.tv_time_end);

        ivPlayStatus.setOnClickListener(click);
        ivPlayer.setOnClickListener(click);
        ivClose.setOnClickListener(click);
        ivSave.setOnClickListener(click);

        // 为SurfaceHolder添加回调
        sv.getHolder().addCallback(callback);

        // 4.0版本之下需要设置的属性
        // 设置Surface不维护自己的缓冲区，而是等待屏幕的渲染引擎将内容推送到界面
        // sv.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        // 为进度条添加进度更改事件
        seekBar.setOnSeekBarChangeListener(change);
        //自动播放
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                play(0);
            }
        },100);
    }
    private SurfaceHolder.Callback callback = new SurfaceHolder.Callback() {
        // SurfaceHolder被修改的时候回调
        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            LogUtils.i(TAG, "SurfaceHolder 被销毁");
            // 销毁SurfaceHolder的时候记录当前的播放位置并停止播放
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                currentPosition = mediaPlayer.getCurrentPosition();
                mediaPlayer.stop();
            }
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            LogUtils.i(TAG, "SurfaceHolder 被创建");
            if (currentPosition > 0) {
                // 创建SurfaceHolder的时候，如果存在上次播放的位置，则按照上次播放位置进行播放
                play(currentPosition);
                currentPosition = 0;
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                   int height) {
            LogUtils.i(TAG, "SurfaceHolder 大小被改变");
        }

    };

    private SeekBar.OnSeekBarChangeListener change = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // 当进度条停止修改的时候触发
            // 取得当前进度条的刻度
            int progress = seekBar.getProgress();
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                // 设置当前播放的位置
                mediaPlayer.seekTo(progress);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {

        }
    };

    private View.OnClickListener click = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.iv_play_status||id==R.id.iv_player) {
                if(mediaPlayer!=null&&mediaPlayer.isPlaying()){
                    ivPlayer.setVisibility(VISIBLE);
                    mediaPlayer.pause();
                    ivPlayStatus.setImageResource(R.drawable.vw_ic_play);
                }else{
                    ivPlayer.setVisibility(GONE);
                    ivPlayStatus.setImageResource(R.drawable.vw_ic_pause);
                    if(isHasPlaying&&mediaPlayer!=null){
                        mediaPlayer.start();
                    }else{
                        play(0);
                    }
                }
            }else if(id == R.id.iv_close){
                onBackPressed();
            }else if(id == R.id.iv_save){
                // TODO: 2020/8/19 这里没有兼任Android10 存储  使用MediaStore 后续完善
                PermissionUtils.permission(PermissionConstants.STORAGE).callback(new PermissionUtils.SimpleCallback() {
                    @Override
                    public void onGranted() {
                        if(!TextUtils.isEmpty(mPath)&& FileUtils.isFileExists(mPath)){
                            String filePath = DirConstants.DIR_VIDEOS+ "Video_"+ System.currentTimeMillis()+".mp4";
                            if(FileUtils.copy(mPath,filePath)){
                                ToastUtils.showShort("视频已保存"+filePath);
                                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(filePath))));
                            }else{
                                ToastUtils.showShort("视频保存失败");
                            }
                        }else{
                            ToastUtils.showShort("文件错误");
                        }
                    }

                    @Override
                    public void onDenied() {
                        ToastUtils.showShort("请打开文件存储权限");
                    }
                }).request();

            }
        }
    };


    /*
     * 停止播放
     */
    protected void stop() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            isHasPlaying = false;
        }
    }

    /**
     * 开始播放
     *
     * @param msec 播放初始位置
     */
    protected void play(final int msec) {
        // 获取视频文件地址
        File file = new File(mPath);
        if (!file.exists()) {
            ToastUtils.showShort("视频文件路径错误");
            return;
        }
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            // 设置播放的视频源
            mediaPlayer.setDataSource(file.getAbsolutePath());
            // 设置显示视频的SurfaceHolder
            mediaPlayer.setDisplay(sv.getHolder());
            if(mWidth>0&&mHeight>0){
                sv.getHolder().setFixedSize(mWidth,mHeight);
            }
            LogUtils.i(TAG, "开始装载");
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                @Override
                public void onPrepared(MediaPlayer mp) {
                    LogUtils.i(TAG, "装载完成");
                    mediaPlayer.start();
                    // 按照初始位置播放
                    mediaPlayer.seekTo(msec);
                    // 设置进度条的最大进度为视频流的最大播放时长
                    seekBar.setMax(mediaPlayer.getDuration());
                    tvEndTime.setText(FilePickerUtil.getDurationString(mediaPlayer.getDuration()));
                    // 开始线程，更新进度条的刻度
                    new Thread() {

                        @Override
                        public void run() {
                            try {
                                isHasPlaying = true;
                                while (isHasPlaying) {
                                    final int current = mediaPlayer
                                            .getCurrentPosition();
                                    LogUtils.d(TAG,"current:"+current);
                                    VideoPlayerActivity.this.runOnUiThread(new Runnable() {
                                        public void run() {
                                            seekBar.setProgress(current);
                                            tvStartTime.setText(FilePickerUtil.getDurationString(current));
                                        }
                                    });
                                    sleep(1000);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                }
            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    // 在播放完毕被回调
                    isHasPlaying = false;
                    tvStartTime.setText(FilePickerUtil.getDurationString(0));
                    ivPlayStatus.setImageResource(R.drawable.vw_ic_play);
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer=null;
                    seekBar.setProgress(0);
                    ivPlayer.setVisibility(VISIBLE);
                }
            });

            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {

                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    // 发生错误重新播放
                    play(0);
                    isHasPlaying = false;
                    return false;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer=null;
        }
    }
}
