package org.khj.khjbasiscamerasdk.viewmodel

import com.khj.Camera
import org.khj.khjbasiscamerasdk.av_modle.CameraWrapper
import org.khj.khjbasiscamerasdk.base.BaseActivity
import org.khj.khjbasiscamerasdk.base.DeviceBaseViewModel
import org.khj.khjbasiscamerasdk.view.dialogfragment.callBackInterface.SettingTimeZoneCallBack

class DeviceTimedTaskViewModel  : DeviceBaseViewModel() {

    var activity: BaseActivity? = null

    override fun setCamera(cameraWrapper: CameraWrapper, camera: Camera, vararg args: Any) {
        this.cameraWrapper = cameraWrapper
        this.camera = camera
        this.activity = args[0] as BaseActivity
    }
}