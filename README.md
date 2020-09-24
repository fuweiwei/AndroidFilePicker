# AndroidFilePicker
FilePicker(支持图片，视频，文件，语音，摄像头点击拍照长按摄像)

使用：
```
 val intentMedia = Intent(this, AudioPickActivity::class.java)
        intentMedia.putExtra(FilePickerActivity.IS_NEED_FOLDER_LIST, true)
        intentMedia.putExtra(FilePickerConstant.MAX_NUMBER, 9)
        startActivityForResult(intentMedia, FilePickerConstant.REQUEST_CODE_PICK_AUDIO)
 ```
        
接收：
 //选择文件
 ```
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
    ```
   
