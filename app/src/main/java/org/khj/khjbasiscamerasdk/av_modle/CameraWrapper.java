package org.khj.khjbasiscamerasdk.av_modle;


import android.annotation.SuppressLint;
import android.os.SystemClock;
import android.text.TextUtils;

import com.khj.Aes;
import com.khj.Camera;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.vise.log.ViseLog;

import org.greenrobot.eventbus.EventBus;
import org.khj.khjbasiscamerasdk.App;
import org.khj.khjbasiscamerasdk.R;
import org.khj.khjbasiscamerasdk.bean.CameraMessageBean;
import org.khj.khjbasiscamerasdk.bean.DeviceInfoBean;
import org.khj.khjbasiscamerasdk.database.EntityManager;
import org.khj.khjbasiscamerasdk.database.entity.DeviceEntity;

import org.khj.khjbasiscamerasdk.utils.AACDecoderUtil;
import org.khj.khjbasiscamerasdk.utils.AACEncoderUtil;
import org.khj.khjbasiscamerasdk.utils.SharedPreferencesUtil;
import org.khj.khjbasiscamerasdk.utils.ToastUtil;
import org.khj.khjbasiscamerasdk.utils.WiFiUtil;
import org.khjsdk.com.khjsdk_2020.eventbus.CameraStatusEvent;
import org.khjsdk.com.khjsdk_2020.eventbus.CheckOnLineEvent;
import org.khjsdk.com.khjsdk_2020.eventbus.FlowSingnalValueEvent;
import org.khjsdk.com.khjsdk_2020.globalizationUtil.GetInitString;

import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import es.dmoral.toasty.Toasty;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by ShuRun on 2018/4/24.
 * 摄像头设备的包装类，管理摄像头各种信息与状态
 */
public class CameraWrapper implements Camera.onOffLineCallback {

    private String realPWD;
    public AtomicInteger reconnectTimes = new AtomicInteger(4);
    private ActivePushCallback mPushCallback;
    private boolean isApMode;//是否是AP模式
    private String deviceSSID;//设备处于AP模式下的热点SSID
    private Camera mCamera;
    private long lastOfflineTime;//上一次offline回调时间
    private long lastOnlineTime;//上一次Online回调时间
    private long lastConnectTime;//上一次connect时间
    private volatile int switchStatus;//设备各种开关量的状态，按位计算。第0位代表枪机白光灯，第一位代表智能夜视
    private boolean isOldAes;//设备密码的加密库是否是旧版
    private Camera.onOffLineCallback realOnOffCallback;
    public String newestVersion;
    private QMUITipDialog updateLoadingDialog;
    private boolean isWatching;//是否处于前台观看视频
    private AtomicBoolean hasNewVersion = new AtomicBoolean(false);//是否有新版本固件
    private int devCap = -1;//设备能力集，在服务器的设备字段为ptz
    private AtomicBoolean isSupportCap = new AtomicBoolean(false);
    public long lastUpdateFirmwareTipTime;//记录上次弹窗提醒升级固件的时间
    public int defenceStatus = -1;//0撤防，1布防
    private boolean isContinuousRecord;
    private String uid;
    private String deviceAccount = "admin";
    private String passWord;
    private AtomicInteger connectStatus = new AtomicInteger(1);//0连接成功，1正在连接，2连接失败，3密码错误,4断开连接,5设备离线,6UID失效
    private String statusMessage = "";
    private DeviceEntity deviceEntity;
    private boolean canUpdateFileSystem = false;
    private String sixEight = "888888";
    private String sixOne = "111111";
    private AtomicInteger checkOnLineStatus = new AtomicInteger(-1);
    private HashMap<Long, List<Camera.fileTimeInfo>> deviceMap = new HashMap<>();//设备SD卡录像数据缓存
    private volatile DeviceInfoBean deviceInfo;

    public boolean isContinuousRecord() {
        return isContinuousRecord;
    }

    public enum Capability {
        PTZ,// 云台
        BATTERY,// 电池
        WHITE_LIGHT,// 白光灯
        MD,// 移动侦测
        VD,// 声音报警
        CRY,// 哭声检测
        PD,// 人形检测
        FD,// 人脸检测
        DEV_ABTY_CUSTOM_VOICE,        // 自定义报警音
        MCU433,        // 433模块
        FOURG,        // 4G
        BALL,         // 球机
        BUTT
    }

    public void setWatching(boolean watching) {
        isWatching = watching;
    }

    public boolean isWatching() {
        return isWatching;
    }

    /**
     * 是否支持能力集
     *
     * @return
     */
    public boolean getIsSupportCap() {
        return isSupportCap.get();
    }

    /**
     * 获取设备的能力集
     *
     * @param capability
     * @return
     */
    public boolean getDevCap(Capability capability) {
        ViseLog.i("获取设备能力集:" + devCap + isSupportCap.get());
        if (!isSupportCap.get() || devCap == -1) {
            return false;
        }
        switch (capability) {
            case PTZ:
                return (devCap & 0x01) == 0x01;
            case BATTERY:
                return (devCap & 0x02) == 0x02;
            case WHITE_LIGHT:
                return (devCap & 0x04) == 0x04;
            case MD:
                return (devCap & 0x08) == 0x08;
            case VD:
                return (devCap & 0x10) == 0x10;
            case CRY:
                return (devCap & 0x20) == 0x20;
            case PD:
                return (devCap & 0x40) == 0x40;
            case FD:
                return (devCap & 0x80) == 0x80;
            case DEV_ABTY_CUSTOM_VOICE:
                return (devCap & 0x100) == 0x100;
            case MCU433:
                return (devCap & 0x200) == 0x200;
            case FOURG:
                return (devCap & 0x400) == 0x400;
            case BALL:
                return (devCap & 0x800) == 0x800;
        }

        return false;
    }

    public boolean isHasNewVersion() {
        ViseLog.e("isHasNewVersion" + hasNewVersion.get());
        return hasNewVersion.get();
    }

    public void setHasNewVersion(boolean is) {
        this.hasNewVersion.set(is);
    }


    public String getUid() {
        return uid;
    }


    public Camera getmCamera() {

        return mCamera;
    }

    public void playBackVideoStop() {
        ViseLog.e("playBackVideoStop");
//        new Exception().printStackTrace();
        mCamera.playBackVideoStop();
    }

    public HashMap<Long, List<Camera.fileTimeInfo>> getDeviceMap() {
        return deviceMap;
    }


    public void setPushCallback(ActivePushCallback pushCallback) {
        mPushCallback = pushCallback;
        if (mPushCallback != null) {
            mPushCallback.onPush(new CameraMessageBean(4, deviceInfo.currentAudienceNum));
        }
    }

    public boolean isApMode() {
        return isApMode;
    }

    public void setApMode(boolean apMode) {
        isApMode = apMode;
    }

    public int getSwitchStatus() {
        return switchStatus;
    }

    public void setSwitchStatus(int switchStatus) {
        this.switchStatus = switchStatus;
        ViseLog.i("switchStatus set" + this.switchStatus);
    }

    /**
     * @param onOffLineCallback
     * @param receiveFirst      是否首先接受原先的状态信息
     */
    public void setOnOffLineCallback(Camera.onOffLineCallback onOffLineCallback, boolean receiveFirst) {
        this.realOnOffCallback = onOffLineCallback;
        if (realOnOffCallback != null && receiveFirst) {
            realOnOffCallback.Online(mCamera, connectStatus.get());
        }


    }


    public DeviceInfoBean getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(DeviceInfoBean deviceInfo) {
        this.deviceInfo = deviceInfo;
    }


    public DeviceEntity getDeviceEntity() {
        return deviceEntity;
    }

    public void setDeviceEntity(DeviceEntity entity) {
        this.deviceEntity = entity;
        isOldAes = true;
        passWord = deviceEntity.getDevicePwd();
//        realPWD = Aes.aesDecrypt(passWord);
        if (deviceEntity.getDevicePwd().equals("888888")) {
            realPWD = "888888";
            ViseLog.i(uid + deviceEntity.getDevicePwd());
        } else {
            realPWD = Aes.aesDecrypt(deviceEntity.getDevicePwd());
            ViseLog.i(uid + "realPWD:" + realPWD + "isOldAes:" + isOldAes + deviceEntity.getDevicePwd());
        }
        if (connectStatus.get() != 0 && connectStatus.get() != 1) {
            if (mCamera != null) {
                mCamera.disconnect();
                ViseLog.e("CameraWrapper connect = " + uid + "连接camera" + realPWD);
                connect();
            }
        }
    }

    public String getConnectStatusMessage() {
        int status = connectStatus.get();
        switch (status) {
            case 0:
                statusMessage = "连接成功";
                break;
            case 1:
                statusMessage = App.context.getString(R.string.deviceConnecting);
                break;
            case 2:
                statusMessage = App.context.getString(R.string.connectFailure);
                break;
            case 3:
                statusMessage = App.context.getString(R.string.passwordError);
                break;
            case 4:
                statusMessage = "断开连接";
            case 5:
//                statusMessage = "设备不在线";
                statusMessage = App.context.getString(R.string.offline);
                break;
            case 6:
                statusMessage = "UID失效";
                break;
            case 7:
                statusMessage = "通道被占用";
                break;
            case 8:
                statusMessage = "连接达到最大";
                break;

        }
        if (status == 5) {
            statusMessage = App.context.getString(R.string.offline);
        } else if (status == 3) {
            statusMessage = App.context.getString(R.string.passwordError);
        } else if (status == 0) {
            statusMessage = App.context.getString(R.string.online);
        }
        return statusMessage;
    }

    public void checkOnLine() {
        if (mCamera != null) {
            mCamera.checkOnline((var) -> {
                ViseLog.i("huangmin check on line = " + var);
                if (checkOnLineStatus.get() != var) {
                    checkOnLineStatus.set(var);
                    EventBus.getDefault().post(new CheckOnLineEvent(uid));
                }
            });
        }
    }

    public Integer getCheckOnLineStatus() {
        return checkOnLineStatus.get();
    }

    public int getStatus() {
        return connectStatus.get();
    }

    public void setStatus(int status) {
        ViseLog.i("status:" + status);
        connectStatus.set(status);
    }


    public CameraWrapper(DeviceEntity entity) {
        this.deviceEntity = entity;
        isOldAes = true;
        if (deviceEntity.getDevicePwd().equals("888888")) {
            realPWD = "888888";
            ViseLog.i(uid + deviceEntity.getDevicePwd());
        } else {
            realPWD = Aes.aesDecrypt(deviceEntity.getDevicePwd());
            ViseLog.i(uid + "realPWD:" + realPWD + "isOldAes:" + isOldAes + deviceEntity.getDevicePwd());
        }
        deviceInfo = new DeviceInfoBean();
        initCamera();
        checkOnLine();
    }

    public CameraWrapper(DeviceEntity entity, boolean isApMode) {
        this.deviceEntity = entity;
        isOldAes = true;
        if (isApMode) {
            realPWD = deviceEntity.getDevicePwd();
            ViseLog.e("pwd" + realPWD);
            ViseLog.i(uid + deviceEntity.getDevicePwd());
        } else {
            realPWD = Aes.aesDecrypt(deviceEntity.getDevicePwd());
            ViseLog.i(uid + "realPWD:" + realPWD + "isOldAes:" + isOldAes + deviceEntity.getDevicePwd());
            ViseLog.w(uid + "realPWD:" + realPWD + "isOldAes:" + isOldAes + deviceEntity.getDevicePwd());
        }
        deviceInfo = new DeviceInfoBean();
        uid = deviceEntity.getDeviceUid();
        deviceAccount = deviceEntity.getDeviceAccount();
        passWord = deviceEntity.getDevicePwd();
        mCamera = new Camera(deviceEntity.getDeviceUid() + GetInitString.Companion.get().getString(uid.substring(0, 3)));
        checkOnLine();
    }

    public boolean getCanUpdateFileSystem() {
        return canUpdateFileSystem;
    }

    public void setCanUpdateFileSystem(boolean canUpdateFileSystem) {
        this.canUpdateFileSystem = canUpdateFileSystem;
    }

    /**
     * c初始化摄像头连接
     */
    private void initCamera() {
        uid = deviceEntity.getDeviceUid();
        deviceAccount = deviceEntity.getDeviceAccount();
        passWord = deviceEntity.getDevicePwd();
        mCamera = new Camera(uid + GetInitString.Companion.get().getString(uid.substring(0, 3)));
        ViseLog.e(uid + "连接camera" + realPWD);
        ViseLog.i(deviceEntity.toString());
        connect();
    }

    @SuppressLint("CheckResult")
    @Override
    public void Online(Camera camera, int i) {
        ViseLog.i("online" + i);
        if (i == -1) {
            Camera.init();
            SystemClock.sleep(1000);
            ViseLog.i("z暂停200");
        }
        if (i != 0 && i != -90 && reconnectTimes.get() > 0 && i != -20009 && i != -26) {
            if (reconnectTimes.get() == 3) {
                ViseLog.i("第一次重连错误" + i);
                reconnect();
            } else if (reconnectTimes.get() == 2) {
                ViseLog.i("第二次重连错误" + i);
                reconnect();
            } else {
                ViseLog.i("第三次重连错误" + i);
                reconnect();
            }
            return;
        }
        switch (i) {
            case 0://连接成功
                connectStatus.set(0);
                reconnectTimes.set(3);
                mCamera = camera;
                mCamera.setPhpServer("http://127.0.0.1:8080", (b, s) -> ViseLog.i(b + s + "设置设备推送报警消息服务器地址"));
                mCamera.getPhpServer((b, s) -> ViseLog.i(b + s + "获取报警地址"));
                Observable.create((ObservableOnSubscribe<Boolean>) emitter -> {
                }).observeOn(AndroidSchedulers.mainThread())
                        .subscribe(aBoolean -> {
                            deviceEntity.setDevicePwd(realPWD);
                            EntityManager.getInstance().getDeviceEntityDao().update(deviceEntity);
                        });
                if (isApMode && realPWD.equals("888888")) {
//                    mCamera.changePassword("888888", sixOne, b -> {
//                        if (b) {
//                            SharedPreferencesUtil.setString(App.context, uid, sixOne);
//                            deviceEntity.setDevicePwd(sixOne);
//                            EntityManager.getInstance().getDeviceEntityDao().update(deviceEntity);
//                            realPWD = sixOne;
//                            ViseLog.e("pwd", "原始密码连接成功，修改密码" + sixOne);
//                        }
//                    });
                }
                mCamera.registerActivePush2((type, message) -> {
                    ViseLog.i(uid + "收到消息" + type + "*" + message);
                    CameraMessageBean cameraMessageBean = new CameraMessageBean(type, message);
                    switch (type) {
                        case 0://关闭摄像头
                            ViseLog.i(uid + "关闭摄像头");
                            deviceInfo.hasTurnOff = true;
                            EventBus.getDefault().post(new CameraStatusEvent(uid, connectStatus.get(), getConnectStatusMessage()));
                            break;
                        case 1://打开摄像头
                            ViseLog.i(uid + "打开摄像头");
                            deviceInfo.hasTurnOff = false;
                            EventBus.getDefault().post(new CameraStatusEvent(uid, connectStatus.get(), getConnectStatusMessage()));
                            break;
                        case 2://设备端开始录制视频
                            ViseLog.i("设备端开始录制视频");
                            deviceInfo.isRecordingMp4 = true;
                            break;
                        case 3://设备端停止录制视频
                            ViseLog.i("设备端停止录制视频");
                            deviceInfo.isRecordingMp4 = false;
                            break;
                        case 4://观看人数
                            deviceInfo.currentAudienceNum = message;
                            break;
                        case 5://sd卡插入拔出
                            String data = cameraMessageBean.getData();
                            ViseLog.i("sd卡插入拔出" + data);
                            Observable.just(data)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(s -> {
                                        if (s.equals("0")) {
                                            Toasty.info(App.context, App.context.getString(R.string.removeSdcard)).show();
                                        } else {
                                            Toasty.info(App.context, App.context.getString(R.string.insertSdcard)).show();
                                        }
                                    });
                            queryDeviceInfo();
                        case 6:
                            //分辨率改变
//                            camera.setQuality(Integer.parseInt(message), new Camera.successCallback() {
//                                @Override
//                                public void success(boolean b) {
//
//                                }
//                            });
                            break;
                        case 7://电池变化
                            break;
                        case 8://布防撤防状态，message,0撤防，1布防
                            defenceStatus = Integer.parseInt(message);
                            ViseLog.i("布防状态:" + defenceStatus);
                            EventBus.getDefault().post(new CameraStatusEvent(uid, connectStatus.get(), getConnectStatusMessage()));
                            break;
                        case 9: { // 4G流量信号值，若非4G设备可忽略
                            ViseLog.i("4G = " + message);
                            EventBus.getDefault().post(new FlowSingnalValueEvent(message, uid));
                            break;
                        }
                    }
                    if (mPushCallback != null) {
                        Observable.just(cameraMessageBean)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(messageBean -> {
                                            if (mPushCallback != null) {
                                                mPushCallback.onPush(messageBean);
                                            }
                                        }
                                );
                    }
                });

                queryDeviceInfo();

                mCamera.getForceOpenCamera(i12 -> {
                    deviceInfo.hasTurnOff = i12 != 1;
//                    KLog.e("当前设备的开关状态" + i12);
                });
                break;
            case -20009://密码错误
                connectStatus.set(3);
                if (isApMode) {
                    connectStatus.set(1);
                    if (realPWD.equals(sixEight)) { // 如果 6个8连接密码错误，改为6个1连接
                        deviceEntity.setDevicePwd(sixOne);
                        passWord = sixOne;
                        realPWD = sixOne;
                        SharedPreferencesUtil.setString(App.context, uid, sixOne);
                    } else if (realPWD.equals(sixOne)) {    // 如果 6个1连接密码错误，改为6个8连接
                        deviceEntity.setDevicePwd(sixEight);
                        passWord = sixEight;
                        realPWD = sixEight;
                        SharedPreferencesUtil.setString(App.context, uid, sixEight);
                    }
                }
                break;
            case -20027://IOTC channel is used by other av channel
                mCamera.disconnect();
                break;
            case -90://设备不在线
                connectStatus.set(5);
                break;
            case -10://UID失效
                connectStatus.set(6);
                break;
            case -48://连接达到最大
                connectStatus.set(8);
                ViseLog.i("连接达到最大");
                break;
            default:
                connectStatus.set(2);

        }

        if (realOnOffCallback != null) {
            realOnOffCallback.Online(camera, i);
        }
        ViseLog.i(uid + "连接camera状态改变了" + "*" + i + "*" + connectStatus.get() + "*" + getConnectStatusMessage());
        EventBus.getDefault().post(new CameraStatusEvent(uid, connectStatus.get(), getConnectStatusMessage()));
    }

    @Override
    public void Offline(Camera camera) {
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - lastOfflineTime < 3000) {
            return;
        }
        lastOfflineTime = currentTimeMillis;
        if (realOnOffCallback != null) {
            realOnOffCallback.Offline(camera);
        }
        connectStatus.set(5);
        ViseLog.e(uid + "心跳包断开了**********************************************");
        EventBus.getDefault().post(new CameraStatusEvent(uid, connectStatus.get(), getConnectStatusMessage()));
        reconnectTimes.set(3);
    }

    private void setTimeZone() {
        TimeZone aDefault = TimeZone.getDefault();
        int rawOffset = aDefault.getRawOffset();
        rawOffset = rawOffset / (1000 * 60);
        int finalRawOffset = rawOffset;
        mCamera.getTimezone(i -> {
            if (i != finalRawOffset) {
                mCamera.setTimezone(finalRawOffset, b -> {
                });
            }
        });
    }

    public void queryDeviceInfo() {
        ViseLog.i("查询设备信息" + uid);
        mCamera.queryDeviceInfo((sdcardTotal, sdcardFree, v1, v2, v3, v4, model, vender, fileSystem) -> {

            StringBuilder builder = new StringBuilder();
            String version = builder.append(v2).append(".").append(v3).append(".").append(v4).toString();
            deviceInfo.firmwareVersion = version;
            String[] current = deviceInfo.firmwareVersion.split("\\.");
            if (current.length == 3) {
                int a = Integer.parseInt(current[0]);//方案1国科，2联永，3君正
                int b = Integer.parseInt(current[1]);//0 tutk,1 看护家
                int c = Integer.parseInt(current[2]);
                if (a == 2 || (a == 1 && b > 0 && c > 3)) {
                    isSupportCap.set(true);
                    mCamera.getCapability(i -> {
                        ViseLog.i("设备能力集" + i);
                        devCap = i;
                    });
                } else {
                    isSupportCap.set(false);
                }
            }
//            KLog.w("当前新版本*：" + deviceInfo.firmwareVersion);
            deviceInfo.sdcardTotal = sdcardTotal;
            deviceInfo.sdcardFree = sdcardFree;
            deviceInfo.hasSdcard = sdcardTotal != 0;
            int devcieType = mCamera.getDevcieType();
            deviceInfo.deviceType = devcieType;
            ViseLog.i("fileSystem" + fileSystem);
            if (!TextUtils.isEmpty(fileSystem) && !fileSystem.equals("exfat") && getDeviceInfo().hasSdcard) {
                canUpdateFileSystem = true;
                ViseLog.i("canUpdateFileSystem = true" + fileSystem);
            }
        });
        String deviceName = deviceEntity.getDeviceName();
        mCamera.getDeviceAlias(s -> {
            if (!deviceName.equals(s)) {
                mCamera.setDeviceAlias(deviceName, b -> {
                    ViseLog.e("设置别名" + b);
                });

            }
        });
        mCamera.getSwitch(i -> {
            switchStatus = i;
            ViseLog.d("switchStatus:" + switchStatus);
        });
        if (isApMode) {
            setTimeZone();
        }

        mCamera.getVideoRecordType(new Camera.successCallbackI() {
            @Override
            public void success(int i) {
                ViseLog.d("videoRecordStatus2:" + i);
                if (i == 1) {
                    isContinuousRecord = true;
                }
            }
        });
        /**
         * 查询布防状态
         */
        mCamera.sendCommonBuffer((byte) 6, "", (i, b, s) -> {
            if (s != null && !TextUtils.isEmpty(s)) {
                try {
                    defenceStatus = Integer.parseInt(s);
                    EventBus.getDefault().post(new CameraStatusEvent(uid, connectStatus.get(), getConnectStatusMessage()));
                } catch (Exception e) {
                    e.printStackTrace();
                    ViseLog.i("sendCommonBuffer Exception = " + e.getMessage());
                }
            }
        });

    }


    /**
     * 接受视频数据
     *
     * @param videoCallback
     */
    public void startRecvVideo(Camera.recvVideoCallback videoCallback) {
        if (mCamera == null) {
            return;
        }
        if (connectStatus.get() == 0) {
            mCamera.startRecvVideo(videoCallback);
            ViseLog.w("接受视频数据" + uid);
        }
    }

    /**
     * 停止接受视频数据
     */
    public void stopReceiveVideo() {
        if (mCamera == null) {
            return;
        }
        mCamera.stopRecvVideo();
//        EventBus.getDefault().post(new CameraStatusEvent(uid, 0, getConnectStatusMessage()));
//        KLog.e(uid+"停止接受视频数据"+getConnectStatusMessage());
    }

    /**
     * 开始接收音频数据
     *
     * @param audioCallback
     */
    public void startReceiveAudio(Camera.recvAudioCallback audioCallback) {
        if (mCamera == null) {
            return;
        }
        if (connectStatus.get() == 0 || connectStatus.get() == 5) {
            mCamera.startRecvAudio(audioCallback);
        }
    }

    /**
     * 停止接受视频数据
     */
    public void stopReceiveAudio() {
        if (mCamera == null) {
            return;
        }
        mCamera.stopRecvAudio();

    }

    public void disconnect() {
        if (mCamera == null) {
            return;
        }
        mCamera.disconnect();
        connectStatus.set(5);
        EventBus.getDefault().post(new CameraStatusEvent(uid, connectStatus.get(), getConnectStatusMessage()));
        ViseLog.e(uid + "断开camera" + getConnectStatusMessage() + connectStatus.get());
    }


    public void release() {
        if (mCamera == null) {
            return;
        }
        mPushCallback = null;
        mCamera.release();
        mCamera = null;
        connectStatus.set(4);
        EventBus.getDefault().post(new CameraStatusEvent(uid, connectStatus.get(), getConnectStatusMessage()));
//        KLog.e(uid+"释放camera"+getConnectStatusMessage()+connectStatus.get());
    }

    public int getAudioFormat() {
        return mCamera.getAudioFormat();
    }

    public void startSendAudio(Camera.sendAudioCallback sendAudioCallback) {
        if (mCamera == null) {
            return;
        }
        mCamera.startSendAudio(sendAudioCallback);
    }

    public void stopSendAudio() {
        if (mCamera == null) {
            return;
        }
        mCamera.stopSendAudio();
    }

    public void setQuality(int i, Camera.successCallback successCallback) {
        if (mCamera == null) {
            return;
        }
        mCamera.setQuality(i, successCallback);
    }

    //6120298721395739651
    public int getVideoQuality() {
        if (mCamera == null) {
            return 0;
        }
        return mCamera.getVideoQuality();
    }

    /**
     * 连接
     */
    public void connect() {
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - lastConnectTime < 1000) {
            ViseLog.i("Online", "两次Online时间间隔少于1秒");
            return;
        }
        lastOnlineTime = currentTimeMillis;
        if (mCamera == null) {
            return;
        }
        ViseLog.w(uid + "-------" + realPWD + isApMode);
        if (realPWD == null) {
            connectStatus.set(3);
            EventBus.getDefault().post(new CameraStatusEvent(uid, connectStatus.get(), getConnectStatusMessage()));
            return;
        }
        connectStatus.set(1);
        ViseLog.i(uid + "连接camera" + connectStatus.get() + "realPWD = " + realPWD);
        mCamera.connect("admin", "888888", 0, this);
        EventBus.getDefault().post(new CameraStatusEvent(uid, connectStatus.get(), getConnectStatusMessage()));
    }

    /**
     * 重新接
     */
    @SuppressLint("CheckResult")
    public void reconnect() {
        if (mCamera == null) {
            return;
        }
        ViseLog.i(WiFiUtil.getInstance(App.context).getSSID());
        if (realPWD == null) {
            connectStatus.set(3);
            return;
        }
        ViseLog.e(uid + "连接camera" + connectStatus.get() + "realPWD = " + realPWD);
        mCamera.reconnect(deviceAccount, realPWD, 1, this);
        connectStatus.set(1);
        ViseLog.w(uid + "断线重连第" + reconnectTimes.get());
        if (realOnOffCallback != null) {
            realOnOffCallback.Online(mCamera, connectStatus.get());
        }
        reconnectTimes.set(reconnectTimes.get() - 1);
        EventBus.getDefault().post(new CameraStatusEvent(uid, connectStatus.get(), getConnectStatusMessage()));
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    public interface ActivePushCallback {
        void onPush(CameraMessageBean messageBean);
    }

    @SuppressLint("CheckResult")
    public void ChangeWitchLightStatus(boolean isChecked) {
        int before = getSwitchStatus();
        int after = before;
        if (isChecked) {
            after = after | 1;
        } else {
            after = after & 0xfffffffe;
        }
        ViseLog.d("switchBefore:" + before + "*" + "After:" + after);
        int finalI = after;
        Observable.create((ObservableOnSubscribe<Boolean>) emitter -> {
            getmCamera().setSwitch(finalI, b -> {
                emitter.onNext(b);
                emitter.onComplete();
            });
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> {
                    ToastUtil.showToast(App.context, aBoolean);
                    if (aBoolean) {
                        setSwitchStatus(finalI);
                        ViseLog.d("设置switch" + finalI);
                    }
                });
    }

}
