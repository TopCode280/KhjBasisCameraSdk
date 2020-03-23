package org.khj.khjbasiscamerasdk.viewmodel

import androidx.lifecycle.MutableLiveData
import com.khj.Camera
import org.khj.khjbasiscamerasdk.*
import org.khj.khjbasiscamerasdk.av_modle.CameraWrapper
import org.khj.khjbasiscamerasdk.base.DeviceBaseViewModel
import java.util.concurrent.atomic.AtomicBoolean

class MoveDetectViewModel : DeviceBaseViewModel() {

    val alarmSwitch: MutableLiveData<Boolean> = MutableLiveData()
    val alarmDetectionValue: MutableLiveData<Int> = MutableLiveData()
    val setAlarmDetectionResult: MutableLiveData<Boolean> = MutableLiveData()
    val currentSensitivity:MutableLiveData<Int> = MutableLiveData()

    val toastString:MutableLiveData<String> = MutableLiveData()

    override fun setCamera(cameraWrapper: CameraWrapper, camera: Camera, vararg args: Any) {
        this.cameraWrapper = cameraWrapper
        this.camera = camera
        alarmSwitch.value = false
        setAlarmDetectionResult.value = false
        alarmDetectionValue.value = -1
        currentSensitivity.value  = -1
        getAlarmSwitch()
    }

    fun getAlarmSwitch() {
        camera!!.getAlarmSwitchExtension {
            alarmSwitch.postValue(it)
        }
    }

    fun getAlarmDetect() {
        camera!!.getMotionDetectExtension {
            alarmDetectionValue.postValue(it)
        }
    }

    fun setMotionDetect(value: Int) {
        camera!!.setMotionDetectExtension(value) {
            setAlarmDetectionResult.postValue(it)
            if (it){
                currentSensitivity.postValue(value)
                toastString.postValue(App.Companion.context.getString(R.string.modifySuccess))
            }else{
                toastString.postValue(App.Companion.context.getString(R.string.modifyFailure))
            }
        }
    }

}