package org.khj.khjbasiscamerasdk

import android.annotation.SuppressLint
import com.khj.Camera
import com.khj.Camera.successCallbackI
import com.vise.log.ViseLog
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.android.schedulers.AndroidSchedulers


/**
 * camera 回调都是在子线程中所以需要用rx切回main线程到外部提交获取的数据
 * @param result 是个闭包函数方便外部 {} 直接获取结果
 * */

@SuppressLint("CheckResult")
fun Camera.getFlipStatus(result: (Boolean) -> Unit) {
    Observable.create { emitter: ObservableEmitter<Boolean> ->
        getFlipping {
            emitter.onNext(it)
            emitter.onComplete()
        }
    }.observeOn(AndroidSchedulers.mainThread())
        .subscribe({
            result(it)
        }, {
            ViseLog.e(it.cause)
        })
}

@SuppressLint("CheckResult")
fun Camera.getDevVolume(result: (Int) -> Unit) {
    Observable.create { emitter: ObservableEmitter<Int> ->
        getDeviceVolume {
            emitter.onNext(it)
            emitter.onComplete()
        }
    }.observeOn(AndroidSchedulers.mainThread())
        .subscribe({
            result(it)
        }, {
            ViseLog.e(it.cause)
        })
}

@SuppressLint("CheckResult")
fun Camera.queryDevInfo(result: (ArrayList<String>) -> Unit) {
    Observable.create { emitter: ObservableEmitter<ArrayList<String>> ->
        queryDeviceInfo { sdcardTotal, sdcardFree, v1, v2, v3, v4, model, vender, fileSystem ->
            val list = ArrayList<String>()
            list.add("$sdcardTotal")
            list.add("$sdcardFree")
            list.add(StringBuilder().append(v2).append(".").append(v3).append(".").append(v4).toString()) // 版本号
            emitter.onNext(list)
        }
    }.observeOn(AndroidSchedulers.mainThread())
        .subscribe({
            result(it)
        }, {
            ViseLog.e(it.cause)
        })
}

@SuppressLint("CheckResult")
fun Camera.getDevMacIP(result: (HashMap<String, String>) -> Unit) {
    Observable.create { emitter: ObservableEmitter<HashMap<String, String>> ->
        getMacIp { i, MAC, IP ->
            val hashMap = HashMap<String, String>()
            hashMap["IP"] = IP
            hashMap["MAC"] = MAC
            emitter.onNext(hashMap)
        }
    }.observeOn(AndroidSchedulers.mainThread())
        .subscribe({
            result(it)
        }, {
            ViseLog.e(it.cause)
        })
}

@SuppressLint("CheckResult")
fun Camera.formatSdCardExtension(result: (Int) -> Unit,dismissDialog: () -> Unit) {
    Observable.create { emitter: ObservableEmitter<Int> ->
        formatSdcard {
            emitter.onNext(it)
        }
    }.observeOn(AndroidSchedulers.mainThread())
        .doFinally { dismissDialog() }
        .subscribe({
            result(it)
        }, {
            ViseLog.e(it.cause)
            dismissDialog()
        },{
            dismissDialog()
        })
}

@SuppressLint("CheckResult")
fun Camera.getTimeZoneExtension(result: (Int) -> Unit) {
    Observable.create { emitter: ObservableEmitter<Int> ->
        getTimezone {
            emitter.onNext(it)
        }
    }.observeOn(AndroidSchedulers.mainThread())
        .subscribe({
            result(it)
        }, {
            ViseLog.e(it.cause)
        })
}