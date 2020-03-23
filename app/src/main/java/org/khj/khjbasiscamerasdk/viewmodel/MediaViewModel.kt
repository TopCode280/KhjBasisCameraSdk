package org.khj.khjbasiscamerasdk.viewmodel

import androidx.lifecycle.MutableLiveData
import com.khj.Camera
import com.vise.log.ViseLog
import org.khj.khjbasiscamerasdk.App
import org.khj.khjbasiscamerasdk.av_modle.CameraWrapper
import org.khj.khjbasiscamerasdk.base.DeviceBaseViewModel
import org.khj.khjbasiscamerasdk.bean.FileInfoWrapper
import org.khj.khjbasiscamerasdk.getListvideoFileExtension
import org.khj.khjbasiscamerasdk.utils.FileUtil
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean

class MediaViewModel : DeviceBaseViewModel() {

    var end: Long? = null
    var deviceFolder: File? = null

    val noMoreFile: MutableLiveData<AtomicBoolean> = MutableLiveData()
    val dataList: MutableLiveData<ArrayList<FileInfoWrapper>> = MutableLiveData()
    val filesListByDate: MutableLiveData<List<File>> = MutableLiveData()
    val downLoadedFiles: MutableLiveData<ArrayList<String>> = MutableLiveData()
    val errorToast: MutableLiveData<String> = MutableLiveData()

    override fun setCamera(cameraWrapper: CameraWrapper, camera: Camera, vararg args: Any) {
        this.cameraWrapper = cameraWrapper
        this.camera = camera
        this.end = args[0]  as Long
        this.deviceFolder = args[1] as File
        noMoreFile.value = AtomicBoolean(false)
        dataList.value = ArrayList()
        filesListByDate.value = ArrayList()
        downLoadedFiles.value = ArrayList()
        queryDownloadedVideo()
    }


    fun refreshLayoutData(endTime: Long, deviceFolder: File,refresh:Boolean) {
        if (refresh){
            queryDownloadedVideo()
            camera?.listVideoFileStart(end!!)
            dataList.value?.clear()
        }
        camera?.getListvideoFileExtension(endTime, { result, boolean ->
            noMoreFile.postValue(AtomicBoolean(boolean!!))
            for (info: Camera.fileInfo in result!!) {
                val mp4Path = deviceFolder.absolutePath + "/" + info.filename.substring(
                    0,
                    info.filename.lastIndexOf(".")
                ) + "_" + "${App.userAccount}.mp4"
                val fileInfoWrapper = FileInfoWrapper(info.filename, mp4Path)
                downLoadedFiles.value?.run {
                    if (contains(fileInfoWrapper.downloadedName)) {
                        fileInfoWrapper.hasDownload = true
                    }
                }
                dataList.value?.add(fileInfoWrapper)
            }
        }, { string ->
            errorToast.postValue(string)
        })
    }

    /**
     * 查询已经下载的视频
     */
    private fun queryDownloadedVideo() {
        filesListByDate.postValue(
            FileUtil.getInstance(App.context).getFilesListByDate(
                end!! * 1000,
                deviceFolder!!.getAbsolutePath(),
                App.userAccount
            )
        )
        downLoadedFiles.value?.clear()
        filesListByDate.value?.let {
            for (file1 in it) {
                ViseLog.d("downloadFile" + file1.absolutePath)
                downLoadedFiles.value?.add(file1.absolutePath)
            }
        }
    }


}