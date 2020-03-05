package org.khj.khjbasiscamerasdk

import android.app.Activity
import android.app.Application
import android.content.Context
import android.util.Log
import com.vise.log.ViseLog
import com.vise.log.inner.DefaultTree
import java.util.*

class App : Application() {

    internal var activityStack: Stack<Activity>? = null

    init {
        initLog()
    }

    override fun onCreate() {
        super.onCreate()
        context = this
        userAccount = "15111520684"
    }

    companion object {
        lateinit var instance: App
        lateinit var context: App
        lateinit var userAccount: String
    }

    fun initLog() {
        ViseLog.getLogConfig()
                .configAllowLog(true)//是否输出日志
                .configShowBorders(true)//是否排版显示
                .configTagPrefix("jerry____khjSdk_2020")//设置标签前缀
                .configFormatTag("%d{HH:mm:ss:SSS} %t %c{-5}")//个性化设置标签，默认显示包名
                .configLevel(Log.VERBOSE) //设置日志最小输出级别，默认Log.VERBOSE
        ViseLog.plant(DefaultTree());//添加打印日志信息到Logcat的树
    }


}