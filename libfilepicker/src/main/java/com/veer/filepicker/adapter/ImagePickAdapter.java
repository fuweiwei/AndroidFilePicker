package com.veer.filepicker.adapter;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.constant.PermissionConstants;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.veer.filepicker.FilePickerConstant;
import com.veer.filepicker.FPToastUtil;
import com.veer.filepicker.FilePickerUtil;
import com.veer.filepicker.R;
import com.veer.filepicker.activity.ImageBrowserActivity;
import com.veer.filepicker.activity.ImagePickActivity;
import com.veer.filepicker.filter.entity.ImageFile;

import java.io.File;
import java.util.ArrayList;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static com.veer.filepicker.activity.ImageBrowserActivity.IMAGE_BROWSER_INIT_INDEX;
import static com.veer.filepicker.activity.ImageBrowserActivity.IMAGE_BROWSER_SELECTED_DIRECTORY;
import static com.veer.filepicker.activity.ImageBrowserActivity.IMAGE_BROWSER_SELECTED_LIST;

/**
 * <li>Author:Veer</li>
 * <li>Date:  2020/3/6</li>
 * <li>Description: </li>
 */

public class ImagePickAdapter extends BaseAdapter<ImageFile, ImagePickAdapter.ImagePickViewHolder> {
    private boolean isNeedImagePager;
    private boolean isNeedCamera;
    private int mMaxNumber;
    private int mCurrentNumber = 0;
    public String mImagePath;
    public Uri mImageUri;

    public ImagePickAdapter(Context ctx, boolean needCamera, boolean isNeedImagePager, int max) {
        this(ctx, new ArrayList<ImageFile>(), needCamera, isNeedImagePager, max);
    }

    public ImagePickAdapter(Context ctx, ArrayList<ImageFile> list, boolean needCamera, boolean needImagePager, int max) {
        super(ctx, list);
        isNeedCamera = needCamera;
        mMaxNumber = max;
        isNeedImagePager = needImagePager;
    }

    @Override
    public ImagePickViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.vw_layout_item_image_pick, parent, false);
        ViewGroup.LayoutParams params = itemView.getLayoutParams();
        if (params != null) {
            WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            int width = wm.getDefaultDisplay().getWidth();
            params.height = width / ImagePickActivity.COLUMN_NUMBER;
        }
        return new ImagePickViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ImagePickViewHolder holder, int position) {
        if (isNeedCamera && position == 0) {
            holder.mIvCamera.setVisibility(View.VISIBLE);
            holder.mIvThumbnail.setVisibility(View.INVISIBLE);
            holder.mCbx.setVisibility(View.INVISIBLE);
            holder.mShadow.setVisibility(View.INVISIBLE);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isUpToMax()) {
                        FPToastUtil.getInstance(mContext).showToast(R.string.vw_up_to_max);
                        return;
                    }
                    PermissionUtils.permission(PermissionConstants.CAMERA).callback(new PermissionUtils.SimpleCallback() {
                        @Override
                        public void onGranted() {
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            String timeStamp = System.currentTimeMillis()+"";
                            File file = new File(Utils.getApp().getExternalMediaDirs()[0],"picture_"+timeStamp + ".jpg");
                            mImagePath = file.getAbsolutePath();

                            ContentValues contentValues = new ContentValues(1);
                            contentValues.put(MediaStore.Images.Media.DATA, mImagePath);
                            mImageUri = mContext.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);

                            intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
                            if (FilePickerUtil.detectIntent(mContext, intent)) {
                                ((Activity) mContext).startActivityForResult(intent, FilePickerConstant.REQUEST_CODE_TAKE_IMAGE);
                            } else {
                                FPToastUtil.getInstance(mContext).showToast(mContext.getString(R.string.vw_no_photo_app));
                            }
                        }

                        @Override
                        public void onDenied() {
                            ToastUtils.showShort("相机权限被拒，无法使用此功能");
                        }
                    }).request();

                }
            });
        } else {
            holder.mIvCamera.setVisibility(View.INVISIBLE);
            holder.mIvThumbnail.setVisibility(View.VISIBLE);
            holder.mCbx.setVisibility(View.VISIBLE);

            ImageFile file;
            if (isNeedCamera) {
                file = mList.get(position - 1);
            } else {
                file = mList.get(position);
            }

            try{
                RequestOptions options = new RequestOptions();
                Glide.with(mContext)
                        .load(file.getPath())
                        .apply(options.centerCrop())
                        .transition(withCrossFade())
//                    .transition(new DrawableTransitionOptions().crossFade(500))
                        .into(holder.mIvThumbnail);
            }catch (Exception e){
                LogUtils.e("GlideException",e.toString());
            }
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

                    int index = isNeedCamera ? holder.getAdapterPosition() - 1 : holder.getAdapterPosition();
                    if (v.isSelected()) {
                        holder.mShadow.setVisibility(View.INVISIBLE);
                        holder.mCbx.setSelected(false);
                        mCurrentNumber--;
                        mList.get(index).setSelected(false);
                    } else {
                        holder.mShadow.setVisibility(View.VISIBLE);
                        holder.mCbx.setSelected(true);
                        mCurrentNumber++;
                        mList.get(index).setSelected(true);
                    }

                    if (mListener != null) {
                        mListener.OnSelectStateChanged(holder.mCbx.isSelected(), mList.get(index));
                    }
                }
            });

            if (isNeedImagePager) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, ImageBrowserActivity.class);
                        intent.putExtra(FilePickerConstant.MAX_NUMBER, mMaxNumber);
                        intent.putExtra(IMAGE_BROWSER_INIT_INDEX,
                                isNeedCamera ? holder.getAdapterPosition() - 1 : holder.getAdapterPosition());
                        intent.putParcelableArrayListExtra(IMAGE_BROWSER_SELECTED_LIST, ((ImagePickActivity) mContext).mSelectedList);
                        intent.putExtra(IMAGE_BROWSER_SELECTED_DIRECTORY, ((ImagePickActivity) mContext).mSelectDirectoryPath);
                        ((Activity) mContext).startActivityForResult(intent, FilePickerConstant.REQUEST_CODE_BROWSER_IMAGE);
                    }
                });
            } else {
                holder.mIvThumbnail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!holder.mCbx.isSelected() && isUpToMax()) {
                            FPToastUtil.getInstance(mContext).showToast(R.string.vw_up_to_max);
                            return;
                        }

                        int index = isNeedCamera ? holder.getAdapterPosition() - 1 : holder.getAdapterPosition();
                        if (holder.mCbx.isSelected()) {
                            holder.mShadow.setVisibility(View.INVISIBLE);
                            holder.mCbx.setSelected(false);
                            mCurrentNumber--;
                            mList.get(index).setSelected(false);
                        } else {
                            holder.mShadow.setVisibility(View.VISIBLE);
                            holder.mCbx.setSelected(true);
                            mCurrentNumber++;
                            mList.get(index).setSelected(true);
                        }

                        if (mListener != null) {
                            mListener.OnSelectStateChanged(holder.mCbx.isSelected(), mList.get(index));
                        }
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return isNeedCamera ? mList.size() + 1 : mList.size();
    }

    class ImagePickViewHolder extends RecyclerView.ViewHolder {
        private ImageView mIvCamera;
        private ImageView mIvThumbnail;
        private View mShadow;
        private ImageView mCbx;

        public ImagePickViewHolder(View itemView) {
            super(itemView);
            mIvCamera = (ImageView) itemView.findViewById(R.id.iv_camera);
            mIvThumbnail = (ImageView) itemView.findViewById(R.id.iv_thumbnail);
            mShadow = itemView.findViewById(R.id.shadow);
            mCbx = (ImageView) itemView.findViewById(R.id.cbx);
        }
    }

    public boolean isUpToMax() {
        return mCurrentNumber >= mMaxNumber;
    }

    public void setCurrentNumber(int number) {
        mCurrentNumber = number;
    }
}
