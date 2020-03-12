package org.khj.khjbasiscamerasdk.activity.addDevices

import android.annotation.SuppressLint
import android.content.Intent
import android.net.wifi.WifiManager.WIFI_STATE_ENABLED
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.uuzuche.lib_zxing.activity.CodeUtils
import com.vise.log.ViseLog
import kotlinx.android.synthetic.main.activity_qrcodeconfig.*
import kotlinx.android.synthetic.main.topbar.*
import org.greenrobot.eventbus.EventBus
import org.khj.khjbasiscamerasdk.App
import org.khj.khjbasiscamerasdk.R
import org.khj.khjbasiscamerasdk.av_modle.CameraManager
import org.khj.khjbasiscamerasdk.bean.MulticastBean
import org.khj.khjbasiscamerasdk.database.EntityManager
import org.khj.khjbasiscamerasdk.database.entity.DeviceEntity
import org.khj.khjbasiscamerasdk.eventbus.DevicesListRefreshEvent
import org.khj.khjbasiscamerasdk.greendao.DeviceEntityDao
import org.khj.khjbasiscamerasdk.isOpenGPS
import org.khj.khjbasiscamerasdk.utils.MulticastServer
import org.khj.khjbasiscamerasdk.utils.WiFiUtil


class QrCodeConfigNetWorkActivity : AppCompatActivity(), View.OnClickListener {

    var wiFiUtil: WiFiUtil? = null
    private var multicastServer: MulticastServer? = null
    private var multicastBean: MulticastBean? = null

    val handle = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message?) {
            when (msg!!.what) {
                1 -> {
                    btn_deviceConnectSuccess!!.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qrcodeconfig)
        topbar.setTitle(R.string.scanErweima)
        topbar.addLeftBackImageButton().setOnClickListener { finish() }
        tv_selectWifi!!.setOnClickListener(this)
        btn_createQRcode!!.setOnClickListener(this)
        btn_deviceConnectSuccess!!.setOnClickListener(this)
        wiFiUtil = WiFiUtil.getInstance(this)
    }

    override fun onResume() {
        super.onResume()
        if (wiFiUtil!!.checkState() == WIFI_STATE_ENABLED) {
            //获取的SSID带有双引号
            val temp = wiFiUtil!!.getSSID()
            ViseLog.e(temp)
            tv_selectWifi!!.setText(temp.replace("\"", ""))
        }
    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.tv_selectWifi -> {
                if (!isOpenGPS(this)) {
                    ViseLog.i("用户没有开启GPS")
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivityForResult(intent, 0)
                } else {
                    searchWiFi()
                }
            }
            R.id.btn_createQRcode -> {
                createQRcode()
            }
            R.id.btn_deviceConnectSuccess -> {
                saveDeviceInfo()
            }
        }
    }


    private fun searchWiFi() {
        val wifiSettingsIntent = Intent("android.settings.WIFI_SETTINGS")
        startActivity(wifiSettingsIntent)
    }

    /**
     * ssid: the wifi ssid
     * PWD:the wifi password
     */
    private fun createQRcode() {
        val ssid = tv_selectWifi!!.getText().toString().trim()
        val pwd = et_pwd?.getText().toString().trim()
        val wifiType = 1 //wpa/wpa2 encrypt type
        val builder = StringBuilder()
        val wifiString = builder.append("S=").append(ssid).append(",")
                .append("P=").append(pwd).append(",")
                .append("A=").append(App.userAccount).append(",")//任意字符串或者用户app账号
                .append("U=").append("abc").append(",")//填一个任意字符串
                .append("T=").append(wifiType)
                .toString()
        val with = 800
        val mBitmap = CodeUtils.createImage(wifiString, with, with, null)
        iv_QrCode!!.setImageBitmap(mBitmap)
        Thread(Runnable { this.receiveMulticast() }).start()
    }

    private fun receiveMulticast() {
        multicastServer = MulticastServer()
        ViseLog.e("准备接受消息")
        val s1 = multicastServer!!.recieveData()
        val gson = Gson()
        try {
            multicastBean = gson.fromJson(s1, MulticastBean::class.java)
            ViseLog.e("收到组播消息:" + multicastBean.toString())
        } catch (e: JsonSyntaxException) {
            e.printStackTrace()
        }
        ViseLog.e("收到组播消息:$s1")
        handle.sendEmptyMessage(1)
    }

    /*
     此处插入数据都为模拟数据
    */
    fun saveDeviceInfo() {
        val deviceEntityDao = EntityManager.getInstance().getDeviceEntityDao()
        val deviceentity = DeviceEntity()
        deviceentity.deviceUid = multicastBean!!.uid
        deviceentity.ptz = multicastBean!!.capability
        deviceentity.userId = App.userAccount.toLong()
        deviceentity.devicePwd = "888888"
        deviceentity.deviceAccount = "admin"
        deviceentity.deviceName = "test_camera_${CameraManager.getInstance().getCameras().size}"
        deviceentity.isAdmin = true
        deviceEntityDao!!.insertOrReplace(deviceentity)
        EventBus.getDefault().post(DevicesListRefreshEvent()) // 通知设备列表刷新
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (multicastServer != null) {
            multicastServer!!.release()
        }
    }
}