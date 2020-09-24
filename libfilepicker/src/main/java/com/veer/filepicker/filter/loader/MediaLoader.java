package com.veer.filepicker.filter.loader;

import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.loader.content.CursorLoader;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE;
import static android.provider.MediaStore.MediaColumns.DATE_ADDED;
import static android.provider.MediaStore.MediaColumns.MIME_TYPE;

/**
 * <li>Author: weiwei.fu</li>
 * <li>Date:  2020/3/6</li>
 * <li>Description: </li>
 */

public class MediaLoader extends CursorLoader {
    private static final String[] MEDIA_PROJECTION = {
            //Base File
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.BUCKET_ID,
            MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Video.Media.DATE_ADDED,
            //Media File
            MediaStore.Video.Media.DURATION,
            MEDIA_TYPE,
    };

    private MediaLoader(Context context, Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        super(context, uri, projection, selection, selectionArgs, sortOrder);
    }

    public MediaLoader(Context context) {
        super(context);

        setProjection(MEDIA_PROJECTION);
        setUri(MediaStore.Files.getContentUri("external"));
        setSortOrder(DATE_ADDED + " DESC");

        setSelection(MIME_TYPE + "=? or " + MIME_TYPE + "=? or "+MIME_TYPE + "=? or " + MIME_TYPE + "=? or "+ MIME_TYPE + "=? or " + MIME_TYPE + "=?");
        String[] selectionArgs;
        selectionArgs = new String[] { "video/quicktime", "video/mp4" ,"image/jpeg", "image/png", "image/jpg","image/gif"};
        setSelectionArgs(selectionArgs);
    }
}
