package com.veer.filepicker.camera.util;

import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.webkit.MimeTypeMap;


import com.blankj.utilcode.util.Utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
/**
 * <li>Author:Veer</li>
 * <li>Date:  2020/3/6</li>
 * <li>Description: </li>
 */
public class FileUtil {
    public static String saveBitmap(String dir, Bitmap b) {
        String dataTake = System.currentTimeMillis()+"";
        String jpegName = new File(dir , "camera_" + dataTake + ".jpg").getAbsolutePath();
        try {
            FileOutputStream fout = new FileOutputStream(jpegName);
            BufferedOutputStream bos = new BufferedOutputStream(fout);
            b.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
            return jpegName;
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static boolean deleteFile(String url) {
        boolean result = false;
        File file = new File(url);
        if (file.exists()) {
            result = file.delete();
        }
        return result;
    }
    /**
     * 当确认保存此文件时才去扫描相册更新并显示视频和图片
     *
     * @param dataFile
     */
    public static void scanPhotoAlbum(File dataFile) {
        if (dataFile == null) {
            return;
        }
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(dataFile.getAbsolutePath().substring(dataFile.getAbsolutePath().lastIndexOf(".") + 1));
        MediaScannerConnection.scanFile(
                Utils.getApp(), new String[]{dataFile.getAbsolutePath()}, new String[]{mimeType}, null);
    }
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }
}
