package org.khj.khjbasiscamerasdk.base

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.githang.statusbar.StatusBarCompat
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import org.greenrobot.eventbus.EventBus
import org.khj.khjbasiscamerasdk.App
import org.khj.khjbasiscamerasdk.R
import org.khjsdk.com.khjsdk_2020.value.MyConstans

abstract class BaseActivity : AppCompatActivity() {

    val myConstans: MyConstans by lazy{
        MyConstans()
    }

    var mContext: Context = this
    protected var mDisposable: CompositeDisposable? = null
    protected var tipDialog: QMUITipDialog? = null

    protected abstract fun getContentViewLayoutID(): Int

    override fun onCreate(savedInstanceState: Bundle?) {
        removeFragmentState(savedInstanceState)
        super.onCreate(savedInstanceState)
        mDisposable = CompositeDisposable()
        if (useEventbus()) {
            EventBus.getDefault().register(this)
        }
        if (getContentViewLayoutID() != 0) {
            setContentView(getContentViewLayoutID())
            initView(savedInstanceState)
        }
    }

    fun setStatusBar(lightStatusBar: Boolean)
    {
        if (Build.VERSION.SDK_INT >= 21)
        {
            val decorView = window.decorView
            val option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            decorView.systemUiVisibility = option
            window.statusBarColor = Color.TRANSPARENT
            StatusBarCompat.setStatusBarColor(this, Color.TRANSPARENT, lightStatusBar)
        }
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

    protected open fun showLoading() {
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
}