package org.khj.khjbasiscamerasdk

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import com.khj.Camera
import com.vise.log.ViseLog
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.android.schedulers.AndroidSchedulers
import org.khj.khjbasiscamerasdk.bean.ListVideoFileBean


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
fun Camera.formatSdCardExtension(result: (Int) -> Unit, dismissDialog: () -> Unit) {
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
        }, {
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

@SuppressLint("CheckResult")
fun Camera.getListvideoFileExtension(
    end: Long,
    result: (ArrayList<Camera.fileInfo>?, Boolean?) -> Unit,
    error: (String) -> Unit
) {
    //result 0: 后面还有文件 1:最后一个文件后面没有了 2：文件目录发生变化，请重新开始
    Observable.create { emitter: ObservableEmitter<ListVideoFileBean> ->
        listvideoFile(end) { result, info ->
            if (result < 2) {
                if (info != null) {
                    val cameraFileInfos = mutableListOf(info)
                    emitter.onNext(
                        ListVideoFileBean(
                            cameraFileInfos as ArrayList<Camera.fileInfo>,
                            result == 1
                        )
                    )
                } else {
                    emitter.tryOnError(Throwable("0"))
                }
            } else {
                emitter.tryOnError(Throwable("4"))
            }
        }
    }.subscribeOn(AndroidSchedulers.mainThread())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({
            result(it.info, it.result)
        }, {
            if (it.equals("0")) {
                error(App.context.getString(R.string.noRecorderVideo))
            } else {
                error("文件目录发生变化,请重试!")
            }
        })
}

// 移动侦测开关
@SuppressLint("CheckResult")
fun Camera.getAlarmSwitchExtension(result: (Boolean) -> Unit) {
    Observable.create { emitter: ObservableEmitter<Boolean> ->
        getAlarmSwitch {
            emitter.onNext(it)
        }
    }.observeOn(AndroidSchedulers.mainThread())
        .subscribe({
            result(it)
        }, {
            ViseLog.e("${this.javaClass.name} ---- ${it.cause}")
        })
}

// 移动侦测灵敏值
@SuppressLint("CheckResult")
fun Camera.getMotionDetectExtension(result: (Int) -> Unit) {
    Observable.create { emitter: ObservableEmitter<Int> ->
        getMotionDetect {
            emitter.onNext(it)
        }
    }.observeOn(AndroidSchedulers.mainThread())
        .subscribe({
            result(it)
        }, {
            ViseLog.e("${this.javaClass.name} ---- ${it.cause}")
        })
}

//声音侦测开关
@SuppressLint("CheckResult")
fun Camera.geteSoundAlarmExtension(result: (Boolean) -> Unit) {
    Observable.create { emitter: ObservableEmitter<Boolean> ->
        geteSoundAlarm {
            emitter.onNext(it)
        }
    }.observeOn(AndroidSchedulers.mainThread())
        .subscribe({
            result(it)
        }, {
            ViseLog.e("${this.javaClass.name} ---- ${it.cause}")
        })
}

// 设备报警声音开关
@SuppressLint("CheckResult")
fun Camera.getAlarmVolumeExtension(result: (Boolean) -> Unit) {
    Observable.create { emitter: ObservableEmitter<Boolean> ->
        geteSoundAlarm {
            emitter.onNext(it)
        }
    }.observeOn(AndroidSchedulers.mainThread())
        .subscribe({
            result(it)
        }, {
            ViseLog.e("${this.javaClass.name} ---- ${it.cause}")
        })
}

// 人脸检测开关 Int == 1
@SuppressLint("CheckResult")
fun Camera.getFtgAlarmExtension(result: (Int) -> Unit) {
    Observable.create { emitter: ObservableEmitter<Int> ->
        getFtgAlarm {
            emitter.onNext(it)
        }
    }.observeOn(AndroidSchedulers.mainThread())
        .subscribe({
            result(it)
        }, {
            ViseLog.e("${this.javaClass.name} ---- ${it.cause}")
        })
}

// 人形检测开关  Int == 1
@SuppressLint("CheckResult")
fun Camera.getPdAlarmExtension(result: (Int) -> Unit) {
    Observable.create { emitter: ObservableEmitter<Int> ->
        getPdAlarm {
            emitter.onNext(it)
        }
    }.observeOn(AndroidSchedulers.mainThread())
        .subscribe({
            result(it)
        }, {
            ViseLog.e("${this.javaClass.name} ---- ${it.cause}")
        })
}

//查询报警间隔
@SuppressLint("CheckResult")
fun Camera.sendCommonBufferNineExtension(result: (String) -> Unit) {
    Observable.create { emitter: ObservableEmitter<String> ->
        sendCommonBuffer(9.toByte(), "") { i, b, s ->
            s?.let {
                emitter.onNext(it)
            }
            emitter.onComplete()
        }
    }.observeOn(AndroidSchedulers.mainThread())
        .subscribe({
            result(it)
        }, {
            ViseLog.e("${this.javaClass.name} ---- ${it.cause}")
        })
}

//设置移动侦测灵敏度
@SuppressLint("CheckResult")
fun Camera.setMotionDetectExtension(progress: Int, result: (Boolean) -> Unit) {
    Observable.create { emitter: ObservableEmitter<Boolean> ->
        setMotionDetect(progress) {
            emitter.onNext(it)
            emitter.onComplete()
        }
    }.observeOn(AndroidSchedulers.mainThread())
        .subscribe({
            result(it)
        }, {
            ViseLog.e("${this.javaClass.name} ---- ${it.cause}")
        })
}

// 设备报警声音开关
@SuppressLint("CheckResult")
fun Camera.setAlarmVolumeExtension(isChecked: Boolean, result: (Boolean) -> Unit) {
    Observable.create { emitter: ObservableEmitter<Boolean> ->
        setAlarmVolume(isChecked) {
            emitter.onNext(it)
            emitter.onComplete()
        }
    }.observeOn(AndroidSchedulers.mainThread())
        .subscribe({
            result(it)
        }, {
            ViseLog.e("${this.javaClass.name} ---- ${it.cause}")
        })
}

// 设置报警间隔时间
@SuppressLint("CheckResult")
fun Camera.sendCommonBufferEightExtension(selector: String, result: (String) -> Unit) {
    Observable.create { emitter: ObservableEmitter<String> ->
        sendCommonBuffer(9.toByte(), selector) { i, b, s ->
            s?.let {
                emitter.onNext(it)
            }
            emitter.onComplete()
        }
    }.observeOn(AndroidSchedulers.mainThread())
        .subscribe({
            result(it)
        }, {
            ViseLog.e("${this.javaClass.name} ---- ${it.cause}")
        })
}

/**
 * 查询当前录制视频质量
 */
@SuppressLint("CheckResult")
fun Camera.getRecordVideoQualityExtension(result: (Int) -> Unit) {
    Observable.create { emitter: ObservableEmitter<Int> ->
        getRecordVideoQuality {
            emitter.onNext(it)
            emitter.onComplete()
        }
    }.observeOn(AndroidSchedulers.mainThread())
        .subscribe({
            result(it)
        }, {
            ViseLog.e("${this.javaClass.name} ---- ${it.cause}")
        })
}

/**
 * 查询当前录制视频模式
 */
@SuppressLint("CheckResult")
fun Camera.getVideoRecordTypeExtension(result: (Int) -> Unit) {
    Observable.create { emitter: ObservableEmitter<Int> ->
        getVideoRecordType {
            emitter.onNext(it)
            emitter.onComplete()
        }
    }.observeOn(AndroidSchedulers.mainThread())
        .subscribe({
            result(it)
        }, {
            ViseLog.e("${this.javaClass.name} ---- ${it.cause}")
        })
}

/**
 * 获取定时录像任务
 */
@SuppressLint("CheckResult")
fun Camera.getTimedRecordVideoTaskExtension(result: (String) -> Unit) {
    Observable.create { emitter: ObservableEmitter<String> ->
        getTimedRecordVideoTask {
            emitter.onNext(it)
            emitter.onComplete()
        }
    }.observeOn(AndroidSchedulers.mainThread())
        .subscribe({
            result(it)
        }, {
            ViseLog.e("${this.javaClass.name} ---- ${it.cause}")
        })
}

/**
 * 设置定时录像类型
 */
@SuppressLint("CheckResult")
fun Camera.setVideoRecordTypeExtension(
    loaddingStatus: MutableLiveData<Boolean>,
    type: Int,
    result: (Boolean) -> Unit
) {
    Observable.create { emitter: ObservableEmitter<Boolean> ->
        setVideoRecordType(type) {
            emitter.onNext(it)
            emitter.onComplete()
        }
    }.observeOn(AndroidSchedulers.mainThread())
        .doOnSubscribe {
            loaddingStatus.postValue(true)
        }.doFinally {
            loaddingStatus.postValue(false)
        }.subscribe({
            result(it)
        }, {
            ViseLog.e("${this.javaClass.name} ---- ${it.cause}")
        })
}

/**
 * 设置定时录像类型
 */
@SuppressLint("CheckResult")
fun Camera.setRecordVideoQualityExtension(
    loaddingStatus: MutableLiveData<Boolean>,
    type: Int,
    result: (Boolean) -> Unit
) {

    Observable.create { emitter: ObservableEmitter<Boolean> ->
        setRecordVideoQuality(type + 1) {
            emitter.onNext(it)
            emitter.onComplete()
        }
    }.observeOn(AndroidSchedulers.mainThread())
        .doOnSubscribe {
            loaddingStatus.postValue(true)
        }.doFinally {
            loaddingStatus.postValue(false)
        }.subscribe({
            result(it)
        }, {
            ViseLog.e("${this.javaClass.name} ---- ${it.cause}")
        })
}