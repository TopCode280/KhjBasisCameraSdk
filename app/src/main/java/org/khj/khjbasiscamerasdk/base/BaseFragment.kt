package org.khj.khjbasiscamerasdk.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import es.dmoral.toasty.Toasty
import io.reactivex.disposables.CompositeDisposable
import org.greenrobot.eventbus.EventBus
import org.khj.khjbasiscamerasdk.App
import org.khj.khjbasiscamerasdk.R
import org.khj.khjbasiscamerasdk.utils.FragmentHelper

abstract class BaseFragment : Fragment() {

    private lateinit var provider: ViewModelProvider
    private lateinit var contentView: View
    lateinit var mContext: Context
    protected var mDisposable: CompositeDisposable? = null
    private var showToastTime: Long = 0
    protected var fragmentHelp:FragmentHelper? = null

    lateinit var mActivity: BaseActivity
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as BaseActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (!::contentView.isInitialized) {
            if (userEventbus()) {
                EventBus.getDefault().register(this)
            }
            mDisposable = CompositeDisposable()
            contentView = inflater.inflate(contentViewId(), null)
            provider = ViewModelProviders.of(this)
            mContext = contentView.context
            fragmentHelp = mActivity.getFragmentHelp()
            initData()
            initView()
            setListeners()
            loadData()
        }
        return contentView
    }

    fun <T : ViewModel> getViewModel(modelClass: Class<T>): T = provider.get(modelClass)
    override fun getView() = contentView
    protected abstract fun contentViewId(): Int
    abstract fun initView()
    abstract fun setListeners()
    abstract fun initData()
    abstract fun loadData()

    open fun finish() = mActivity.finish()

    protected open fun back() = mActivity.onBackPressed()

    protected open fun userEventbus() = false

    fun changeResultToast(success: Boolean) {
        if (System.currentTimeMillis() - showToastTime <= 5 * 1000) {
            showToastTime = System.currentTimeMillis()
            if (success) {
                Toasty.success(App.context, getString(R.string.modifySuccess)).show()
            } else {
                Toasty.error(App.context, getString(R.string.modifyFailure)).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (userEventbus()) {
            EventBus.getDefault().unregister(this)
        }
        mDisposable?.dispose()
    }
}