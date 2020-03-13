package org.khj.khjbasiscamerasdk.fragment.setting

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.View
import android.widget.Button
import androidx.lifecycle.Observer
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_deviceinfo.*
import kotlinx.android.synthetic.main.topbar.*
import org.khj.khjbasiscamerasdk.App
import org.khj.khjbasiscamerasdk.R
import org.khj.khjbasiscamerasdk.base.BaseDeviceFragment
import org.khj.khjbasiscamerasdk.utils.ToastUtil
import org.khj.khjbasiscamerasdk.viewmodel.DeviceInfoViewModel

class DeviceInfoFragment : BaseDeviceFragment(), View.OnClickListener {

    private var rightTextButton: Button? = null
    private var viewModel: DeviceInfoViewModel? = null

    override fun contentViewId() = R.layout.fragment_deviceinfo

    override fun initView() {
        super.initView()
        topbar.setTitle(getString(R.string.deviceInfo))
        topbar.addLeftBackImageButton().setOnClickListener { back() }
        tv_deviceName.setBackgroundResource(R.drawable.selector_item_white)
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
            deviceInfo.observe(this@DeviceInfoFragment, Observer {
                val total = it[0]
                cameraWrapper!!.deviceInfo?.run {
                    if (total.trim() == "0") {
                        tv_SDstatus.text = getString(R.string.noSDcard)
                        hasSdcard = false
                    } else {
                        tv_SDstatus.text = getString(R.string.normal)
                        hasSdcard = true
                        sdcardTotal = it[0].toInt()
                        sdcardFree = it[1].toInt()
                    }
                    tv_firmware.text = firmwareVersion
                }
            })
            devMacIP.observe(this@DeviceInfoFragment, Observer {
                tv_ip.text = it["IP"]
                tv_mac.text = it["MAC"]
            })
        }
    }

    override fun setListeners() {
        super.setListeners()
        tv_deviceName.setOnLongClickListener {
            val cm = App.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val text = tv_deviceName.text.toString()
            val myClip = ClipData.newPlainText("text", text)
            cm.setPrimaryClip(myClip)
            ToastUtil.showToast(context, context?.getString(R.string.clipData))
            return@setOnLongClickListener true
        }
        rl_formatSdcard.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.rl_formatSdcard -> {
                if (!cameraWrapper!!.deviceInfo.hasSdcard) {
                    Toasty.error(App.context, App.context.getString(R.string.sdcardNotAvailable))
                        .show()
                    return
                }
                viewModel?.showFormatDialog(mActivity)
            }
        }
    }
}