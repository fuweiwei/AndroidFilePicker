package com.veer.filepicker.filter;


import androidx.fragment.app.FragmentActivity;

import com.veer.filepicker.filter.callback.FileLoaderCallbacks;
import com.veer.filepicker.filter.callback.FilterResultCallback;
import com.veer.filepicker.filter.entity.AudioFile;
import com.veer.filepicker.filter.entity.ImageFile;
import com.veer.filepicker.filter.entity.MediaFile;
import com.veer.filepicker.filter.entity.NormalFile;
import com.veer.filepicker.filter.entity.VideoFile;

/**
 * <li>Author:Veer</li>
 * <li>Date:  2020/3/6</li>
 * <li>Description: </li>
 */

public class FileFilter {
    public static void getImages(FragmentActivity activity, FilterResultCallback<ImageFile> callback){
        activity.getSupportLoaderManager().initLoader(0, null,
                new FileLoaderCallbacks(activity, callback, FileLoaderCallbacks.TYPE_IMAGE));
    }

    public static void getVideos(FragmentActivity activity, FilterResultCallback<VideoFile> callback){
        activity.getSupportLoaderManager().initLoader(1, null,
                new FileLoaderCallbacks(activity, callback, FileLoaderCallbacks.TYPE_VIDEO));
    }
    public static void getAudios(FragmentActivity activity, FilterResultCallback<AudioFile> callback){
        activity.getSupportLoaderManager().initLoader(2, null,
                new FileLoaderCallbacks(activity, callback, FileLoaderCallbacks.TYPE_AUDIO));
    }

    public static void getFiles(FragmentActivity activity,
                                FilterResultCallback<NormalFile> callback, String[] suffix){
        activity.getSupportLoaderManager().initLoader(3, null,
                new FileLoaderCallbacks(activity, callback, FileLoaderCallbacks.TYPE_FILE, suffix));
    }
    public static void getMedias(FragmentActivity activity, FilterResultCallback<MediaFile> callback){
        activity.getSupportLoaderManager().initLoader(4, null,
                new FileLoaderCallbacks(activity, callback, FileLoaderCallbacks.TYPE_MEDIA));
    }

}
