package org.khjsdk.com.khjsdk_2020.eventbus

data class CameraStatusEvent(var uid:String,var connectStatus:Int,var statusMessage:String)

//connectStatus  0连接成功，1正在连接，2连接失败，3密码错误,4断开连接,5正在接收视频数据