package org.khj.khjbasiscamerasdk.activity.addDevices

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.khj.Camera
import com.khj.Camera.searchDeviceInfo
import com.uuzuche.lib_zxing.activity.CaptureActivity
import com.uuzuche.lib_zxing.activity.CodeUtils
import com.vise.log.ViseLog
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.Permission
import es.dmoral.toasty.Toasty
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_lanadddevice.*
import kotlinx.android.synthetic.main.topbar.*
import org.khj.khjbasiscamerasdk.App
import org.khj.khjbasiscamerasdk.R
import org.khj.khjbasiscamerasdk.av_modle.CameraManager
import org.khj.khjbasiscamerasdk.base.BaseActivity
import org.khj.khjbasiscamerasdk.bean.MulticastBean
import org.khj.khjbasiscamerasdk.bean.SearchDeviceInfoBean
import org.khj.khjbasiscamerasdk.database.EntityManager
import org.khj.khjbasiscamerasdk.database.entity.DeviceEntity
import org.khj.khjbasiscamerasdk.greendao.DeviceEntityDao
import org.khj.khjbasiscamerasdk.utils.MulticastServer
import org.khj.khjbasiscamerasdk.view.dialogfragment.SelectUidDialogFragment
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class LanAddDeviceActivity : BaseActivity(), View.OnClickListener,
    SelectUidDialogFragment.UidListSelect, Camera.onOffLineCallback {

    private val REQUEST_CODE = 188
    private var selectUidDialogFragment: SelectUidDialogFragment? = null
    private var multicastServer: MulticastServer? = null
    private var mCamera: Camera? = null
    private var uid: String? = null
    private var disposable: Disposable? = null

    override fun getContentViewLayoutID() = R.layout.activity_lanadddevice

    override fun initView(savedInstanceState: Bundle?) {
        topbar.setTitle(getString(R.string.addLanDevice))
        topbar.addLeftBackImageButton().setOnClickListener { v: View? -> finish() }

        btn_scan.setOnClickListener(this)
        btn_search.setOnClickListener(this)
        btn_addDevice.setOnClickListener(this)

        multicastServer = MulticastServer()
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btn_scan -> {
                AndPermission.with(App.context)
                    .permission(Permission.CAMERA)
                    .onGranted { scan() }
                    .onDenied {
                        Toasty.error(App.context, getString(R.string.denyCameraAuthority)).show()
                    }.start()
            }
            R.id.btn_search -> {
                searchLan()
            }
            R.id.btn_addDevice -> {
                bindDeviceToUser()
            }
        }
    }

    private fun bindDeviceToUser() {
        uid = et_uid.getText().toString()
        if (TextUtils.isEmpty(uid) || uid!!.length < 10) {
            Toasty.error(App.context, getString(R.string.notNullUid)).show()
            return
        }
        showLoading()
        Thread(Runnable {
            val s: String = multicastServer!!.recieveData()
            ViseLog.e("收到组播消息:$s")
            val gson = Gson()
            try {
                val multicastBean = gson.fromJson(s, MulticastBean::class.java)
                saveDeviceInfo(multicastBean)
                ViseLog.e("收到组播消息:" + multicastBean.toString())
            } catch (e: JsonSyntaxException) {
                e.printStackTrace()
            }
            disposable?.dispose()
            finish()
        }).start()
        mCamera = Camera(uid)
        mCamera!!.connect("admin", "888888", 0, this)
    }

    /**
     * 搜索局域网的设备
     */
    @SuppressLint("CheckResult")
    private fun searchLan() {
        ViseLog.i("局域网搜索")
        Observable.create(ObservableOnSubscribe { emitter: ObservableEmitter<Array<searchDeviceInfo>?> ->
            Camera.searchDevice { searchDeviceInfos: Array<searchDeviceInfo>? ->
                if (searchDeviceInfos != null) {
                    ViseLog.i("搜索个数：" + searchDeviceInfos.size)
                    emitter.onNext(searchDeviceInfos)
                    emitter.onComplete()
                } else {
                    ViseLog.i("搜索个数0")
                    emitter.onComplete()
                }
            }
        }).observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { disposable: Disposable? ->
                mDisposable?.add(disposable!!)
                showLoading(disposable)
            }.doFinally {
                dismissLoading()
            }.subscribe {
                showUidSelectDialogFragment(it)
            }
    }

    private fun showUidSelectDialogFragment(searchDeviceInfos: Array<searchDeviceInfo>?) {
        tipDialog?.let {
            if (it.isShowing) {
                it.dismiss()
            }
        }
        if (searchDeviceInfos != null) {
            val uidList: ArrayList<SearchDeviceInfoBean> = ArrayList()
            for (i in searchDeviceInfos.indices) {
                val infoBean = SearchDeviceInfoBean()
                infoBean.uid = searchDeviceInfos[i].UID
                uidList.add(infoBean)
            }
            selectUidDialogFragment = SelectUidDialogFragment()
            selectUidDialogFragment!!.run {
                ViseLog.i("搜索个数：" + searchDeviceInfos.size)
                val bundle = Bundle()
                bundle.putParcelableArrayList("uidList", uidList)
                arguments = bundle
                show(supportFragmentManager, "selectUidDialogFragment")
            }
        } else {
            Toasty.error(App.context, getString(R.string.notAvailableInLan)).show()
        }
    }


    private fun scan() {
        val intent = Intent(this, CaptureActivity::class.java)
        startActivityForResult(intent, REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE) { //处理扫描结果（在界面上显示）
            if (null != data) {
                val bundle = data.extras ?: return
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    val result = bundle.getString(CodeUtils.RESULT_STRING)
                    et_uid.setText(result)
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    Toast.makeText(App.context, R.string.ParseQRFailure, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun clickUid(uid: String) {
        et_uid.setText(uid)
    }

    override fun Offline(p0: Camera?) {

    }

    override fun Online(camera: Camera, p1: Int) {
        ViseLog.e("连接状况 $p1* $uid")   // p1 == 0代表连接成功
        if (p1 == 0) {
            val aDefault = TimeZone.getDefault()
            var rawOffset = aDefault.rawOffset
            rawOffset = rawOffset / (1000 * 60)
            camera.setTimezone(rawOffset) { b: Boolean ->
                // 连接成功后设置设备当前时区,否则可能引起显示时间以及录像问题
                if (b) {
                    camera.setAccountNumber(
                        App.userAccount,
                        "",
                        "",
                        3.toByte().toInt(),
                        "127.0.0.1"
                    ) { c: Boolean ->
                        if (c) {
                            disposable = Observable.timer(10, TimeUnit.SECONDS)
                                .observeOn(AndroidSchedulers.mainThread())
                                .doOnSubscribe { disposable: Disposable? ->
                                    mDisposable!!.add(disposable!!)
                                }.subscribe { aLong: Long? ->
                                    dismissLoading()
                                    finish()
                                }
                            ViseLog.i("账号绑定成功")
                        } else {
                            noticeFailure(-1)
                        }
                    }
                } else {
                    noticeFailure(-1)
                }
            }
        } else {
            ViseLog.e("连接失败 $p1")
            noticeFailure(p1)
        }
    }

    /*
     此处插入数据都为模拟数据
    */
    fun saveDeviceInfo(multicastBean: MulticastBean) {
        val deviceEntityDao = EntityManager.getInstance().getDeviceEntityDao()
        val deviceentity = DeviceEntity()
        deviceentity.deviceUid = multicastBean.uid
        deviceentity.ptz = multicastBean.capability
        deviceentity.userId = App.userAccount.toLong()
        deviceentity.devicePwd = "888888"
        deviceentity.deviceAccount = "admin"
        deviceentity.deviceName = "test_camera_" + CameraManager.getInstance().getCameras().size + 1
        deviceentity.isAdmin = true
        deviceEntityDao!!.insertOrReplace(deviceentity)
        finish()
    }

    fun noticeFailure(i: Int) {
        dismissLoading()
        when (i) {
            -20009 -> {
                Toasty.error(App.context, getString(R.string.addFailureByPwdError)).show();
            }
            else -> {
                Toasty.error(App.context, getString(R.string.failureAndRetry)).show();
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (multicastServer != null) {
            multicastServer!!.release()
        }
    }
}