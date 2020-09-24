package com.veer.filepicker.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.veer.filepicker.FPToastUtil;
import com.veer.filepicker.FilePickerConstant;
import com.veer.filepicker.FilePickerUtil;
import com.veer.filepicker.R;
import com.veer.filepicker.activity.MediaBrowserActivity;
import com.veer.filepicker.activity.MediaPickActivity;
import com.veer.filepicker.activity.VideoPickActivity;
import com.veer.filepicker.filter.entity.MediaFile;

import java.util.ArrayList;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static com.veer.filepicker.FilePickerConstant.REQUEST_CODE_TAKE_VIDEO;
import static com.veer.filepicker.activity.MediaBrowserActivity.MEDIA_BROWSER_INIT_INDEX;
import static com.veer.filepicker.activity.MediaBrowserActivity.MEDIA_BROWSER_SELECTED_DIRECTORY;
import static com.veer.filepicker.activity.MediaBrowserActivity.MEDIA_BROWSER_SELECTED_LIST;

/**
 * <li>Package: com.zhaogang.filepicker.adapter</li>
 * <li>Author: weiwei.fu</li>
 * <li>Date:  2020/3/6</li>
 * <li>Description: </li>
 */

public class MediaPickAdapter extends BaseAdapter<MediaFile, MediaPickAdapter.VideoPickViewHolder> {
    private boolean isNeedCamera;
    private int mMaxNumber;
    private int mCurrentNumber = 0;
    public String mVideoPath;

    public MediaPickAdapter(Context ctx, boolean needCamera, int max) {
        this(ctx, new ArrayList<MediaFile>(), needCamera, max);
    }

    public MediaPickAdapter(Context ctx, ArrayList<MediaFile> list, boolean needCamera, int max) {
        super(ctx, list);
        isNeedCamera = needCamera;
        mMaxNumber = max;
    }

    @Override
    public MediaPickAdapter.VideoPickViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.vw_layout_item_media_pick, parent, false);
        ViewGroup.LayoutParams params = itemView.getLayoutParams();
        if (params != null) {
            WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            int width = wm.getDefaultDisplay().getWidth();
            params.height = width / VideoPickActivity.COLUMN_NUMBER;
        }
        return new VideoPickViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final VideoPickViewHolder holder, int position) {
        if (isNeedCamera && position == 0) {
            holder.mIvCamera.setVisibility(View.VISIBLE);
            holder.mIvThumbnail.setVisibility(View.INVISIBLE);
            holder.mCbx.setVisibility(View.INVISIBLE);
            holder.mShadow.setVisibility(View.INVISIBLE);
            holder.mDurationLayout.setVisibility(View.INVISIBLE);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
//                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date());
//                    File file = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_DCIM).getAbsolutePath()
//                            + "/VID_" + timeStamp + ".mp4");
//                    mVideoPath = file.getAbsolutePath();
//
//                    ContentValues contentValues = new ContentValues(1);
//                    contentValues.put(MediaStore.Images.Media.DATA, mVideoPath);
//                    Uri uri = mContext.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
//
//                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
//                    intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                    if (FilePickerUtil.detectIntent(mContext, intent)) {
                        ((Activity) mContext).startActivityForResult(intent, REQUEST_CODE_TAKE_VIDEO);
                    } else {
                        FPToastUtil.getInstance(mContext).showToast(mContext.getString(R.string.vw_no_video_app));
                    }
                }
            });
        } else {
            holder.mIvCamera.setVisibility(View.INVISIBLE);
            holder.mIvThumbnail.setVisibility(View.VISIBLE);
            holder.mCbx.setVisibility(View.VISIBLE);




            final MediaFile file;
            if (isNeedCamera) {
                file = mList.get(position - 1);
            } else {
                file = mList.get(position);
            }

            if(file.getDuration()>0){
                holder.mDurationLayout.setVisibility(View.VISIBLE);
                holder.mDuration.setText(FilePickerUtil.getDurationString(file.getDuration()));
            }else{
                holder.mDurationLayout.setVisibility(View.INVISIBLE);

            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, MediaBrowserActivity.class);
                    intent.putExtra(FilePickerConstant.MAX_NUMBER, mMaxNumber);
                    intent.putExtra(MEDIA_BROWSER_INIT_INDEX,
                            isNeedCamera ? holder.getAdapterPosition() - 1 : holder.getAdapterPosition());
                    intent.putParcelableArrayListExtra(MEDIA_BROWSER_SELECTED_LIST, ((MediaPickActivity) mContext).mSelectedList);
                    intent.putExtra(MEDIA_BROWSER_SELECTED_DIRECTORY, ((MediaPickActivity) mContext).mSelectDirectoryPath);
                    ((Activity) mContext).startActivityForResult(intent, FilePickerConstant.REQUEST_CODE_BROWSER_MEDIA);
                }
            });
            RequestOptions options = new RequestOptions();
            Glide.with(mContext)
                    .load(file.getPath())
                    .apply(options.centerCrop().frame(500))
                    .into(holder.mIvThumbnail);
            if (file.isSelected()) {
                holder.mCbx.setSelected(true);
                holder.mShadow.setVisibility(View.VISIBLE);
            } else {
                holder.mCbx.setSelected(false);
                holder.mShadow.setVisibility(View.INVISIBLE);
            }

            holder.mCbx.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!v.isSelected() && isUpToMax()) {
                        FPToastUtil.getInstance(mContext).showToast(R.string.vw_up_to_max);
                        return;
                    }

                    if(file.getDuration()>5*60*1000){
                        ToastUtils.showShort("选择的视频不能超过5分钟");
                        return;
                    }
                    if(file.getSize()>50*1024*1024){
                        ToastUtils.showShort("视频只能上传50M以内");
                        return;
                    }

                    if (v.isSelected()) {
                        holder.mShadow.setVisibility(View.INVISIBLE);
                        holder.mCbx.setSelected(false);
                        mCurrentNumber--;
                    } else {
                        holder.mShadow.setVisibility(View.VISIBLE);
                        holder.mCbx.setSelected(true);
                        mCurrentNumber++;
                    }

                    int index = isNeedCamera ? holder.getAdapterPosition() - 1 : holder.getAdapterPosition();
                    mList.get(index).setSelected(holder.mCbx.isSelected());

                    if (mListener != null) {
                        mListener.OnSelectStateChanged(holder.mCbx.isSelected(), mList.get(index));
                    }
                }
            });


        }
    }

    @Override
    public int getItemCount() {
        return isNeedCamera ? mList.size() + 1 : mList.size();
    }

    class VideoPickViewHolder extends RecyclerView.ViewHolder {
        private ImageView mIvCamera;
        private ImageView mIvThumbnail;
        private View mShadow;
        private ImageView mCbx;
        private TextView mDuration;
        private RelativeLayout mDurationLayout;

        public VideoPickViewHolder(View itemView) {
            super(itemView);
            mIvCamera = (ImageView) itemView.findViewById(R.id.iv_camera);
            mIvThumbnail = (ImageView) itemView.findViewById(R.id.iv_thumbnail);
            mShadow = itemView.findViewById(R.id.shadow);
            mCbx = (ImageView) itemView.findViewById(R.id.cbx);
            mDuration = (TextView) itemView.findViewById(R.id.txt_duration);
            mDurationLayout = (RelativeLayout) itemView.findViewById(R.id.layout_duration);
        }
    }

    public boolean isUpToMax() {
        return mCurrentNumber >= mMaxNumber;
    }

    public void setCurrentNumber(int number) {
        mCurrentNumber = number;
    }
}
