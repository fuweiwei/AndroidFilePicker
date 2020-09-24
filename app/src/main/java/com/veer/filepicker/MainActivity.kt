package com.veer.filepicker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.veer.filepicker.activity.*
import com.veer.filepicker.filter.entity.AudioFile
import com.veer.filepicker.filter.entity.MediaFile
import com.veer.filepicker.filter.entity.NormalFile
import java.util.ArrayList

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val intentMedia = Intent(this, AudioPickActivity::class.java)
        intentMedia.putExtra(FilePickerActivity.IS_NEED_FOLDER_LIST, true)
        intentMedia.putExtra(FilePickerConstant.MAX_NUMBER, 9)
        startActivityForResult(intentMedia, FilePickerConstant.REQUEST_CODE_PICK_AUDIO)
    }

    fun pick(){
        val intentMedia = Intent(this, CameraPickActivity::class.java)
        startActivityForResult(intentMedia, FilePickerConstant.REQUEST_CODE_TAKE_CAMERA)

//        val intentMedia = Intent(this, MediaPickActivity::class.java)
//        intentMedia.putExtra(FilePickerActivity.IS_NEED_FOLDER_LIST, true)
//        intentMedia.putExtra(FilePickerConstant.MAX_NUMBER, 9)
//        startActivityForResult(intentMedia, FilePickerConstant.REQUEST_CODE_PICK_MEDIA)
//
//        val intentMedia = Intent(this, NormalFilePickActivity::class.java)
//        intentMedia.putExtra(FilePickerActivity.IS_NEED_FOLDER_LIST, true)
//        intentMedia.putExtra(FilePickerConstant.MAX_NUMBER, 9)
//        intentMedia.putExtra(NormalFilePickActivity.SUFFIX, arrayOf("xlsx", "xls", "doc", "docx", "ppt", "pptx", "pdf","txt")
//        )
//        startActivityForResult(intentMedia, FilePickerConstant.REQUEST_CODE_PICK_FILE)
//
//
//        val intentMedia = Intent(this, AudioPickActivity::class.java)
//        intentMedia.putExtra(FilePickerActivity.IS_NEED_FOLDER_LIST, true)
//        intentMedia.putExtra(FilePickerConstant.MAX_NUMBER, 9)
//        startActivityForResult(intentMedia, FilePickerConstant.REQUEST_CODE_PICK_AUDIO)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //选择文件
        if(requestCode== FilePickerConstant.REQUEST_CODE_PICK_FILE){
            if (resultCode == RESULT_OK) {
                data?.let {
                    val list: ArrayList<NormalFile>? = it.getParcelableArrayListExtra<NormalFile>(FilePickerConstant.RESULT_PICK_FILE)
                }
            }
            return
        }
        //选择录音
        if(requestCode== FilePickerConstant.REQUEST_CODE_PICK_AUDIO){
            if (resultCode == RESULT_OK) {
                data?.let {
                    val list: ArrayList<AudioFile>? = it.getParcelableArrayListExtra<AudioFile>(FilePickerConstant.RESULT_PICK_AUDIO)
                }
            }
            return
        }
        //选择媒体 图片和视频
        if(requestCode== FilePickerConstant.REQUEST_CODE_PICK_MEDIA){
            if (resultCode == RESULT_OK) {
                data?.let {
                    val list: ArrayList<MediaFile>? = it.getParcelableArrayListExtra<MediaFile>(FilePickerConstant.RESULT_PICK_MEDIA)
                }
            }
            return
        }
        //相机拍摄
        if(requestCode== FilePickerConstant.REQUEST_CODE_TAKE_CAMERA){
            if (resultCode == FilePickerConstant.RESULT_CODE_CAMERA_PIC || resultCode == FilePickerConstant.RESULT_CODE_CAMERA_VIDEO) {
                data?.let {
                    val mediaFile: MediaFile = it.extras?.get((FilePickerConstant.RESULT_PICK_CAMERA)) as MediaFile
                }
            }
            return
        }
    }

}