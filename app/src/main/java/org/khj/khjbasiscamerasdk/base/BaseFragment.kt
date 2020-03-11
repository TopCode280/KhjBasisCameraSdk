package org.khj.khjbasiscamerasdk.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import io.reactivex.disposables.CompositeDisposable
import org.greenrobot.eventbus.EventBus

abstract class BaseFragment : Fragment() {

    private lateinit var provider: ViewModelProvider
    private lateinit var contentView: View
    lateinit var mContext: Context
    protected var mDisposable: CompositeDisposable? = null

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
            initView()
            setListeners()
            initData()
            loadData()
        }
        return contentView
    }

    lateinit var mActivity: BaseActivity
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as BaseActivity
    }

    override fun getView() = contentView
    abstract fun contentViewId(): Int
    abstract fun initView()
    abstract fun setListeners()
    abstract fun initData()
    abstract fun loadData()

    open fun finish() = mActivity.finish()

    protected open fun userEventbus() = false

    override fun onDestroyView() {
        super.onDestroyView()
        if (userEventbus()) {
            EventBus.getDefault().unregister(this)
        }
        mDisposable?.dispose()
    }
}