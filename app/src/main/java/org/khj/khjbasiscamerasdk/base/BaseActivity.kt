package org.khj.khjbasiscamerasdk.base

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.qmuiteam.qmui.util.QMUIStatusBarHelper
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog
import com.vise.log.ViseLog
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import org.greenrobot.eventbus.EventBus
import org.khj.khjbasiscamerasdk.App
import org.khj.khjbasiscamerasdk.R
import org.khj.khjbasiscamerasdk.activity.MainActivity
import org.khjsdk.com.khjsdk_2020.value.AppStatusConstant

abstract class BaseActivity : AppCompatActivity() {

    var AppStatusConstant: AppStatusConstant = AppStatusConstant()
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
        if (usetranslucent()) {
            QMUIStatusBarHelper.translucent(this)
        }
        beforeInit()
        if (getContentViewLayoutID() != 0) {
            setContentView(getContentViewLayoutID())
            initView(savedInstanceState)
        }
    }

    /**
     * 界面初始化前期准备
     */
    protected fun beforeInit() {

    }

    abstract fun initView(savedInstanceState: Bundle?)


    protected fun useEventbus(): Boolean {
        return false
    }

    protected fun usetranslucent(): Boolean {
        return true
    }

    protected fun protectApp() {
        ViseLog.e("APP被强杀了")
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(AppStatusConstant.KEY_HOME_ACTION, AppStatusConstant.ACTION_RESTART_APP)
        startActivity(intent)
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