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
import org.khj.khjbasiscamerasdk.base.BaseActivity
import org.khjsdk.com.khjsdk_2020.mvvm.view.fragment.DevicesFragment

class MainActivity : BaseActivity() {

    var createTime: Long = 0   //主界面启动的时间,启动后的10秒内，不处理网络改变的通知，因为如果4G网络进来，会立马收到断网的通知，导致设备断线重连
    var deivcesFragment: DevicesFragment = DevicesFragment()
    override fun getContentViewLayoutID() =  R.layout.activity_main

    override fun initView(savedInstanceState: Bundle?) {
        initVar()
        addFragment(deivcesFragment, R.id.content)
    }

    fun initVar() {
        createTime = System.currentTimeMillis()
    }
}
