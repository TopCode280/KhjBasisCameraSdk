package org.khj.khjbasiscamerasdk.bean;

/**
 * Created by ShuRun on 2018/5/26.
 */
public class DeviceInfoBean {
    public String firmwareVersion="9.9.9";//固件版本
    public int sdcardTotal;//SD卡容量
    public int sdcardFree;//剩余容量
    public boolean hasSdcard; //是否有SD卡
    public boolean hasTurnOff=false;//设备已被关闭
    public int deviceType;//设备类型
    public boolean isRecordingMp4;//设备端正在录制mp4
    public boolean isScroll;//是摇头机
    public String currentAudienceNum="";//当前观看视频人数
}
