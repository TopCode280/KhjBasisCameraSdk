package org.khj.khjbasiscamerasdk

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import com.khj.Camera
import com.vise.log.ViseLog
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.android.schedulers.AndroidSchedulers

/**
 * 判断GPS是否开启，GPS或者AGPS开启一个就认为是开启的
 *
 * @param context
 * @return true 表示开启
 */
fun isOpenGPS(context: Context): Boolean {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
    val gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
    val network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    return gps || network
}



