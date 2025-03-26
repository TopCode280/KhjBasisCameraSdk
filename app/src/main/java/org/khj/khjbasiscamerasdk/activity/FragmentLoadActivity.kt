package org.khj.khjbasiscamerasdk.activity

import android.os.Bundle
import android.view.LayoutInflater
import androidx.viewbinding.ViewBinding
import org.khj.khjbasiscamerasdk.R
import org.khj.khjbasiscamerasdk.base.BaseActivity
import org.khj.khjbasiscamerasdk.base.BaseFragment
import org.khj.khjbasiscamerasdk.databinding.LayoutFragmentBinding
import org.khj.khjbasiscamerasdk.fragment.setting.DevicesSettingFragment
import org.khj.khjbasiscamerasdk.utils.FragmentHelper

class FragmentLoadActivity : BaseActivity<LayoutFragmentBinding>() {

    var uid: String? = ""
    val deviceSetting: DevicesSettingFragment by lazy {
        DevicesSettingFragment()
    }

    override fun inflateBinding(layoutInflater: LayoutInflater): LayoutFragmentBinding {
        return LayoutFragmentBinding.inflate(layoutInflater)
    }

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

    override fun onBackPressed() {
        val currentFragment: BaseFragment<ViewBinding> = fragmentHelper!!.getCurrentFragment()
        if (currentFragment.ifAllowDetach) {
            val count = fragmentHelper!!.pop()
            if (count == 0) {
                finish()
            }
        } else {
            currentFragment.onBackPressed()
        }
    }
}