package org.khjsdk.com.khjsdk_2020.value

data class AppStatusConstant(
        val STATUS_FORCE_KILLED : Int = -1,
        val STATUS_KICK_OUT : Int= 1, //TOKEN失效或者被踢下线
        val STATUS_NORMAL : Int= 2,  //APP正常态
        val KEY_HOME_ACTION : String = "key_home_action", //返回到主页面
        val ACTION_BACK_TO_HOME : Int= 6, //默认值
        val ACTION_RESTART_APP : Int= 9, //被强杀
        val ACTION_KICK_OUT : Int = 10 //被踢出
)