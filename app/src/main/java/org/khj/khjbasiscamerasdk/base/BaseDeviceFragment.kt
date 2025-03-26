package org.khj.khjbasiscamerasdk.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.khj.Camera
import com.vise.log.ViseLog
import org.khj.khjbasiscamerasdk.App
import org.khj.khjbasiscamerasdk.activity.FragmentLoadActivity
import org.khj.khjbasiscamerasdk.av_modle.CameraManager
import org.khj.khjbasiscamerasdk.av_modle.CameraWrapper
import org.khj.khjbasiscamerasdk.utils.WiFiUtil
import org.khjsdk.com.khjsdk_2020.value.MyConstans

abstract class BaseDeviceFragment<VB : ViewBinding> : BaseFragment<VB>() {

    protected var cameraWrapper: CameraWrapper? = null
    protected var camera: Camera? = null
    protected var ssid: String? = null
    protected var apMode = false
    protected var uid: String? = null
    protected val myconstans: MyConstans by lazy {
        MyConstans()
    }

    protected abstract val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> VB

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?): VB {
        return bindingInflater(inflater, container, false)
    }

    override fun initView() {
    }

    override fun setListeners() {
    }

    override fun initData() {
        uid = (mActivity as FragmentLoadActivity).getDeviceUid()
        ssid = WiFiUtil.getInstance(App.context).getSSID()
        if (ssid != null && ssid!!.contains("camera_")) {
            apMode = true
        }
        ViseLog.i("是否为Ap模式 $apMode")
        if (apMode) {
            cameraWrapper = CameraManager.getInstance().getCameraWrapper(uid)
            if (cameraWrapper == null) {
                cameraWrapper = CameraManager.getInstance().getApCamera(uid)
            }
            ViseLog.i("cameraWrapper $apMode $cameraWrapper")
        } else {
            cameraWrapper = CameraManager.getInstance().getCameraWrapper(uid)
            ViseLog.i("cameraWrappe r$apMode $cameraWrapper")
        }
        if (cameraWrapper == null) {
            finish()
            return
        }
        camera = cameraWrapper!!.getmCamera()
        if (camera == null) {
            finish()
            return
        }
    }

    override fun loadData() {
    }
}