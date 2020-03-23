package org.khj.khjbasiscamerasdk.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.khj.Camera
import org.khj.khjbasiscamerasdk.av_modle.CameraWrapper

abstract class DeviceBaseViewModel : ViewModel() {
    var showToast = MutableLiveData<Boolean>()
    var cameraWrapper: CameraWrapper? = null
    var camera: Camera? = null

    abstract fun setCamera(cameraWrapper: CameraWrapper, camera: Camera, vararg args: Any)
}