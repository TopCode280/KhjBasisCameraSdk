package org.khj.khjbasiscamerasdk.viewmodel

import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.khj.Camera
import org.khj.khjbasiscamerasdk.App
import org.khj.khjbasiscamerasdk.R
import org.khj.khjbasiscamerasdk.av_modle.CameraWrapper

class DeviceInfoViewModel : ViewModel() {

    var deviceName = MutableLiveData<String>()
    var deviceType = MutableLiveData<Int>()
    var cameraWrapper: CameraWrapper? = null
    var camera: Camera? = null

    fun setCamera(cameraWrapper: CameraWrapper, camera: Camera) {
        this.cameraWrapper = cameraWrapper
        this.camera = camera
        deviceType.postValue(camera.devcieType)
        if (TextUtils.isEmpty(cameraWrapper.deviceEntity.deviceName)) {
            deviceName.postValue(App.context.getString(R.string.nullName))
        } else {
            deviceName.postValue(cameraWrapper.deviceEntity.deviceName)
        }
    }

}