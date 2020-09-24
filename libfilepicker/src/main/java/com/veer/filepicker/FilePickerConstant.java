package com.veer.filepicker;

/**
 * <li>Author:Veer</li>
 * <li>Date:  2020/3/6</li>
 * <li>Description: </li>
 */

public class FilePickerConstant {
    public static final String MAX_NUMBER = "MaxNumber";

    //选择图片code
    public static final int REQUEST_CODE_PICK_IMAGE = 0x100;
    public static final String RESULT_PICK_IMAGE = "ResultPickImage";
    public static final int REQUEST_CODE_TAKE_IMAGE = 0x101;

    //图片查看code
    public static final int REQUEST_CODE_BROWSER_IMAGE = 0x102;
    public static final String RESULT_BROWSER_IMAGE = "ResultBrowserImage";
    //选择视频code
    public static final int REQUEST_CODE_PICK_VIDEO = 0x200;
    public static final String RESULT_PICK_VIDEO = "ResultPickVideo";
    public static final int REQUEST_CODE_TAKE_VIDEO = 0x201;
    //选择语音code
    public static final int REQUEST_CODE_PICK_AUDIO = 0x300;
    public static final String RESULT_PICK_AUDIO = "ResultPickAudio";
    public static final int REQUEST_CODE_TAKE_AUDIO = 0x301;
    //选择文件code
    public static final int REQUEST_CODE_PICK_FILE = 0x400;
    public static final String RESULT_PICK_FILE = "ResultPickFILE";

    //单相机code
    public static final int REQUEST_CODE_TAKE_CAMERA = 0x500;

    //微信摄像头code（点击拍摄长按摄像）
    public static final int RESULT_CODE_CAMERA_PIC = 0x601;
    public static final int RESULT_CODE_CAMERA_VIDEO = 0x602;
    public static final int RESULT_CODE_CAMERA_ERROR = 0x603;
    public static final String RESULT_PICK_CAMERA = "ResultPickCamera";

    //选择媒体code
    public static final int REQUEST_CODE_PICK_MEDIA = 0x700;
    public static final String RESULT_PICK_MEDIA = "ResultPickMedia";
    public static final int REQUEST_CODE_TAKE_MEDIA = 0x701;

    //媒体查看code
    public static final int REQUEST_CODE_BROWSER_MEDIA = 0x103;
    public static final String RESULT_BROWSER_MEDIA = "ResultBrowserMedia";
}
