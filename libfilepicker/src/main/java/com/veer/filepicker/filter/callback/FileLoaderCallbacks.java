package com.veer.filepicker.filter.callback;

import android.content.Context;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.veer.filepicker.FilePickerUtil;
import com.veer.filepicker.filter.entity.AudioFile;
import com.veer.filepicker.filter.entity.Directory;
import com.veer.filepicker.filter.entity.ImageFile;
import com.veer.filepicker.filter.entity.MediaFile;
import com.veer.filepicker.filter.entity.NormalFile;
import com.veer.filepicker.filter.entity.VideoFile;
import com.veer.filepicker.filter.loader.AudioLoader;
import com.veer.filepicker.filter.loader.FileLoader;
import com.veer.filepicker.filter.loader.ImageLoader;
import com.veer.filepicker.filter.loader.MediaLoader;
import com.veer.filepicker.filter.loader.VideoLoader;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.provider.BaseColumns._ID;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE;
import static android.provider.MediaStore.Files.FileColumns.MIME_TYPE;
import static android.provider.MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME;
import static android.provider.MediaStore.Images.ImageColumns.BUCKET_ID;
import static android.provider.MediaStore.Images.ImageColumns.ORIENTATION;
import static android.provider.MediaStore.MediaColumns.DATA;
import static android.provider.MediaStore.MediaColumns.DATE_ADDED;
import static android.provider.MediaStore.MediaColumns.DISPLAY_NAME;
import static android.provider.MediaStore.MediaColumns.SIZE;
import static android.provider.MediaStore.MediaColumns.TITLE;
import static android.provider.MediaStore.Video.VideoColumns.DURATION;

/**
 * <li>Author:Veer</li>
 * <li>Date:  2020/3/6</li>
 * <li>Description: </li>
 */

public class FileLoaderCallbacks implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final int TYPE_IMAGE = 0;
    public static final int TYPE_VIDEO = 1;
    public static final int TYPE_AUDIO = 2;
    public static final int TYPE_FILE = 3;
    public static final int TYPE_MEDIA = 4;

    private WeakReference<Context> context;
    private FilterResultCallback resultCallback;

    private int mType = TYPE_IMAGE;
    private String[] mSuffixArgs;
    private CursorLoader mLoader;
    private String mSuffixRegex;

    public FileLoaderCallbacks(Context context, FilterResultCallback resultCallback, int type) {
        this(context, resultCallback, type, null);
    }

    public FileLoaderCallbacks(Context context, FilterResultCallback resultCallback, int type, String[] suffixArgs) {
        this.context = new WeakReference<>(context);
        this.resultCallback = resultCallback;
        this.mType = type;
        this.mSuffixArgs = suffixArgs;
        if (suffixArgs != null && suffixArgs.length > 0) {
            mSuffixRegex = obtainSuffixRegex(suffixArgs);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (mType) {
            case TYPE_IMAGE:
                mLoader = new ImageLoader(context.get());
                break;
            case TYPE_VIDEO:
                mLoader = new VideoLoader(context.get());
                break;
            case TYPE_AUDIO:
                mLoader = new AudioLoader(context.get());
                break;
            case TYPE_FILE:
                mLoader = new FileLoader(context.get());
                break;
            case TYPE_MEDIA:
                mLoader = new MediaLoader(context.get());
                break;
        }

        return mLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data == null) return;
        switch (mType) {
            case TYPE_IMAGE:
                onImageResult(data);
                break;
            case TYPE_VIDEO:
                onVideoResult(data);
                break;
            case TYPE_AUDIO:
                onAudioResult(data);
                break;
            case TYPE_FILE:
                onFileResult(data);
                break;
            case TYPE_MEDIA:
                onMediaResult(data);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @SuppressWarnings("unchecked")
    private void onMediaResult(Cursor data) {
        List<Directory<MediaFile>> directories = new ArrayList<>();

        if (data.getPosition() != -1) {
            data.moveToPosition(-1);
        }

        while (data.moveToNext()) {
            //Create a File instance
            MediaFile media = new MediaFile();
            media.setId(data.getLong(data.getColumnIndexOrThrow(_ID)));
            media.setName(data.getString(data.getColumnIndexOrThrow(DISPLAY_NAME)));
            media.setPath(data.getString(data.getColumnIndexOrThrow(DATA)));
            media.setSize(data.getLong(data.getColumnIndexOrThrow(SIZE)));
            media.setBucketId(data.getString(data.getColumnIndexOrThrow(BUCKET_ID)));
            media.setBucketName(data.getString(data.getColumnIndexOrThrow(BUCKET_DISPLAY_NAME)));
            media.setDate(data.getLong(data.getColumnIndexOrThrow(DATE_ADDED)));

            media.setDuration(data.getLong(data.getColumnIndexOrThrow(DURATION)));
            media.setMediaType(data.getString(data.getColumnIndexOrThrow(MEDIA_TYPE)));
            if(media.getDuration()>0){
                //获取媒体的宽高
                MediaMetadataRetriever mmr = new  MediaMetadataRetriever();
                mmr.setDataSource(media.getPath());
                int width = Integer.parseInt(mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
                int height =  Integer.parseInt(mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
                String rotation = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);//视频的方向角度
                //竖屏拍摄 要旋转一下 宽高
                if("90".equals(rotation)){
                    media.setWidth(height);
                    media.setHeight(width);
                }else{
                    media.setWidth(width);
                    media.setHeight(height);
                }
            }
            //Create a Directory
            Directory<MediaFile> directory = new Directory<>();
            directory.setId(media.getBucketId());
            directory.setName(media.getBucketName());
            directory.setPath(FilePickerUtil.extractPathWithoutSeparator(media.getPath()));

            if (!directories.contains(directory)) {
                directory.addFile(media);
                directories.add(directory);
            } else {
                directories.get(directories.indexOf(directory)).addFile(media);
            }
        }

        if (resultCallback != null) {
            resultCallback.onResult(directories);
        }
    }
    @SuppressWarnings("unchecked")
    private void onImageResult(Cursor data) {
        List<Directory<ImageFile>> directories = new ArrayList<>();

        if (data.getPosition() != -1) {
            data.moveToPosition(-1);
        }

        while (data.moveToNext()) {
            //Create a File instance
            ImageFile img = new ImageFile();
            img.setId(data.getLong(data.getColumnIndexOrThrow(_ID)));
            img.setName(data.getString(data.getColumnIndexOrThrow(TITLE)));
            img.setPath(data.getString(data.getColumnIndexOrThrow(DATA)));
            img.setSize(data.getLong(data.getColumnIndexOrThrow(SIZE)));
            img.setBucketId(data.getString(data.getColumnIndexOrThrow(BUCKET_ID)));
            img.setBucketName(data.getString(data.getColumnIndexOrThrow(BUCKET_DISPLAY_NAME)));
            img.setDate(data.getLong(data.getColumnIndexOrThrow(DATE_ADDED)));

            img.setOrientation(data.getInt(data.getColumnIndexOrThrow(ORIENTATION)));

            //Create a Directory
            Directory<ImageFile> directory = new Directory<>();
            directory.setId(img.getBucketId());
            directory.setName(img.getBucketName());
            directory.setPath(FilePickerUtil.extractPathWithoutSeparator(img.getPath()));

            if (!directories.contains(directory)) {
                directory.addFile(img);
                directories.add(directory);
            } else {
                directories.get(directories.indexOf(directory)).addFile(img);
            }
        }

        if (resultCallback != null) {
            resultCallback.onResult(directories);
        }
    }

    @SuppressWarnings("unchecked")
    private void onVideoResult(final Cursor data) {
        List<Directory<VideoFile>> directories = new ArrayList<>();

        if (data.getPosition() != -1) {
            data.moveToPosition(-1);
        }

        while (data.moveToNext()) {
            //Create a File instance
            VideoFile video = new VideoFile();
            video.setId(data.getLong(data.getColumnIndexOrThrow(_ID)));
            video.setName(data.getString(data.getColumnIndexOrThrow(TITLE)));
            video.setPath(data.getString(data.getColumnIndexOrThrow(DATA)));
            video.setSize(data.getLong(data.getColumnIndexOrThrow(SIZE)));
            video.setBucketId(data.getString(data.getColumnIndexOrThrow(BUCKET_ID)));
            video.setBucketName(data.getString(data.getColumnIndexOrThrow(BUCKET_DISPLAY_NAME)));
            video.setDate(data.getLong(data.getColumnIndexOrThrow(DATE_ADDED)));

            video.setDuration(data.getLong(data.getColumnIndexOrThrow(DURATION)));

            //Create a Directory
            Directory<VideoFile> directory = new Directory<>();
            directory.setId(video.getBucketId());
            directory.setName(video.getBucketName());
            directory.setPath(FilePickerUtil.extractPathWithoutSeparator(video.getPath()));

            if (!directories.contains(directory)) {
                directory.addFile(video);
                directories.add(directory);
            } else {
                directories.get(directories.indexOf(directory)).addFile(video);
            }
        }

        if (resultCallback != null) {
            resultCallback.onResult(directories);
        }
    }

    @SuppressWarnings("unchecked")
    private void onAudioResult(Cursor data) {
        List<Directory<AudioFile>> directories = new ArrayList<>();

        if (data.getPosition() != -1) {
            data.moveToPosition(-1);
        }

        while (data.moveToNext()) {
            //Create a File instance
            AudioFile audio = new AudioFile();
            audio.setId(data.getLong(data.getColumnIndexOrThrow(_ID)));
            audio.setName(data.getString(data.getColumnIndexOrThrow(TITLE)));
            audio.setPath(data.getString(data.getColumnIndexOrThrow(DATA)));
            audio.setSize(data.getLong(data.getColumnIndexOrThrow(SIZE)));
            audio.setDate(data.getLong(data.getColumnIndexOrThrow(DATE_ADDED)));

            audio.setDuration(data.getLong(data.getColumnIndexOrThrow(DURATION)));

            //Create a Directory
            Directory<AudioFile> directory = new Directory<>();
            directory.setName(FilePickerUtil.extractFileNameWithSuffix(FilePickerUtil.extractPathWithoutSeparator(audio.getPath())));
            directory.setPath(FilePickerUtil.extractPathWithoutSeparator(audio.getPath()));

            if (!directories.contains(directory)) {
                directory.addFile(audio);
                directories.add(directory);
            } else {
                directories.get(directories.indexOf(directory)).addFile(audio);
            }
        }

        if (resultCallback != null) {
            resultCallback.onResult(directories);
        }
    }

    @SuppressWarnings("unchecked")
    private void onFileResult(Cursor data) {
        List<Directory<NormalFile>> directories = new ArrayList<>();

        if (data.getPosition() != -1) {
            data.moveToPosition(-1);
        }

        while (data.moveToNext()) {
            String path = data.getString(data.getColumnIndexOrThrow(DATA));
            if (path != null && contains(path)) {
                //Create a File instance
                NormalFile file = new NormalFile();
                file.setId(data.getLong(data.getColumnIndexOrThrow(_ID)));
                file.setName(data.getString(data.getColumnIndexOrThrow(DISPLAY_NAME)));
                file.setPath(data.getString(data.getColumnIndexOrThrow(DATA)));
                file.setSize(data.getLong(data.getColumnIndexOrThrow(SIZE)));
                file.setDate(data.getLong(data.getColumnIndexOrThrow(DATE_ADDED)));

                file.setMimeType(data.getString(data.getColumnIndexOrThrow(MIME_TYPE)));

                //Create a Directory
                Directory<NormalFile> directory = new Directory<>();
                directory.setName(FilePickerUtil.extractFileNameWithSuffix(FilePickerUtil.extractPathWithoutSeparator(file.getPath())));
                directory.setPath(FilePickerUtil.extractPathWithoutSeparator(file.getPath()));

                if (!directories.contains(directory)) {
                    directory.addFile(file);
                    directories.add(directory);
                } else {
                    directories.get(directories.indexOf(directory)).addFile(file);
                }
            }
        }

        if (resultCallback != null) {
            resultCallback.onResult(directories);
        }
    }

    private boolean contains(String path) {
        if(TextUtils.isEmpty(mSuffixRegex)){
            return true;
        }
        String name = FilePickerUtil.extractFileNameWithSuffix(path);
        Pattern pattern = Pattern.compile(mSuffixRegex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(name);
        return matcher.matches();
    }
    private String obtainSuffixRegex(String[] suffixes) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < suffixes.length ; i++) {
            if (i ==0) {
                builder.append(suffixes[i].replace(".", ""));
            } else {
                builder.append("|\\.");
                builder.append(suffixes[i].replace(".", ""));
            }
        }
        return ".+(\\." + builder.toString() + ")$";
    }
}
