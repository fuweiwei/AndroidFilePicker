package com.veer.filepicker;


import com.blankj.utilcode.util.SDCardUtils;
import com.blankj.utilcode.util.Utils;

import java.io.File;

public class DirConstants {
    // APP目录
    public static final String DIR_WORK = SDCardUtils.getSDCardPathByEnvironment() + "/filepicker/";
    //Android 10 外部私有目录 应用卸载会删除
    public static final String DIR_PRIVATE_MEDIA = Utils.getApp().getExternalMediaDirs()[0].getAbsolutePath()+File.separator;
    public static final String DIR_PRIVATE_CACHE = Utils.getApp().getExternalCacheDir().getAbsolutePath()+File.separator;
    public static final String DIR_PRIVATE_FILE = Utils.getApp().getExternalFilesDir("").getAbsolutePath()+File.separator;
    // 图片
    public static final String DIR_PICTURE = DIR_WORK + "pic/";
    // 视频路径
    public static final String DIR_VIDEOS = DIR_WORK + "videos/";
    // 音频路径
    public static final String DIR_AUDIOS = DIR_WORK + "audios/";
    // 文件目录
    public static final String DIR_FILE = DIR_WORK + "file/";

}
