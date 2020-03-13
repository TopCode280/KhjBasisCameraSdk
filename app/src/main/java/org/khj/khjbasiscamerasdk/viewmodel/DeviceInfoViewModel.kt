package org.khj.khjbasiscamerasdk.viewmodel

import android.text.TextUtils
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.adorkable.iosdialog.IOSAlertDialog
import com.khj.Camera
import org.khj.khjbasiscamerasdk.*
import org.khj.khjbasiscamerasdk.av_modle.CameraWrapper
import org.khj.khjbasiscamerasdk.base.BaseActivity
import org.khj.khjbasiscamerasdk.base.BaseViewModel

class DeviceInfoViewModel : BaseViewModel() {

    var deviceName = MutableLiveData<String>()
    var deviceType = MutableLiveData<Int>()
    var deviceInfo = MutableLiveData<ArrayList<String>>()
    var devMacIP = MutableLiveData<HashMap<String, String>>()
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
        querySdCard()
        getMacIP()
    }

    fun querySdCard() {
        camera?.queryDevInfo {
            deviceInfo.postValue(it)
        }
    }

    fun getMacIP() {
        camera?.getDevMacIP {
            devMacIP.postValue(it)
        }
    }

    fun showFormatDialog(activity: BaseActivity) {
        activity.run {
            if (!isFinishing) {
                IOSAlertDialog(activity)
                    .init()
                    .setTitle(getString(R.string.formatSdcard))
                    .setMsg(getString(R.string.ensureFormatSdcard))
                    .setPositiveButton(getString(R.string.commit), View.OnClickListener {
                        showLoading()
                        camera?.formatSdCardExtension({
                            dismissLoading()
                            showToast.postValue(it == 0)
                        }, {
                            dismissLoading()
                        })
                    }).setNegativeButton(getString(R.string.cancel), View.OnClickListener {
                    }).setClickBlankCancellation(false).show()
            }
        }
    }
}