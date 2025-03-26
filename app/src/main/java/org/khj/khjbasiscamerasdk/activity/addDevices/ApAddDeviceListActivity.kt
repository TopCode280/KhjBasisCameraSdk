package org.khj.khjbasiscamerasdk.activity.addDevices

import android.annotation.SuppressLint
import android.content.Intent
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.vise.log.ViseLog
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.khj.khjbasiscamerasdk.App
import org.khj.khjbasiscamerasdk.R
import org.khj.khjbasiscamerasdk.activity.video.WatchVideoActivity
import org.khj.khjbasiscamerasdk.adapter.ApDeviceAdapter
import org.khj.khjbasiscamerasdk.av_modle.CameraManager
import org.khj.khjbasiscamerasdk.base.BaseActivity
import org.khj.khjbasiscamerasdk.databinding.ActivityApadddevicelistBinding
import org.khj.khjbasiscamerasdk.databinding.ActivityWatchervideoBinding
import org.khj.khjbasiscamerasdk.utils.WiFiUtil
import org.khj.khjbasiscamerasdk.view.SimpleMiddleDividerItemDecoration
import java.util.concurrent.TimeUnit

class ApAddDeviceListActivity : BaseActivity<ActivityApadddevicelistBinding>() {

    private var wiFiUtil: WiFiUtil? = null
    private var apDeviceAdapter: ApDeviceAdapter? = null
    private var searchLanSub: Disposable? = null
    private var apList: List<ScanResult>? = null
    private var uid: String? = null
    private val ssid: String? = null
    private var delayTime: Int? = 0

    override fun inflateBinding(layoutInflater: LayoutInflater): ActivityApadddevicelistBinding {
        return ActivityApadddevicelistBinding.inflate(layoutInflater)
    }

    override fun initView(savedInstanceState: Bundle?) {
        topBarBinding.topbar.setTitle(R.string.ApMode)
        topBarBinding.topbar.addLeftBackImageButton().setOnClickListener { v: View? -> finish() }
        try {
            wiFiUtil = WiFiUtil.getInstance(App.context)
            wiFiUtil?.run {
                startScan()
            }
        } catch (e: Exception) {
            ViseLog.e("wifi出错了" + e.message)
            Toast.makeText(App.context, "wifi出错了", Toast.LENGTH_SHORT).show()
            return
        }
        apDeviceAdapter = ApDeviceAdapter()
        queryCameras()
        initRecyclerView()
    }

    private fun queryCameras() {
        searchLanSub = Observable.interval(0, 5, TimeUnit.SECONDS)
            .map { aLong: Long? ->
                try {
                    wiFiUtil!!.startScan()
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
                aLong
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { aLong: Long? ->
                apList = wiFiUtil!!.apList
                apDeviceAdapter!!.setNewData(apList)
            }
        mDisposable.add(searchLanSub!!)
    }

    fun initRecyclerView() {
        binding.rvAp.run {
            setLayoutManager(LinearLayoutManager(mContext))
            addItemDecoration(SimpleMiddleDividerItemDecoration(mContext))
            setAdapter(apDeviceAdapter)
        }
        apDeviceAdapter?.setOnItemClickListener { adapter, view, position ->
            run {
                val apSsid = apList!!.get(position).SSID
                if (apSsid != null && apSsid.startsWith("camera_")) {
                    uid = apSsid.substring(10)
                    ViseLog.e(uid)
                }
                if (searchLanSub != null) {
                    searchLanSub!!.dispose()
                }
                connectDeviceAp(apSsid)
            }
        }
    }

    @SuppressLint("CheckResult")
    private fun connectDeviceAp(device_ssid: String?) {
        if (wiFiUtil!!.checkState() != WifiManager.WIFI_STATE_ENABLED) {
            wiFiUtil!!.openWifi()
        }
        wiFiUtil?.let {
            delayTime = if (device_ssid == it.realSSID){
                0
            }else{
                7
            }
        }
        Observable.create(ObservableOnSubscribe { emitter: ObservableEmitter<Boolean?> ->
            val change: Boolean
            if (device_ssid != null) {
                CameraManager.getInstance().disconnectAll()
                change = wiFiUtil!!.changeToWifi(device_ssid, "12345678", WiFiUtil.Data.WIFI_CIPHER_WPA2)
                ViseLog.e("Ap", "代码链接 $device_ssid")
                emitter.onNext(change)
            } else {
                emitter.onNext(false)
            }
            emitter.onComplete()
        }).subscribeOn(Schedulers.io())
            .delay(delayTime!!.toLong(), TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { disposable: Disposable? ->
                mDisposable?.add(disposable!!)
                showLoading(disposable)
            }
            .doFinally { dismissLoading() }
            .subscribe { aBoolean: Boolean? ->
                if (isFinishing) {
                    return@subscribe
                }
                CameraManager.getInstance().addApCamera(uid)
                val intent = Intent(this, WatchVideoActivity::class.java)
                intent.putExtra("uid", uid)
                intent.putExtra("ap", true)
                startActivity(intent)
                finish()
            }
    }

}