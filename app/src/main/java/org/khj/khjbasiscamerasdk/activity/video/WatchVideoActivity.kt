package org.khj.khjbasiscamerasdk.activity.video

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.SurfaceTexture
import android.media.AudioManager
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.Surface
import android.view.TextureView
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.khj.Camera
import com.khj.Camera.*
import com.khj.Muxing
import com.qmuiteam.qmui.util.QMUIViewHelper
import com.vise.log.ViseLog
import es.dmoral.toasty.Toasty
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.khj.khjbasiscamerasdk.App
import org.khj.khjbasiscamerasdk.App.Companion.context
import org.khj.khjbasiscamerasdk.R
import org.khj.khjbasiscamerasdk.activity.FragmentLoadActivity
import org.khj.khjbasiscamerasdk.adapter.PlaybackVideoAdapter
import org.khj.khjbasiscamerasdk.av_modle.CameraManager
import org.khj.khjbasiscamerasdk.av_modle.CameraWrapper
import org.khj.khjbasiscamerasdk.base.BaseActivity
import org.khj.khjbasiscamerasdk.base.PermissionRetCall
import org.khj.khjbasiscamerasdk.database.entity.DeviceEntity
import org.khj.khjbasiscamerasdk.databinding.ActivityWatchervideoBinding
import org.khj.khjbasiscamerasdk.newP2PUtil.CameraSendCommandBufferCall
import org.khj.khjbasiscamerasdk.newP2PUtil.GetBufferJsonUtil
import org.khj.khjbasiscamerasdk.utils.*
import org.khj.khjbasiscamerasdk.view.SimpleMiddleDividerItemDecoration
import org.khjsdk.com.khjsdk_2020.value.MyConstans
import java.io.File
import java.lang.ref.WeakReference
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

class WatchVideoActivity : BaseActivity<ActivityWatchervideoBinding>(), successCallback,
    onOffLineCallback,
    AdapterView.OnItemSelectedListener ,CameraSendCommandBufferCall{

    private var muxing: Muxing? = null
    private var deviceUid: String? = null
    private var isApMode: Boolean? = null
    private var cameraWrapper: CameraWrapper? = null
    private var decoderUtil: AACDecoderUtil? = null
    private var encoderUtil: AACEncoderUtil? = null
    private var deviceEntity: DeviceEntity? = null
    private var deviceInfoId: String? = null
    private var deviceName: String? = null
    private var lastStartTime: Long = 0
    private var mWeakHandler: WeakHandler? = null
    private var delayReconnectSubscrition: Disposable? = null
    private var mSurface: Surface? = null
    private val totalVideo = AtomicInteger(0)//收到的视频总数据
    private val isRecordingAudio = AtomicBoolean(false)
    private val isRecordingMP4 = AtomicBoolean(false)
    private val isRunning = AtomicBoolean(false)
    private val connectSuccess = AtomicBoolean(false)
    private var initDifference: Long = 0//第一次收到视频I帧时，pts和本地时间的差值，毫秒，如果以后的i帧pts与本地时间的差值过大，就清空缓存
    private var ignore: Boolean = false//不触发状态改变监听标记
    private var deviceFolder: File? = null
    private var constans = MyConstans()
    private var delayStopSendAudioSub: Disposable? = null
    private var isSendAudioOn: Boolean = false
    private var isReceiveAudio = false
    private var dpis = context.resources.getStringArray(R.array.videoRecordQuality)
    private var currentDpi = -1
    private var mp4Path: String? = null
    private var isPlayBackVideo: Boolean = false
    private var currentTimeMillis = System.currentTimeMillis()
    private var startToDayMillis = DateUtils.getTodayStart(System.currentTimeMillis())
    private val isRecordingMP4WithAudio = AtomicBoolean(false)
    private var playbackAdapter: PlaybackVideoAdapter? = null
    private var playBackSubcrition: Disposable? = null
    private var fileTimeInfoArrayList: List<fileTimeInfo> = ArrayList()
    private var temFrameCount = 0
    private var playSDSub: Disposable? = null
    private var delayToPlaySdSub: Disposable? = null

    private class WeakHandler(activity: WatchVideoActivity) : Handler() {
        private val mWeakReference: WeakReference<Activity>

        init {
            this.mWeakReference = WeakReference(activity)
        }

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            val activity = mWeakReference.get() as WatchVideoActivity?
            ViseLog.d("msg.what:"+msg.what)
            if (activity?.cameraWrapper != null) {
                when (msg.what) {
                    0 -> {
                        activity.startVideo()
                    }
                    -90->{
                        Toasty.error(activity, "设备已离线").show()
                    }
                    -20009->{
                        Toasty.error(activity, "设备密码错误").show()
                    }
                }
            }

        }
    }

    override fun inflateBinding(layoutInflater: LayoutInflater): ActivityWatchervideoBinding {
        return ActivityWatchervideoBinding.inflate(layoutInflater)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initView(savedInstanceState: Bundle?) {
        requestPerm()
        mWeakHandler = WeakHandler(this)
        App.videoDecode.reInit()
        App.videoDecode.hw = false // false 软解码 true 硬解码
        initVar()
        initVideo()
        topBarBinding.topbar.setTitle(deviceUid)
        topBarBinding.topbar.addLeftBackImageButton()?.setOnClickListener {
            finish()
        }
        topBarBinding.topbar.addRightImageButton(R.drawable.shezhi, QMUIViewHelper.generateViewId())
            .setOnClickListener {
                val intent = Intent(this@WatchVideoActivity, FragmentLoadActivity::class.java)
                intent.putExtra(myConstans.FragmentLoadActivityTagDeviceUid, deviceUid)
                intent.putExtra(myConstans.FragmentLoadActivityFragmentTag, 1)
                startActivity(intent)
            }
        binding.btnDirectionUp.setOnClickListener {
            // 方向盘 上
            turnCamera(AVIOCTRL_PTZ_UP)
        }
        binding.btnDirectionLeft.setOnClickListener {
            // 方向盘 左
            turnCamera(AVIOCTRL_PTZ_LEFT)
        }
        binding.btnDirectionRight.setOnClickListener {
            // 方向盘 右
            turnCamera(AVIOCTRL_PTZ_RIGHT)
        }
        binding.btnDirectionDown.setOnClickListener {
            // 方向盘 下
            turnCamera(AVIOCTRL_PTZ_DOWN)
        }
        binding.cbxChangelight.setOnCheckedChangeListener { buttonView, isChecked ->
            // 白光灯
            if (!ignore) {
                cameraWrapper?.ChangeWitchLightStatus(isChecked)
            }
        }
        binding.btnScreenshot.setOnClickListener {
            // 截图
            taskPhoto()
        }
        binding.btnSendvoice.setOnTouchListener { view, motionEvent ->
            //发送音频
            try {
                return@setOnTouchListener sendAudio(motionEvent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return@setOnTouchListener true
        }
        binding.tvRecordVideo.setOnClickListener {
            // 录制视频
            if (isRecordingMP4.get()) {
                closeRecordFile()
                binding.tvRecordVideo.text = getString(R.string.startRecordingVideo)
            } else {
                if (connectSuccess.get()) {
                    isRecordingMP4.set(true)
                    binding.tvRecordVideo.text = getString(R.string.closeRecordingVideo)
                    mp4Path =
                        deviceFolder!!.getAbsolutePath() + "/" + TimeUtil.getCurrDateTime() + App.userAccount + ".mov"
                    if (binding.cbxReceiveAudio.isChecked() && cameraWrapper!!.getmCamera().isRecvAudioOn) {
                        isRecordingMP4WithAudio.set(true)
                    } else {
                        isRecordingMP4WithAudio.set(false)
                    }
                    muxing!!.open(mp4Path, isRecordingMP4WithAudio.get())
                    Observable.timer(3, TimeUnit.SECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe { disposable: Disposable? ->
                            binding.tvRecordVideo.setEnabled(false)
                            mDisposable.add(disposable!!)
                        }.subscribe { aLong: Long? ->
                            binding.tvRecordVideo.setEnabled(true)
                        }
                } else {
                    ToastUtil.showToast(context, getString(R.string.cannotRecordBefore))
                }
            }
        }
        binding.cbxReceiveAudio.setOnCheckedChangeListener { buttonView, isChecked ->
            isReceiveAudio = isChecked
            receviceAudio(isChecked)
        }
        cameraWrapper?.let {
            if (it.getDevCap(CameraWrapper.Capability.WHITE_LIGHT)) {
                binding.cbxChangelight.visibility = View.VISIBLE
            }
        }
        binding.tvPlaybackVideo.setOnClickListener {
            if (isPlayBackVideo) {
                binding.rvBasicFunction.visibility = View.VISIBLE
                binding.rvPlaybackVideo.visibility = View.GONE
                binding.tvPlaybackVideo.text = getString(R.string.playBackVideo)
                cameraWrapper!!.playBackVideoStop()
                delayToPlaySdSub?.dispose()
                binding.videoLoading.show()
                startVideo()
            } else {
                binding.rvBasicFunction.visibility = View.GONE
                binding.rvPlaybackVideo.visibility = View.VISIBLE
                binding.tvPlaybackVideo.text = getString(R.string.closePlayBack)
                queryRecordVideo()
            }
            isPlayBackVideo = !isPlayBackVideo
        }

        val adapter = ArrayAdapter(this, R.layout.simple_list_item, dpis)
        adapter.setDropDownViewResource(R.layout.simple_list_item)
        binding.spDpi.adapter = adapter
        binding.spDpi.setOnItemSelectedListener(this) // 切换视频清晰度

        playbackAdapter = PlaybackVideoAdapter()
        playbackAdapter!!.setOnItemClickListener { adapter, view, position ->
            ViseLog.i("去播放视频")
            binding.videoLoading.show()
            cameraWrapper!!.playBackVideoStop()
            playBackSubcrition = Observable.timer(800, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { aLong: Long? ->
                    ViseLog.e("开始回放" + DateUtils.getDateTime(fileTimeInfoArrayList.get(position).videofiletime))
                    playBack(fileTimeInfoArrayList.get(position), position)
                }
        }
        binding.recyclerviewPlaybackVideo.run {
            setLayoutManager(LinearLayoutManager(context))
            addItemDecoration(SimpleMiddleDividerItemDecoration(context))
            setAdapter(playbackAdapter)
        }
    }

    override fun onStart() {
        super.onStart()
        if (mSurface != null) {
            App.videoDecode.videoDecodecCreateSurface(mSurface)
            App.videoDecode.videoDecodecChangeSurface()
            startVideo()
            ViseLog.w("onStart" + "startVideo")
        }
    }

    fun initVar() {
        deviceUid = this.intent.extras?.get("uid").toString()
        isApMode = this.intent.extras?.getBoolean("ap", false)
        deviceFolder = File(constans.KanHuJiaPath + App.userAccount)
        deviceFolder!!.run {
            if (!exists()) {
                mkdirs()
            }
        }
    }

    fun initVideo() {
        muxing = Muxing()
        App.videoDecode.full = true // 设置视频流是否充满屏幕，不设置的话会自动计算 Surface 大小设置一个比例
        binding.textureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(
                surface: SurfaceTexture,
                width: Int,
                height: Int
            ) {
                mSurface = Surface(surface)
                App.videoDecode.apply {
                    videoDecodecCreateSurface(mSurface)
                    videoDecodecChangeSurface()
                }
            }

            override fun onSurfaceTextureSizeChanged(
                surface: SurfaceTexture,
                width: Int,
                height: Int
            ) {
                mSurface = Surface(surface)
                App.videoDecode.apply {
                    videoDecodecCreateSurface(mSurface)
                    videoDecodecChangeSurface()
                }
            }

            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
                App.videoDecode.apply {
                    videoDecodecDestorySurface()
                }
                return false
            }

            override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {

            }
        }
        try {
            initCamera(deviceUid, isApMode)
        } catch (e: Exception) {
            e.printStackTrace()
            finish()
        }
    }

    private fun initCamera(uid: String?, isApMode: Boolean?) {
        if (isApMode!!) {
            cameraWrapper = CameraManager.getInstance().getCameraWrapper(uid)
            if (cameraWrapper == null) {
                cameraWrapper = CameraManager.getInstance().getApCamera(uid)
            } else {
                ViseLog.i("找到AP设备")
            }
            if (cameraWrapper!!.status != 0) {
                cameraWrapper!!.reconnect()
            }
        } else {
            cameraWrapper = CameraManager.getInstance().getCameraWrapper(uid)
        }
        try {
            cameraWrapper!!.setPushCallback { messageBean ->
                when (messageBean.type) {
                    0//关闭摄像头
                    -> {
                        ViseLog.e("摄像头关闭")
                        cameraWrapper?.let {
                            cameraWrapper?.stopSendAudio()
                            encoderUtil?.pause()
                        }
                        cameraWrapper!!.stopReceiveVideo()
                        isRunning.set(false)
                    }

                    1//打开摄像头
                    -> {
                        ViseLog.e("打开摄像头")
                        binding.textureView.visibility = View.VISIBLE
                        isRunning.set(false)
                        ViseLog.e("开启视频")
                        startVideo()
                    }

                    2//设备端开始录制视频
                    -> ViseLog.e("设备端开始录制视频")

                    3//设备端停止录制视频
                    -> ViseLog.e("设备端停止录制视频")

                    4 -> {
                    }

                    5 -> {
                    }

                    6 -> {
                    }

                    7 -> {
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        deviceEntity = cameraWrapper?.getDeviceEntity()
        deviceInfoId = deviceEntity?.getDeviceInfoId()
        deviceName = deviceEntity?.getDeviceName()
        val status = cameraWrapper!!.getStatus()
        if (!isApMode) {
            if (status == 0 || status == 1) {
                ViseLog.e("开启倒计时")
                delayReconnectSubscrition = Observable.timer(8, TimeUnit.SECONDS)
                    .subscribe { aLong ->
                        ViseLog.e("连接设备倒计时后重连" + aLong!!)
                        ViseLog.e(cameraWrapper!!.getUid())
                        cameraWrapper!!.reconnect()
                    }
            } else {
                cameraWrapper!!.connect()
            }
        } else {
            cameraWrapper!!.connect()
        }
        cameraWrapper!!.setOnOffLineCallback(this, true)
        cameraWrapper!!.setWatching(true)//这个设备在前台观看
    }

    private fun requestPerm() {
        if (!XXPermissions.isGranted(
                mContext, Permission.MANAGE_EXTERNAL_STORAGE,
                Permission.CAMERA, Permission.RECORD_AUDIO
            )
        ) {
            requestPermissions(
                mContext, object : PermissionRetCall {
                    override fun onAllow() {
                        ViseLog.i("允许权限")
                    }
                },
                Permission.MANAGE_EXTERNAL_STORAGE, Permission.CAMERA, Permission.RECORD_AUDIO
            )
        }
    }


    /**
     * 更改画面质量的回调
     *
     * @param b
     */
    override fun success(p0: Boolean) {
        ViseLog.i("set void qxd = $p0")
    }

    override fun Offline(p0: Camera?) {
    }

    override fun Online(p0: Camera?, p1: Int) {
        mWeakHandler?.sendEmptyMessage(p1)
    }

    @SuppressLint("CheckResult")
    fun startVideo() {
        ViseLog.w("startVideo")
        if (cameraWrapper != null && cameraWrapper!!.getStatus() != 0) {
            ViseLog.w("还没有连接成功")
            return
        }
        val now = System.currentTimeMillis()
        if (now - lastStartTime < 500 && isRunning.get()) {
            lastStartTime = now
            ViseLog.w("两次启动视频时间间隔太短;")
            return
        }
        lastStartTime = now
        if (cameraWrapper!!.getStatus() == 0) {
            delayReconnectSubscrition?.let {
                delayReconnectSubscrition!!.dispose()
                delayReconnectSubscrition = null
            }
            delayReconnectSubscrition = Observable.timer(8, TimeUnit.SECONDS)
                .subscribe { aLong ->
                    ViseLog.e("连接设备倒计时后重连" + aLong!!)
                    ViseLog.e(cameraWrapper!!.getUid())
                    cameraWrapper!!.reconnect()
                }
            mSurface?.let {
                App.videoDecode.apply {
                    videoDecodecCreateSurface(mSurface)
                    videoDecodecChangeSurface()
                }
            }
            cameraWrapper!!.startRecvVideo { bytes, pts, keyframe ->
                totalVideo.set(totalVideo.get() + bytes.size) // 接收到的视频数据总长
                if (isRecordingMP4.get()) {
                    muxing?.write(bytes, false, -1)
                }
                if (!isRunning.get() && keyframe === 1) {
                    connectSuccess.set(true)
                    ViseLog.i(deviceUid + "接收到第一针")
                    initDifference =
                        System.currentTimeMillis() - java.lang.Long.parseLong(pts.toString() + "")
                    isRunning.set(true)
                    App.videoDecode.apply {
                        videoDecodec(bytes, pts)
                    }
                    if (cameraWrapper!!.getDevCap(CameraWrapper.Capability.CloudAi)) {
                        cameraWrapper!!.sendCommonBuffer(
                            100,
                            GetBufferJsonUtil.getInstance().setCloudAISwitch(true),
                            this
                        ) // 用与发送自定义的新功能指令，flag 100 data携带json，根据设备能力集发送对应的功能设备会回复处理结果
                    }
                } else if (isRunning.get()) {
                    if (keyframe == 1) {
                        Observable.just(1)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe { integer ->
                                if (binding.videoLoading.isShown()) {
                                    binding.videoLoading.hide()
                                }
                                delayReconnectSubscrition?.let {
                                    delayReconnectSubscrition!!.dispose()
                                    delayReconnectSubscrition = null
                                }
                            }
                        val tempDiff = System.currentTimeMillis() - pts
                        if (tempDiff < initDifference) {
                            initDifference = tempDiff
                        } else if (tempDiff > initDifference + 5 * 1000) {
                            cameraWrapper!!.getmCamera().cleanAudioBuf()
                            cameraWrapper!!.getmCamera().cleanVideoBuf()
                            ViseLog.e(cameraWrapper!!.getUid() + "延迟超过5秒，清空缓存")
                        }
                    }
                    App.videoDecode.apply {
                        videoDecodec(bytes, pts)
                    }
                }
            }
        } else {
            cameraWrapper!!.reconnect()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        delayReconnectSubscrition?.let {
            delayReconnectSubscrition!!.dispose()
            delayReconnectSubscrition = null
        }
        if (binding.videoLoading != null && binding.videoLoading.isShown()) {
            binding.videoLoading.hide()
        }
        cameraWrapper?.let {
            cameraWrapper!!.setWatching(false)
            cameraWrapper!!.stopReceiveAudio()
            cameraWrapper!!.stopReceiveVideo()
            cameraWrapper!!.stopSendAudio()
            cameraWrapper!!.setOnOffLineCallback(null, false)
            cameraWrapper!!.setPushCallback(null)
            if (cameraWrapper!!.isApMode()) {
                cameraWrapper!!.disconnect()
            }
        }
        encoderUtil?.let {
            encoderUtil!!.stop()
            encoderUtil!!.release()
            encoderUtil = null
        }
        App.videoDecode.apply {
            speed = 1 // 倍数调回一倍
            mSurface?.let {
                videoDecodecDestorySurface();
            }
            ViseLog.e("onDestroy释放videoDecodec")
        }
        decoderUtil?.run {
            stop()
            null
        }
        mDisposable?.run {
            dispose()
            clear()
        }
    }

    fun turnCamera(direction: Int) {
        cameraWrapper?.let {
            it.getmCamera()?.setPtz(direction, 12)
        }
    }

    fun taskPhoto() {
        val jpgPath = deviceFolder?.getAbsolutePath() + "/"
        val name = TimeUtil.getCurrDateTime() + deviceUid + ".jpg"
        try {
            App.videoDecode.takeJpeg(jpgPath + name) { s, b ->
                Observable.just(b)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { aBoolean ->
                        if (aBoolean) {
                            ToastUtil.showToast(context, getString(R.string.savePath))
                        }
                    }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun sendAudio(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                isRecordingAudio.set(true)
                startSendAudio()
                ViseLog.i("开始发送音频!!!")
                if (binding.cbxReceiveAudio.isChecked()) {
                    decoderUtil?.run {
                        pause()
                    }
                    ViseLog.i("暂停声音监听播放!!!!")
                }
            }

            MotionEvent.ACTION_UP -> {
                isRecordingAudio.set(false)
                cameraWrapper?.let {
                    it.getmCamera()?.run {
                        cleanAudioBuf()
                    }
                }
                if (binding.cbxReceiveAudio.isChecked()) {
                    decoderUtil?.run {
                        replayAudio()
                    }
                    ViseLog.i("延时恢复声音监听播放!!!!")
                }
                ViseLog.i("结束发送音频!!!")
                delayStopSendAudio()
            }

            MotionEvent.ACTION_CANCEL -> {
                isRecordingAudio.set(false)
                if (binding.cbxReceiveAudio.isChecked()) {
                    decoderUtil?.run {
                        replayAudio()
                    }
                    ViseLog.i("延时恢复声音监听播放!!!!")
                }
                ViseLog.i("结束发送音频!!!")
                delayStopSendAudio()
            }
        }
        return true
    }

    private fun startSendAudio() {
        delayStopSendAudioSub?.run {
            dispose()
            null
        }
        isSendAudioOn = cameraWrapper!!.getmCamera().isSendAudioOn
        ViseLog.e("是否正在发送音频 : $isSendAudioOn")
        if (isSendAudioOn) {
            return
        }
        if (encoderUtil == null) {
            encoderUtil = AACEncoderUtil()
            encoderUtil!!.prepare(AACEncoderUtil.MyAudioFormart.G711A);
        }
//        cameraWrapper?.sendAudio(isRecordingAudio.get(), encoderUtil)
        cameraWrapper?.startSendAudio { data ->
            if (isRecordingAudio.get()) {
                if (encoderUtil != null && encoderUtil!!.ifPrepared() && encoderUtil!!.getAudioRecord() != null) {
                    encoderUtil!!.getAudioRecord().read(data, 0, data.size)
                    ViseLog.i("data length = $data.size")
                    return@startSendAudio data.size
                } else {
                    return@startSendAudio 0
                }
            } else {
                if (encoderUtil != null && encoderUtil!!.ifPrepared() && encoderUtil!!.getAudioRecord() != null) {
                    encoderUtil!!.getAudioRecord().read(ByteArray(160), 0, 160)
                }
                return@startSendAudio 0
            }
        }
    }

    /**
     * 延时关闭发送音频通道
     */
    private fun delayStopSendAudio() {
        delayStopSendAudioSub = Observable.timer(8, TimeUnit.SECONDS)
            .subscribe { aLong ->
                cameraWrapper?.run {
                    stopSendAudio()
                }
            }
        mDisposable?.add(delayStopSendAudioSub!!)
    }


    /**
     * 关闭接收音频通道
     */
    private fun delayStopReceiveAudio() {
        cameraWrapper?.run {
            stopReceiveAudio()
        }
    }

    @SuppressLint("CheckResult")
    fun replayAudio() {
        Observable.timer(1, TimeUnit.SECONDS)
            .subscribe { aLong ->
                decoderUtil!!.replay()
            };
    }

    private fun receviceAudio(isChecked: Boolean) {
        if (isChecked) {
            startReceiveAudio()
        } else {
            delayStopReceiveAudio()
        }
        decoderUtil?.run {
            if (isChecked) {
                replay()
            } else {
                pause()
            }
        }
    }

    private fun startReceiveAudio() {
        if (cameraWrapper!!.getmCamera()!!.isRecvAudioOn) {
            ViseLog.i("正在接收音频!!!")
            return
        }
        if (decoderUtil == null) {
            decoderUtil = AACDecoderUtil()
        }
        setSpeakerphoneOn(context, true)
        if (binding.cbxReceiveAudio.isChecked) {
            decoderUtil!!.replay()
        } else {
            decoderUtil!!.pause()
        }
        cameraWrapper?.let {
            ViseLog.e("开启设备声音监听")
            it.startReceiveAudio { data, pts ->
                decoderUtil?.decodePCM(data, 0, data.size)
            }
        }
    }

    fun setSpeakerphoneOn(ctx: Context, mode: Boolean) {
        val audioManager = ctx.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.isSpeakerphoneOn = mode//打开扬声器
    }


    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (currentDpi != position) {
            currentDpi = position
            cameraWrapper?.run {
                when (position) {
                    0 -> {
                        cameraWrapper!!.setQuality(
                            VIDEO_QUALITY_MIN,
                            this@WatchVideoActivity
                        )
                    }

                    1 -> {
                        cameraWrapper!!.setQuality(
                            VIDEO_QUALITY_MIDDLE,
                            this@WatchVideoActivity
                        )
                    }

                    2 -> {
                        cameraWrapper!!.setQuality(
                            VIDEO_QUALITY_MAX,
                            this@WatchVideoActivity
                        )
                    }
                }
                getmCamera()?.run {
                    cleanAudioBuf()
                    cleanVideoBuf()
                    this
                }
            }
        }
    }

    // 结束视频录制
    private fun closeRecordFile() { //        KLog.e("关闭文件" + isRecordingMP4.get());
        if (isRecordingMP4.get()) {
            isRecordingMP4.set(false)
            muxing!!.close()
            val file = File(mp4Path!!)
            ViseLog.w("视频名字 $mp4Path file.length  = ${file.length()}")
            if (file.exists() and (file.length() < 30 * 1024)) {
                file.delete()
                ToastUtil.showToast(context, getString(R.string.recordTimeLess))
            } else {
                ToastUtil.showToast(context, getString(R.string.savePath))
            }
            mp4Path = ""
        }
    }

    @SuppressLint("CheckResult")
    fun queryRecordVideo() {
        ViseLog.i("$startToDayMillis 开始查询 $currentTimeMillis")
        if (!cameraWrapper!!.deviceInfo.hasSdcard) {
            return
        }
        val fileTimeInfos = cameraWrapper!!.deviceMap[startToDayMillis]
        if (fileTimeInfos == null) {
            Observable.create { emitter: ObservableEmitter<ArrayList<fileTimeInfo>> ->
                cameraWrapper!!.getmCamera()
                    .getVideoFiletime(startToDayMillis / 1000) { i, arrayList ->
                        // 获取今天的所有视频录像片段数据  i 为 0成功 否则失败
                        if (i == 0) {
                            ViseLog.i(arrayList)
                            emitter.onNext(arrayList)
                            emitter.onComplete()
                        } else {
                            emitter.tryOnError(Throwable("$i"))
                        }
                    }
            }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { disposable: Disposable? ->
                    mDisposable?.add(disposable!!)
                    showLoading(disposable)
                }.doFinally {
                    dismissLoading()
                }.subscribe({
                    fileTimeInfoArrayList = it
                    if (it.size == 0) {
                        cameraWrapper!!.deviceMap[startToDayMillis] = ArrayList()
                        ToastUtil.showToast(context, context.getString(R.string.noVideosToday))
                    } else {
                        playbackAdapter!!.setNewData(it)
                        cameraWrapper!!.deviceMap[startToDayMillis] = it
                    }
                }, {
                    ViseLog.e("查询出错")
                    val msg = it.message
                    if (msg.equals("1")) {
                        Toasty.error(context, getString(R.string.sdcardNotAvailable)).show()
                    } else {
                        Toasty.error(context, getString(R.string.queryError) + msg).show()
                    }
                }, {

                }, {

                })
        } else {
            fileTimeInfoArrayList = fileTimeInfos
            playbackAdapter!!.setNewData(fileTimeInfos)
        }
    }

    @SuppressLint("CheckResult")
    fun playBack(fileInfo: fileTimeInfo, index: Int) {
        val duration: Long = fileInfo.playbackTotalTime * 1000.toLong() //
        ViseLog.i("duration", fileInfo.playbackTotalTime)
        val initTime: Long = fileInfo.videofiletime * 1000
        val fileName = DateUtils.getDateTime2(fileInfo.videofiletime * 1000).toString() + ".mp4"
        ViseLog.e("开始回放" + fileName + DateUtils.getDateTime2(initTime))
        setSpeakerphoneOn(context, true)
        if (decoderUtil == null) {
            decoderUtil = AACDecoderUtil()
        }
        Observable.create { emitter: ObservableEmitter<Long> ->
            cameraWrapper!!.getmCamera()
                .playBackVideoStart(fileName, 0, { i, bytes, total, current, b ->
                    if (i == 0) {   //第一帧开始
                        App.videoDecode.let {
                            if (isPlayBackVideo) {
                                it.videoDecodec(bytes, 0)
                                totalVideo.set(totalVideo.get() + bytes.size)
                            }
                        }
                        muxing?.let {
                            if (isRecordingMP4.get()) {
                                it.write(bytes, false, -1)
                            }
                        }
                        var playedMills = (current * duration / total) + 1
                        temFrameCount++
                        if (temFrameCount % 10 == 0) {
                            emitter.onNext(playedMills)
                            temFrameCount = 0
                        }
                        if (current >= total) {
                            emitter.onComplete()
                        }
                    } else {
                        ViseLog.e("错误回放码 $i")
                        emitter.tryOnError(Throwable("$i"))
                    }
                }, { bytes, l ->
                    decoderUtil?.let {
                        it.decodePCM(bytes, 0, bytes.size)
                        muxing?.let {
                            if (isRecordingMP4.get()) {
                                it.write(bytes, true, -1)
                            }
                        }
                    }
                })
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { disposable: Disposable? ->

            }.doFinally {
                binding.videoLoading.hide()
                dismissLoading()
            }.subscribe({
                decoderUtil?.let {
                    if (binding.cbxReceiveAudio.isChecked) {
                        it.replay()
                    } else {
                        it.pause()
                    }
                }
                if (binding.videoLoading.isShown) {
                    binding.videoLoading.smoothToHide()
                }
                temFrameCount++

            }, {
                cameraWrapper?.run {
                    if (getmCamera() != null) {
                        playBackVideoStop();
                    }
                }
                Toasty.error(context, context.getString(R.string.playbackError)).show()
            }, {
                cameraWrapper?.run {
                    if (getmCamera() != null) {
                        playBackVideoStop();
                    }
                }
                delayToPlaySdSub = Observable.timer(1, TimeUnit.SECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { aLong: Long? ->
                        if (index + 1 <= fileTimeInfoArrayList.size - 1 && isPlayBackVideo) {
                            playBack(fileTimeInfoArrayList[index + 1], index + 1)
                            ViseLog.e(
                                "查找下一个$$$$$$$$$$$$$$$$$$$$$$$ 录像" +
                                        DateUtils.getDateTime2(fileTimeInfoArrayList[index + 1].videofiletime * 1000)
                            )
                        }
                    }
            }, {
                playSDSub = it
                mDisposable.add(it)
            })
    }

    override fun call(var1: Int, var2: Byte, var3: String?) {

    }
}
