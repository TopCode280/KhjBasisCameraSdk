package org.khjsdk.com.khjsdk_2020.utils

import org.khjsdk.com.khjsdk_2020.value.AppStatusConstant

class AppStatusManager {
    val appstatusValue : AppStatusConstant =  AppStatusConstant()
    private var appStatus : Int = appstatusValue.STATUS_FORCE_KILLED     //APP状态 初始值为没启动 不在前台状态

    var appStatusManager: AppStatusManager? = null

    private fun AppStatusManager(){

    }

    fun getAppStatus(): Int {
        return appStatus
    }

    fun setAppStatus(appStatus: Int) {
        this.appStatus = appStatus
    }
}