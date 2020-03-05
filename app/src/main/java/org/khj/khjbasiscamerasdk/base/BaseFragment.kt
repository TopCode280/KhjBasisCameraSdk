package org.khj.khjbasiscamerasdk.base

import android.app.Fragment
import android.content.Context
import android.os.Bundle
import org.khj.khjbasiscamerasdk.base.BaseActivity

open class BaseFragment : Fragment() {

    protected var mActivity: BaseActivity? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mActivity  = context as BaseActivity;
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    /**
     * 1. 初始化数据，包括上个页面传递过来的数据在这个方法做
     */
    protected fun initData(savedInstanceState: Bundle?) {

    }

    /**
     * 1. 需要使用eventbug 重写 return true
     */
    protected fun userEventbus(): Boolean {
        return false
    }
}