package org.khj.khjbasiscamerasdk.fragment.setting

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.Observer
import es.dmoral.toasty.Toasty
import org.khj.khjbasiscamerasdk.App
import org.khj.khjbasiscamerasdk.R
import org.khj.khjbasiscamerasdk.base.BaseDeviceFragment
import org.khj.khjbasiscamerasdk.databinding.FragmentDeviceinfoBinding
import org.khj.khjbasiscamerasdk.databinding.FragmentDevicesettingBinding
import org.khj.khjbasiscamerasdk.utils.ToastUtil
import org.khj.khjbasiscamerasdk.viewmodel.DeviceInfoViewModel

class DeviceInfoFragment : BaseDeviceFragment<FragmentDeviceinfoBinding>(), View.OnClickListener {

    private var rightTextButton: Button? = null
    private var viewModel: DeviceInfoViewModel? = null

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentDeviceinfoBinding =
        { inflater, container, attachToParent ->
            FragmentDeviceinfoBinding.inflate(inflater, container, attachToParent)
        }

    override fun initView() {
        super.initView()
        topBarBinding.topbar.setTitle(getString(R.string.deviceInfo))
        topBarBinding.topbar.addLeftBackImageButton().setOnClickListener { back() }
        binding.tvDeviceName.setBackgroundResource(R.drawable.selector_item_white)
        binding.tvDeviceUID.text = cameraWrapper?.deviceEntity?.deviceUid ?: "获取失败"
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
                binding.tvDeviceName.text = it
            })
            deviceType.observe(this@DeviceInfoFragment, Observer {
                binding.tvDeviceType.text = "IPC $it"
            })
            deviceInfo.observe(this@DeviceInfoFragment, Observer {
                val total = it[0]
                cameraWrapper!!.deviceInfo?.run {
                    if (total.trim() == "0") {
                        binding.tvSDstatus.text = getString(R.string.noSDcard)
                        hasSdcard = false
                    } else {
                        binding.tvSDstatus.text = getString(R.string.normal)
                        hasSdcard = true
                        sdcardTotal = it[0].toInt()
                        sdcardFree = it[1].toInt()
                    }
                    binding.tvFirmware.text = firmwareVersion
                }
            })
            devMacIP.observe(this@DeviceInfoFragment, Observer {
                binding.tvIp.text = it["IP"]
                binding.tvMac.text = it["MAC"]
            })
            showToast.observe(this@DeviceInfoFragment, Observer {
                changeResultToast(it)
            })
        }
    }

    override fun setListeners() {
        super.setListeners()
        binding.tvDeviceName.setOnLongClickListener {
            val cm = App.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val text =  binding.tvDeviceName.text.toString()
            val myClip = ClipData.newPlainText("text", text)
            cm.setPrimaryClip(myClip)
            ToastUtil.showToast(context, context?.getString(R.string.clipData))
            return@setOnLongClickListener true
        }
        binding.rlFormatSdcard.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.rl_formatSdcard -> {
                if (!cameraWrapper!!.deviceInfo.hasSdcard) {
                    Toasty.error(App.context, App.context.getString(R.string.sdcardNotAvailable)).show()
                    return
                }
                viewModel?.showFormatDialog(mActivity)
            }
        }
    }
}