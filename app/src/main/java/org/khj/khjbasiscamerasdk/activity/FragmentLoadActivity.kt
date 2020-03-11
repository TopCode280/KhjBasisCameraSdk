package org.khj.khjbasiscamerasdk.activity

import android.os.Bundle
import org.khj.khjbasiscamerasdk.App
import org.khj.khjbasiscamerasdk.R
import org.khj.khjbasiscamerasdk.base.BaseActivity
import org.khj.khjbasiscamerasdk.fragment.setting.DevicesSettingFragment
import org.khjsdk.com.khjsdk_2020.value.MyConstans

class FragmentLoadActivity : BaseActivity() {

    var uid: String? = ""
    val deviceSetting: DevicesSettingFragment by lazy {
        DevicesSettingFragment()
    }

    override fun getContentViewLayoutID() = R.layout.layout_fragment

    override fun initView(savedInstanceState: Bundle?) {
        val extras = intent.extras
        extras?.apply {
            uid = getString(myConstans.FragmentLoadActivityTagDeviceUid)
            val tagget = getInt(myConstans.FragmentLoadActivityFragmentTag, -1)
            when (tagget) {
                1 -> {
                    addFragment(deviceSetting, R.id.fragment_content)
                }
            }
        }
    }

    fun getDeviceUid(): String = uid!!

}