package org.khj.khjbasiscamerasdk.fragment.setting

import kotlinx.android.synthetic.main.topbar.*
import org.khj.khjbasiscamerasdk.R
import org.khj.khjbasiscamerasdk.base.BaseFragment

class DevicesSettingFragment : BaseFragment() {

    override fun contentViewId() = R.layout.fragment_devicesetting

    override fun initView() {
        topbar.setTitle(R.string.deviceSetting)
        topbar.addLeftBackImageButton().setOnClickListener { finish() }
    }

    override fun setListeners() {
    }

    override fun initData() {
    }

    override fun loadData() {
    }
}