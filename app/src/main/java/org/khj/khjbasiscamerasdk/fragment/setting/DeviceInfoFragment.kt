package org.khj.khjbasiscamerasdk.fragment.setting

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.View
import android.widget.Button
import androidx.lifecycle.Observer
import com.qmuiteam.qmui.util.QMUIViewHelper
import kotlinx.android.synthetic.main.fragment_deviceinfo.*
import kotlinx.android.synthetic.main.topbar.*
import org.khj.khjbasiscamerasdk.R
import org.khj.khjbasiscamerasdk.base.BaseDeviceFragment
import org.khj.khjbasiscamerasdk.viewmodel.DeviceInfoViewModel
import org.khj.khjbasiscamerasdk.viewmodel.DeviceSettingViewModel

class DeviceInfoFragment : BaseDeviceFragment() {

    private var rightTextButton: Button? = null
    private var viewModel: DeviceInfoViewModel? = null

    override fun contentViewId() = R.layout.fragment_deviceinfo

    override fun initView() {
        super.initView()
        topbar.setTitle(getString(R.string.deviceInfo))
        topbar.addLeftBackImageButton().setOnClickListener { back() }
    }

    @SuppressLint("SetTextI18n")
    override fun initData() {
        super.initData()
        viewModel = getViewModel(DeviceInfoViewModel::class.java)
        viewModel?.run {
            setCamera(
                this@DeviceInfoFragment.cameraWrapper!!,
                this@DeviceInfoFragment.camera!!
            )
            deviceName.observe(this@DeviceInfoFragment, Observer {
                tv_deviceName.text = it
            })
            deviceType.observe(this@DeviceInfoFragment, Observer {
                tv_deviceType.text = "IPC $it"
            })
        }
    }
}