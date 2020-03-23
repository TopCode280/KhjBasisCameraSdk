package org.khj.khjbasiscamerasdk.viewmodel

import android.os.Bundle
import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import com.khj.Camera
import com.vise.log.ViseLog
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.khj.khjbasiscamerasdk.App
import org.khj.khjbasiscamerasdk.av_modle.CameraWrapper
import org.khj.khjbasiscamerasdk.base.BaseActivity
import org.khj.khjbasiscamerasdk.base.DeviceBaseViewModel
import org.khj.khjbasiscamerasdk.getDevVolume
import org.khj.khjbasiscamerasdk.getFlipStatus
import org.khj.khjbasiscamerasdk.getTimeZoneExtension
import org.khj.khjbasiscamerasdk.view.dialogfragment.TimeZoneDialog
import org.khj.khjbasiscamerasdk.view.dialogfragment.callBackInterface.SettingTimeZoneCallBack
import java.text.DecimalFormat

class DeviceSettingViewModel : DeviceBaseViewModel(),SettingTimeZoneCallBack {

    var activity: BaseActivity? = null
    val isPictureFlip = MutableLiveData<Boolean>()
    val deviceVolume = MutableLiveData<Int>()
    val visionMode = MutableLiveData<Int>()
    val timeZone = MutableLiveData<String>()

    override fun setCamera(cameraWrapper: CameraWrapper, camera: Camera, vararg args: Any) {
        this.cameraWrapper = cameraWrapper
        this.camera = camera
        this.activity = args[0] as BaseActivity
        getPictureFlipStatus()
        getDeviceVolume()
        getNigheSwitch()
        getTimeZone()
    }

    fun getPictureFlipStatus() {
        camera?.getFlipStatus {
            isPictureFlip.postValue(it)
        }
    }

    fun setPictureFlipStatus(isCheck: Boolean) { // 1 翻转 0 不翻转
        GlobalScope.launch {
            camera!!.setFlipping(if (isCheck) 1 else 0) {
                showToast.postValue(it)
                isPictureFlip.postValue(if (it) isCheck else !isCheck)
            }
        }
    }

    fun getDeviceVolume() {
        camera?.getDevVolume {
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
                showToast.postValue(it)
                if (it) {
                    cameraWrapper?.switchStatus = finalI
                }
                visionMode.postValue(if (it && isCheck) 2 else -1)
            }
        }
    }

    fun getTimeZone() {
        camera?.getTimeZoneExtension {
            timeZone.postValue(division(it))
        }
    }

    fun showTimeSettingDialogFragment() {
        if (TextUtils.isEmpty(timeZone.value)) {
            Toasty.error(App.context, "查询失败").show()
            return
        }
        val timezoneDialog = TimeZoneDialog()
        val bundle = Bundle()
        bundle.putString("UID", cameraWrapper?.deviceEntity?.deviceUid)
        bundle.putFloat("TIME_ZONE", timeZone.value!!.toFloat())
        timezoneDialog.setArguments(bundle)
        timezoneDialog.show(activity!!.supportFragmentManager, "TimezoneDialog")
        timezoneDialog.setTimeZoneCallBack(this)
    }

    //整数相除 保留一位小数
    private fun division(time: Int): String? {
        return DecimalFormat("0.0").format(time / 60.toDouble())
    }

    override fun onSuccess(timeZone: Int?) {
        this.timeZone.postValue(division(timeZone!!))
    }

    override fun onFailure() {
    }


}