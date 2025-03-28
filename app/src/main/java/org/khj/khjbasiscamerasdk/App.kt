package org.khj.khjbasiscamerasdk

import android.app.Application
import android.util.Log
import androidx.multidex.MultiDex
import com.khj.glVideoDecodec2
import com.uuzuche.lib_zxing.activity.ZXingLibrary.initDisplayOpinion
import com.vise.log.ViseLog
import com.vise.log.inner.DefaultTree

class App : Application() {

    init {
        initLog()
    }

    companion object {
        lateinit var instance: App
        lateinit var context: App
        lateinit var userAccount: String
        var videoDecode = glVideoDecodec2()

    }

    override fun onCreate() {
        super.onCreate()
        context = this
        userAccount = "15111520684"
        MultiDex.install(context)
        initDisplayOpinion(context) // zxing 二维码扫描界面需要此处初始化
        registerActivityLifecycleCallbacks(LifeCycle)
    }

    fun initLog() {
        ViseLog.getLogConfig()
                .configAllowLog(true)//是否输出日志
                .configShowBorders(true)//是否排版显示
                .configTagPrefix("jerry____khjSdk_2025")//设置标签前缀
                .configFormatTag("%d{HH:mm:ss:SSS} %t %c{-5}")//个性化设置标签，默认显示包名
                .configLevel(Log.VERBOSE) //设置日志最小输出级别，默认Log.VERBOSE
        ViseLog.plant(DefaultTree());//添加打印日志信息到Logcat的树
    }
}