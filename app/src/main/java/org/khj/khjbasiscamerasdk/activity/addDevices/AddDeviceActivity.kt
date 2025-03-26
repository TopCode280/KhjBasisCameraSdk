package org.khj.khjbasiscamerasdk.activity.addDevices

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.vise.log.ViseLog
import es.dmoral.toasty.Toasty
import org.khj.khjbasiscamerasdk.App
import org.khj.khjbasiscamerasdk.R
import org.khj.khjbasiscamerasdk.base.BaseActivity
import org.khj.khjbasiscamerasdk.base.PermissionRetCall
import org.khj.khjbasiscamerasdk.databinding.ActivityAdddevicesBinding
import org.khj.khjbasiscamerasdk.databinding.ActivityWatchervideoBinding
import org.khj.khjbasiscamerasdk.isOpenGPS

class AddDeviceActivity : BaseActivity<ActivityAdddevicesBinding>(), View.OnClickListener {

    override fun inflateBinding(layoutInflater: LayoutInflater): ActivityAdddevicesBinding {
        return ActivityAdddevicesBinding.inflate(layoutInflater)
    }

    override fun initView(savedInstanceState: Bundle?) {
        topBarBinding.topbar.setTitle(R.string.deviceList)
        topBarBinding.topbar.addLeftBackImageButton().setOnClickListener { finish() }
        binding.rlGRcode.setOnClickListener(this)
        binding.rlApMode.setOnClickListener(this)
        binding.rlWlan.setOnClickListener(this)
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
        if (XXPermissions.isGranted(
                this,
                Permission.ACCESS_FINE_LOCATION,
                Permission.ACCESS_COARSE_LOCATION
            )
        ) {
            startActivity(Intent(this, QrCodeConfigNetWorkActivity::class.java))
        } else {
            requestPermissions(
                this, object : PermissionRetCall {
                    override fun onAllow() {
                        startActivity(
                            Intent(
                                this@AddDeviceActivity,
                                QrCodeConfigNetWorkActivity::class.java
                            )
                        )
                    }

                    override fun onRefuse(never: Boolean) {
                        Toasty.error(App.instance, getString(R.string.refuseAuthorize)).show()
                    }
                },
                Permission.ACCESS_FINE_LOCATION, Permission.ACCESS_COARSE_LOCATION
            )
        }
    }

    fun GotoApAddList() {
        if (XXPermissions.isGranted(
                this,
                Permission.ACCESS_FINE_LOCATION,
                Permission.ACCESS_COARSE_LOCATION
            )
        ) {
            allowPermissionsGotoApList()
        } else {
            requestPermissions(
                this, object : PermissionRetCall {
                    override fun onAllow() {
                        allowPermissionsGotoApList()
                    }

                    override fun onRefuse(never: Boolean) {
                        Toasty.error(App.instance, getString(R.string.refuseAuthorize)).show()
                    }
                },
                Permission.ACCESS_FINE_LOCATION, Permission.ACCESS_COARSE_LOCATION
            )
        }
    }

    fun allowPermissionsGotoApList() {
        if (!isOpenGPS(App.context)) {
            ViseLog.i("用户没有开启GPS")
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivityForResult(intent, 0)
        } else {
            startActivity(Intent(this, ApAddDeviceListActivity::class.java))
        }
    }

    fun GotoLanAdd() {
        if (XXPermissions.isGranted(
                this,
                Permission.CAMERA
            )
        ) {
            startActivity(Intent(this, LanAddDeviceActivity::class.java))
        } else {
            requestPermissions(
                this, object : PermissionRetCall {
                    override fun onAllow() {
                        startActivity(
                            Intent(
                                this@AddDeviceActivity,
                                LanAddDeviceActivity::class.java
                            )
                        )
                    }

                    override fun onRefuse(never: Boolean) {
                        Toasty.error(App.instance, getString(R.string.refuseAuthorize)).show()
                    }
                },
                Permission.CAMERA
            )
        }
    }
}