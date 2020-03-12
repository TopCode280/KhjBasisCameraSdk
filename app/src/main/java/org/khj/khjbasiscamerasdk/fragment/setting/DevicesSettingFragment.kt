package org.khj.khjbasiscamerasdk.fragment.setting

import android.annotation.SuppressLint
import android.view.View
import androidx.lifecycle.Observer
import com.vise.log.ViseLog
import kotlinx.android.synthetic.main.fragment_devicesetting.*
import kotlinx.android.synthetic.main.topbar.*
import org.khj.khjbasiscamerasdk.R
import org.khj.khjbasiscamerasdk.av_modle.CameraWrapper
import org.khj.khjbasiscamerasdk.base.BaseDeviceFragment
import org.khj.khjbasiscamerasdk.viewmodel.DeviceSettingViewModel

// 注 有部分设备因为固件问题会有设置上的不同，具体需要单独区分，更具设备类型或者Uid等。更具功能隐藏某项设置
// 若有一些设置无反应，需要检查设备,网络连接情况或者说设备固件不支持此功能
class DevicesSettingFragment : BaseDeviceFragment(), View.OnClickListener {

    var ignore = true

    private val devcieType: Int by lazy {
        camera!!.getDevcieType()
    }

    private var viewModel: DeviceSettingViewModel? = null

    override fun contentViewId() = R.layout.fragment_devicesetting

    override fun initView() {
        super.initView()
        topbar.setTitle(R.string.deviceSetting)
        topbar.addLeftBackImageButton().setOnClickListener { finish() }

        if (cameraWrapper!!.getDevCap(CameraWrapper.Capability.WHITE_LIGHT)) {  // 如果有白关灯就有夜视功能
            rl_vision.visibility = View.VISIBLE
        }
    }

    @SuppressLint("SetTextI18n")
    override fun initData() {
        super.initData()
        viewModel = getViewModel(DeviceSettingViewModel()::class.java)
        viewModel?.run {
            setCamera(
                this@DevicesSettingFragment.cameraWrapper!!,
                this@DevicesSettingFragment.camera!!
            )
            isPictureFlip.observe(this@DevicesSettingFragment, Observer {
                ignore = true
                cbx_reverse.isChecked = it
                ignore = false
            })
            deviceVolume.observe(this@DevicesSettingFragment, Observer {
                tv_volume.text = "${it} /%"
            })
            visionMode.observe(this@DevicesSettingFragment, Observer {
                ignore = true
                cbx_vision.setChecked(it == 2)
                ignore = false
            })
            settingSucess.observe(this@DevicesSettingFragment, Observer {
                changeResultToast(it)
            })
        }
    }

    override fun setListeners() {
        super.setListeners()
        cbx_reverse.setOnCheckedChangeListener { _, isChecked ->
            if (!ignore) {
                viewModel?.setPictureFlipStatus(isChecked)
            }
        }
        cbx_vision.setOnCheckedChangeListener { _, isChecked ->
            if (!ignore) {
                val before = cameraWrapper!!.switchStatus
                var after = before
                after = if (isChecked) {
                    after or 2
                } else {
                    after and -0x3
                }
                ViseLog.d("switchBefore:$before*After:$after")
                val finalI = after
                viewModel?.setNigheSwitch(finalI, isChecked)
            }
        }
        rl_deviceInfo.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        ViseLog.i("View ID = ${v!!.id}")
        fragmentHelp?.run {
            when (v.id) {
                R.id.rl_deviceInfo -> {
                    val infoFragment = DeviceInfoFragment()
                    addToStack(infoFragment)
                    switchFragment(infoFragment, true)
                }
            }
        }
    }
}
