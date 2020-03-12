package org.khj.khjbasiscamerasdk.activity

import android.os.Bundle
import org.khj.khjbasiscamerasdk.R
import org.khj.khjbasiscamerasdk.base.BaseActivity
import org.khj.khjbasiscamerasdk.fragment.setting.DevicesSettingFragment
import org.khj.khjbasiscamerasdk.utils.FragmentHelper

class FragmentLoadActivity : BaseActivity() {

    var uid: String? = ""
    val deviceSetting: DevicesSettingFragment by lazy {
        DevicesSettingFragment()
    }

    override fun getContentViewLayoutID() = R.layout.layout_fragment

    override fun initView(savedInstanceState: Bundle?) {
        val extras = intent.extras
        fragmentHelper = FragmentHelper(supportFragmentManager, R.id.fragment_content)
        extras?.apply {
            uid = getString(myConstans.FragmentLoadActivityTagDeviceUid)
            val tagget = getInt(myConstans.FragmentLoadActivityFragmentTag, -1)
            fragmentHelper?.run {
                when (tagget) {
                    1 -> {
                        addFragment(deviceSetting)
                        addToStack(deviceSetting)
                    }
                }
            }
        }
    }

    fun getDeviceUid(): String = uid!!

}