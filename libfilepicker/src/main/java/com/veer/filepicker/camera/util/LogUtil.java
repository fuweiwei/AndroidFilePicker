package com.veer.filepicker.camera.util;


import com.blankj.utilcode.util.LogUtils;
/**
 * <li>Author:Veer</li>
 * <li>Date:  2020/3/6</li>
 * <li>Description: </li>
 */
public class LogUtil {

    private static final String DEFAULT_TAG = "CJT";

    public static void i(String tag, String msg) {
//        if (DEBUG)
        LogUtils.i(tag, msg);
    }

    public static void v(String tag, String msg) {
        LogUtils.v(tag, msg);
    }

    public static void d(String tag, String msg) {
        LogUtils.d(tag, msg);
    }

    public static void e(String tag, String msg) {
        LogUtils.e(tag, msg);
    }

    public static void i(String msg) {
        i(DEFAULT_TAG, msg);
    }

    public static void v(String msg) {
        v(DEFAULT_TAG, msg);
    }

    public static void d(String msg) {
        d(DEFAULT_TAG, msg);
    }

    public static void e(String msg) {
        e(DEFAULT_TAG, msg);
    }
}
