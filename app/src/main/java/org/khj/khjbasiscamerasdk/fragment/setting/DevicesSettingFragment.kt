package org.khj.khjbasiscamerasdk.fragment.setting

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.khj.Camera
import com.vise.log.ViseLog
import org.khj.khjbasiscamerasdk.R
import org.khj.khjbasiscamerasdk.av_modle.CameraWrapper
import org.khj.khjbasiscamerasdk.base.BaseDeviceFragment
import org.khj.khjbasiscamerasdk.databinding.FragmentDevicesBinding
import org.khj.khjbasiscamerasdk.databinding.FragmentDevicesettingBinding
import org.khj.khjbasiscamerasdk.databinding.FragmentTimePlanBinding
import org.khj.khjbasiscamerasdk.viewmodel.DeviceSettingViewModel

// 注 有部分设备因为固件问题会有设置上的不同，具体需要单独区分，更具设备类型或者Uid等。更具功能隐藏某项设置
// 若有一些设置无反应，需要检查设备,网络连接情况或者说设备固件不支持此功能
class DevicesSettingFragment:
    BaseDeviceFragment<FragmentDevicesettingBinding>(), View.OnClickListener {

    var ignore = true

    private val devcieType: Int by lazy {
        camera!!.getDevcieType()
    }

    private var viewModel: DeviceSettingViewModel? = null

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentDevicesettingBinding =
        { inflater, container, attachToParent ->
            FragmentDevicesettingBinding.inflate(inflater, container, attachToParent)
        }

    override fun initView() {
        super.initView()
        topBarBinding.topbar.setTitle(R.string.deviceSetting)
        topBarBinding.topbar.addLeftBackImageButton().setOnClickListener { finish() }

        if (cameraWrapper!!.getDevCap(CameraWrapper.Capability.WHITE_LIGHT)) {  // 如果有白关灯就有夜视功能
            binding.rlVision.visibility = View.VISIBLE
        }
    }

    @SuppressLint("SetTextI18n")
    override fun initData() {
        super.initData()
        viewModel = getViewModel(DeviceSettingViewModel()::class.java)
        viewModel?.run {
            setCamera(
                this@DevicesSettingFragment.cameraWrapper!!,
                this@DevicesSettingFragment.camera!!,
                mActivity
            )
            isPictureFlip.observe(this@DevicesSettingFragment, Observer {
                ignore = true
                binding.cbxReverse.isChecked = it
                ignore = false
            })
            deviceVolume.observe(this@DevicesSettingFragment, Observer {
                binding.tvVolume.text = "${it} /%"
            })
            visionMode.observe(this@DevicesSettingFragment, Observer {
                ignore = true
               binding.cbxVision.setChecked(it == 2)
                ignore = false
            })
            showToast.observe(this@DevicesSettingFragment, Observer {
                changeResultToast(it)
            })
            timeZone.observe(this@DevicesSettingFragment, Observer {
               binding.tvCurrentTimeZone.text = "UTC $it"
            })
        }
    }

    override fun setListeners() {
        super.setListeners()
       binding.cbxReverse.setOnCheckedChangeListener { _, isChecked ->
            if (!ignore) {
                viewModel?.setPictureFlipStatus(isChecked)
            }
        }
       binding.cbxVision.setOnCheckedChangeListener { _, isChecked ->
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
       binding.rlDeviceInfo.setOnClickListener(this)
       binding.rlTimeZoneSetting.setOnClickListener(this)
       binding.tvRecordVideo.setOnClickListener(this)
       binding.tvSelfCheck.setOnClickListener(this)
       binding.tvMedia.setOnClickListener(this)
       binding.tvAlarm.setOnClickListener(this)
       binding.tvOnOff.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        ViseLog.i("View ID = ${v!!.id}")
        fragmentHelp?.apply {
            when (v.id) {
                R.id.rl_deviceInfo -> {
                    val infoFragment = DeviceInfoFragment()
                    addToStack(infoFragment)
                    switchFragment(infoFragment, true)
                }

                R.id.rl_timeZoneSetting -> {
                    viewModel?.showTimeSettingDialogFragment()
                }

                R.id.tv_selfCheck -> {
                    camera?.setPtz(Camera.AVIOCTRL_MOTOR_RESET_POSITION, 0)
                }

                R.id.tv_media -> {
                    val mediaPictureFragment = MediaFragment()
                    addToStack(mediaPictureFragment)
                    switchFragment(mediaPictureFragment, true)
                }

                R.id.tv_alarm -> {
                    val mediaPictureFragment = MediaFragment()
                    addToStack(mediaPictureFragment)
                    switchFragment(mediaPictureFragment, true)
                }

                R.id.tv_onOff -> {
                    val mediaPictureFragment = MediaFragment()
                    addToStack(mediaPictureFragment)
                    switchFragment(mediaPictureFragment, true)
                }

                R.id.tv_recordVideo -> {
                    val recordVideoSettingFragment = RecordVideoSettingFragment()
                    addToStack(recordVideoSettingFragment)
                    switchFragment(recordVideoSettingFragment, true)
                }
            }
        }
    }
}
