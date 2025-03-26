package org.khj.khjbasiscamerasdk.base

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.viewbinding.ViewBinding
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog
import es.dmoral.toasty.Toasty
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import org.greenrobot.eventbus.EventBus
import org.khj.khjbasiscamerasdk.App
import org.khj.khjbasiscamerasdk.R
import org.khj.khjbasiscamerasdk.databinding.TopbarBinding
import org.khj.khjbasiscamerasdk.utils.FragmentHelper

abstract class BaseFragment<VB : ViewBinding> : Fragment() {

    private lateinit var provider: ViewModelProvider
    private var mContext: Context? = null
    protected var mDisposable: CompositeDisposable? = null
    private var showToastTime: Long = 0
    protected var fragmentHelp: FragmentHelper? = null
    protected var tipDialog: QMUITipDialog? = null
    lateinit var mActivity: BaseActivity<ViewBinding>

    // 用于缓存 binding，在 onDestroyView 中置空
    private var _binding: VB? = null
    protected val binding get() = _binding!!
    protected lateinit var topBarBinding: TopbarBinding
    var ifAllowDetach = true

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context;
        mActivity = context as BaseActivity<ViewBinding>
    }

    protected abstract fun initBinding(inflater: LayoutInflater, container: ViewGroup?): VB

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (userEventbus()) {
            EventBus.getDefault().register(this)
        }
        mDisposable = CompositeDisposable()
        _binding = initBinding(inflater, container)
        val view = binding.root.findViewById<View>(R.id.topbar)
        if (view != null) {
            topBarBinding = TopbarBinding.bind(binding.root.findViewById(R.id.topbar))
        }
        provider = ViewModelProviders.of(this)
        fragmentHelp = mActivity.getFragmentHelp()
        initData()
        initView()
        setListeners()
        loadData()
        return binding.root
    }

    fun <T : ViewModel> getViewModel(modelClass: Class<T>): T = provider.get(modelClass)
    abstract fun initView()
    abstract fun setListeners()
    abstract fun initData()
    abstract fun loadData()

    open fun finish() = mActivity.finish()

    protected open fun back() = mActivity.onBackPressed()

    protected open fun userEventbus() = false

    override fun onDetach() {
        mContext = null
        super.onDetach()
    }

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
        _binding = null
        super.onDestroyView()
        if (userEventbus()) {
            EventBus.getDefault().unregister(this)
        }
        mDisposable?.dispose()
    }

    open fun showLoading(disposable: Disposable?) {
        if (isAdded) {
            tipDialog = QMUITipDialog(mActivity)
            tipDialog?.apply {
                setCanceledOnTouchOutside(false)
                setContentView(R.layout.dialog_loading)
                setCancelable(true)
                setOnCancelListener { dialog: DialogInterface? ->
                    disposable?.dispose()
                }
                show()
            }
        }
    }

    open fun showLoading() {
        if (isAdded) {
            tipDialog = QMUITipDialog(mActivity)
            tipDialog?.apply {
                setCanceledOnTouchOutside(false)
                setContentView(R.layout.dialog_loading)
                setCancelable(true)
                setOnCancelListener { dialog: DialogInterface? ->

                }
                show()
            }
        }
    }

    fun onBackPressed() {}


    open fun dismissLoading() {
        if (isAdded) {
            if (tipDialog != null && tipDialog!!.isShowing) {
                tipDialog!!.dismiss()
                tipDialog = null
            }
        }
    }
}