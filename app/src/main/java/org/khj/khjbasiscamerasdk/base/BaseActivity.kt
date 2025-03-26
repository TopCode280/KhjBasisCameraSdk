package org.khj.khjbasiscamerasdk.base

import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.viewbinding.ViewBinding
import com.githang.statusbar.StatusBarCompat
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.XXPermissions
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog
import com.vise.log.ViseLog
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import org.greenrobot.eventbus.EventBus
import org.khj.khjbasiscamerasdk.App
import org.khj.khjbasiscamerasdk.R
import org.khj.khjbasiscamerasdk.databinding.ActivityMainBinding
import org.khj.khjbasiscamerasdk.databinding.TopbarBinding
import org.khj.khjbasiscamerasdk.utils.FragmentHelper
import org.khjsdk.com.khjsdk_2020.value.MyConstans

abstract class BaseActivity<B : ViewBinding> : AppCompatActivity() {

    protected lateinit var binding: B
    protected lateinit var topBarBinding: TopbarBinding // 假设topbar.xml生成的Binding类是TopbarBinding


    val myConstans: MyConstans by lazy {
        MyConstans()
    }

    abstract fun inflateBinding(layoutInflater: LayoutInflater): B


    protected val mDisposable: CompositeDisposable by lazy {
        CompositeDisposable()
    }
    protected var fragmentHelper: FragmentHelper? = null
    var mContext: Context = this
    protected var tipDialog: QMUITipDialog? = null

    open fun getFragmentHelp(): FragmentHelper? {
        return fragmentHelper
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        removeFragmentState(savedInstanceState)
        super.onCreate(savedInstanceState)
        binding = inflateBinding(layoutInflater)
        setContentView(binding.root)
        if (useEventbus()) {
            EventBus.getDefault().register(this)
        }
        val view = binding.root.findViewById<View>(R.id.topbar)
        if (view != null) {
            topBarBinding = TopbarBinding.bind(binding.root.findViewById(R.id.topbar))
        }
        initView(savedInstanceState)
    }

    fun setStatusBar(lightStatusBar: Boolean) {
        val decorView = window.decorView
        val option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        decorView.systemUiVisibility = option
        window.statusBarColor = Color.TRANSPARENT
        StatusBarCompat.setStatusBarColor(this, Color.TRANSPARENT, lightStatusBar)
    }

    abstract fun initView(savedInstanceState: Bundle?)


    protected fun useEventbus(): Boolean {
        return false
    }

    protected fun removeFragmentState(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            savedInstanceState.remove("android:support:fragments")   //            FRAGMENTS_TAG
            savedInstanceState.remove("android:fragments")
        }
    }

    open fun showLoading() {
        tipDialog = QMUITipDialog(mContext)
        tipDialog!!.setCanceledOnTouchOutside(false)
        val inflate = View.inflate(App.context, R.layout.dialog_loading, null)
        tipDialog!!.setContentView(
            inflate,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        tipDialog!!.setCancelable(true)
        tipDialog!!.setOnCancelListener { dialog: DialogInterface? -> mDisposable!!.dispose() }
        tipDialog!!.show()
    }

    open fun showLoading(disposable: Disposable?) {
        tipDialog = QMUITipDialog(mContext)
        tipDialog?.run {
            setCanceledOnTouchOutside(false)
            setContentView(R.layout.dialog_loading)
            setCancelable(true)
            setOnCancelListener {
                disposable?.dispose()
            }
            show()
        }
    }

    open fun dismissLoading() {
        tipDialog?.let {
            if (it.isShowing) {
                it.dismiss()
                tipDialog = null
            }
        }
    }

    inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> FragmentTransaction) {
        beginTransaction().func().commit()
    }

    fun AppCompatActivity.addFragment(fragment: Fragment, frameId: Int) {
        supportFragmentManager.inTransaction { add(frameId, fragment) }
    }

    fun AppCompatActivity.replaceFragment(fragment: Fragment, frameId: Int) {
        supportFragmentManager.inTransaction { replace(frameId, fragment) }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (useEventbus()) {
            EventBus.getDefault().unregister(this)
        }
        if (mDisposable != null) {
            mDisposable!!.dispose()
            mDisposable!!.clear()
        }
        dismissLoading()
    }

    fun requestPermissions(
        context: Context?,
        permissionRetCall: PermissionRetCall?,
        vararg permissions: String
    ) {
        if (context == null || permissionRetCall == null) {
            return
        }
        XXPermissions.with(context)
            .permission(*permissions)
            .request(object : OnPermissionCallback {
                override fun onGranted(list: List<String>, all: Boolean) {
                    if (all) {
                        permissionRetCall.onAllow()
                    }
                }

                override fun onDenied(permissions: List<String>, never: Boolean) {
                    dismissLoading()
                    permissionRetCall.onRefuse(never)
                }
            })
    }
}