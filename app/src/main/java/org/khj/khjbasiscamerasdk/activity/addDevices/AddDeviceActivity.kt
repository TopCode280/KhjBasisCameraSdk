package org.khj.khjbasiscamerasdk.activity.addDevices

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.vise.log.ViseLog
import com.yanzhenjie.permission.Action
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.Permission
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_adddevices.*
import kotlinx.android.synthetic.main.topbar.*
import org.khj.khjbasiscamerasdk.App
import org.khj.khjbasiscamerasdk.R
import org.khj.khjbasiscamerasdk.isOpenGPS

class AddDeviceActivity : AppCompatActivity(), View.OnClickListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adddevices)
        initView()
    }

    fun initView() {
        topbar.setTitle(R.string.deviceList)
        topbar.addLeftBackImageButton().setOnClickListener { finish() }
        rl_GRcode.setOnClickListener(this)
        rl_ap_mode.setOnClickListener(this)
        rl_wlan.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.rl_GRcode -> {
                GotoQrCode()
            }
            R.id.rl_ap_mode -> {
                GotoApAddList()
            }
            R.id.rl_wlan -> {
                GotoLanAdd()
            }
        }
    }

    fun GotoQrCode() {
        AndPermission.with(this)
            .permission(*Permission.Group.LOCATION)
            .onGranted(Action {
                startActivity(Intent(this, QrCodeConfigNetWorkActivity::class.java))
            }).onDenied(Action {
                Toasty.error(App.instance, getString(R.string.refuseAuthorize)).show()
            }).start()
    }

    fun GotoApAddList() {
        AndPermission.with(this)
            .permission(*Permission.Group.LOCATION)
            .onGranted(Action {
                if (!isOpenGPS(App.context)) {
                    ViseLog.i("用户没有开启GPS")
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivityForResult(intent, 0)
                } else {
                    startActivity(Intent(this, ApAddDeviceListActivity::class.java))
                }
            }).onDenied(Action {
                Toasty.error(App.instance, getString(R.string.refuseAuthorize)).show()
            }).start()
    }

    fun GotoLanAdd() {
        AndPermission.with(this)
            .permission(*Permission.Group.CAMERA)
            .onGranted(Action {
                startActivity(Intent(this, LanAddDeviceActivity::class.java))
            }).onDenied(Action {
                Toasty.error(App.instance, getString(R.string.refuseAuthorize)).show()
            }).start()
    }
}