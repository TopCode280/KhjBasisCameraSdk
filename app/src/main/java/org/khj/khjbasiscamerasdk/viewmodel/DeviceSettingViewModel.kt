package org.khj.khjbasiscamerasdk.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.khj.Camera
import com.vise.log.ViseLog
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.khj.khjbasiscamerasdk.av_modle.CameraWrapper
import org.khj.khjbasiscamerasdk.getFlipStatus

class DeviceSettingViewModel : ViewModel() {

    val isPictureFlip = MutableLiveData<Boolean>()
    val deviceVolume = MutableLiveData<Int>()
    val visionMode = MutableLiveData<Int>()
    val settingSucess = MutableLiveData<Boolean>()
    var cameraWrapper: CameraWrapper? = null
    var camera: Camera? = null

    fun setCamera(cameraWrapper: CameraWrapper, camera: Camera) {
        this.cameraWrapper = cameraWrapper
        this.camera = camera
        getPictureFlipStatus()
        getDeviceVolume()
        getNigheSwitch()
    }

    fun getPictureFlipStatus() {
        camera?.getFlipStatus {
            isPictureFlip.postValue(it)
        }
    }

    fun setPictureFlipStatus(isCheck: Boolean) { // 1 翻转 0 不翻转
        GlobalScope.launch {
            camera!!.setFlipping(if (isCheck) 1 else 0) {
                settingSucess.postValue(it)
                isPictureFlip.postValue(if (it) isCheck else !isCheck)
            }
        }
    }

    fun getDeviceVolume() {
        camera?.getDeviceVolume {
            ViseLog.i("getDeviceVolume it = $it")
            deviceVolume.postValue(it)
        }
    }

    fun getNigheSwitch() {
        val switchStatus = cameraWrapper!!.switchStatus
        val i = switchStatus and 2
        visionMode.postValue(i)
        ViseLog.i("switchStatus $switchStatus * $i")
    }

    fun setNigheSwitch(finalI: Int, isCheck: Boolean) {
        GlobalScope.launch {
            camera!!.setSwitch(finalI) {
                settingSucess.postValue(it)
                if (it) {
                    cameraWrapper?.switchStatus = finalI
                }
                visionMode.postValue(if (it && isCheck) 2 else -1)
            }
        }
    }
}