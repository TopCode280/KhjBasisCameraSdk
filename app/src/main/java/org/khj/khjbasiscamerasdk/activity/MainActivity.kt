package org.khj.khjbasiscamerasdk.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.Permission
import org.khj.khjbasiscamerasdk.App
import org.khj.khjbasiscamerasdk.R
import org.khjsdk.com.khjsdk_2020.mvvm.view.fragment.DevicesFragment

class MainActivity : AppCompatActivity() {

    var createTime: Long = 0   //主界面启动的时间,启动后的10秒内，不处理网络改变的通知，因为如果4G网络进来，会立马收到断网的通知，导致设备断线重连
    var deivcesFragment: DevicesFragment? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        deivcesFragment = DevicesFragment()
        initVar()
        initView()
    }

    fun initVar() {
        createTime = System.currentTimeMillis()
    }

    fun initView() {
        addFragment(deivcesFragment!!, R.id.content)
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

    private fun requestPerm() {
        AndPermission.with(this)
                .permission(
                        Permission.Group.MICROPHONE,
                        Permission.Group.STORAGE,
                        Permission.Group.CAMERA,
                        Permission.Group.LOCATION
                ).onGranted { permissions ->

                }.onDenied { permissions -> Toast.makeText(App.context, R.string.denyPermissionLedTo, Toast.LENGTH_SHORT).show() }
                .start()
    }
}
