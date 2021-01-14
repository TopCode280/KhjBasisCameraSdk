package org.khj.khjbasiscamerasdk.viewmodel

import android.app.Activity
import android.content.DialogInterface
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.adorkable.iosdialog.IOSAlertDialog
import com.khj.Camera
import com.qmuiteam.qmui.widget.dialog.QMUIDialog.CheckableDialogBuilder
import com.vise.log.ViseLog
import org.khj.khjbasiscamerasdk.*
import org.khj.khjbasiscamerasdk.av_modle.CameraWrapper
import org.khj.khjbasiscamerasdk.base.BaseActivity
import org.khj.khjbasiscamerasdk.base.DeviceBaseViewModel
import org.khj.khjbasiscamerasdk.bean.TimeSlot
import org.khj.khjbasiscamerasdk.utils.TimePlanManager

class RecordVideoSettingViewModel : DeviceBaseViewModel() {

    var currentQuality: MutableLiveData<Int> = MutableLiveData()  // 录制视频清晰度
    var videoRecordStatus: MutableLiveData<Int> = MutableLiveData()
    var showRecodPlanViews: MutableLiveData<Boolean> = MutableLiveData()
    var deviceInfo = MutableLiveData<ArrayList<String>>()
    var slotArrayList = MutableLiveData<ArrayList<TimeSlot>>()
    var loaddingStatus = MutableLiveData<Boolean>()
    var settingResultToastShow = MutableLiveData<Boolean>()
    var settingResultToastMsg = MutableLiveData<String>()

    override fun setCamera(cameraWrapper: CameraWrapper, camera: Camera, vararg args: Any) {
        this.cameraWrapper = cameraWrapper
        this.camera = camera
        currentQuality.value = -1
        videoRecordStatus.value = -1
        slotArrayList.value = ArrayList()
        settingResultToastMsg.value = ""
        queryStatus()
    }

    fun queryStatus() {
        camera?.let {
            it.getRecordVideoQualityExtension {
                ViseLog.e("当前设置的录像质量 ${it - 1}");
                currentQuality.postValue(it - 1)
            }
            videoRecordStatus.value = it.videoRecordStatus
            it.getVideoRecordTypeExtension {
                videoRecordStatus.postValue(it)
                showRecodPlanViews.postValue(videoRecordStatus.value == 2)
                ViseLog.e("当前设置的录像模式$it")
            }
        }
        querySdCard()
        queryRecordPlan()
    }

    fun querySdCard() {
        camera?.let {
            it.queryDevInfo {
                deviceInfo.postValue(it)
            }
        }
    }

    fun queryRecordPlan() {
        camera?.getTimedRecordVideoTaskExtension {
            TimePlanManager.getInstance().fromPlan(it)
            slotArrayList.postValue(TimePlanManager.getInstance().slotArrayList)
        }
    }

    fun recordingClicklisten(mActivity: Activity, recorModeArray: Array<String>) {
        val hasSdcard = cameraWrapper!!.deviceInfo.hasSdcard
        if (!hasSdcard) {
            settingResultToastMsg.postValue(App.context.getString(R.string.sdcardNotAvailable))
            return
        }
        val checkableDialogBuilder: CheckableDialogBuilder = CheckableDialogBuilder(mActivity)
            .setTitle(R.string.selectRecordMode)
            .addItems(
                recorModeArray
            ) { dialog: DialogInterface, which: Int ->
                if (which != videoRecordStatus.value) {
                    camera?.setVideoRecordTypeExtension(loaddingStatus, which) {
                        if (it){
                            videoRecordStatus.postValue(which)
                        }
                        settingResultToastShow.postValue(it)
                        showRecodPlanViews.postValue(videoRecordStatus.value == 2)
                    }
                }
                dialog.dismiss()
            }.setCheckedIndex(videoRecordStatus.value!!)
        val musicDialog = checkableDialogBuilder.create()
        musicDialog.setCanceledOnTouchOutside(true)
        musicDialog.show()
    }

    fun videoQualityClicklisten(mActivity: Activity, recorQualityArray: Array<String>) {
        val hasSdcard = cameraWrapper!!.deviceInfo.hasSdcard
        if (!hasSdcard) {
            settingResultToastMsg.postValue(App.context.getString(R.string.sdcardNotAvailable))
            return
        }
        val checkableDialogBuilder = CheckableDialogBuilder(mActivity)
            .setTitle(R.string.selectRecordQuality)
            .addItems(
                recorQualityArray
            ) { dialog: DialogInterface, which: Int ->
                if (which != currentQuality.value) {
                    camera?.setRecordVideoQualityExtension(loaddingStatus, which) {
                        if (it) {
                            currentQuality.postValue(which)
                        }
                        settingResultToastShow.postValue(it)
                    }
                }
                dialog.dismiss()
            }.setCheckedIndex(currentQuality.value!!)
        val musicDialog = checkableDialogBuilder.create()
        musicDialog.setCanceledOnTouchOutside(true)
        musicDialog.show()
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