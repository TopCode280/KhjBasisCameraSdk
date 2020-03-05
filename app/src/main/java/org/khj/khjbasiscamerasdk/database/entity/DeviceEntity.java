package org.khj.khjbasiscamerasdk.database.entity;

import com.google.gson.Gson;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Transient;
import org.khj.khjbasiscamerasdk.bean.DeviceConfigBean;

import java.io.Serializable;

@Entity(indexes = {
        @Index(value = "deviceUid , userId", unique = true)
})
public class DeviceEntity implements Serializable {
    private static final long serialVersionUID = 1L;


    /**
     * deviceInfoId : 1001 设备序列号，作为设备唯一的标识
     * deviceUid : L67HLF3NWXH866DU111A
     * deviceName :
     * deviceAccount : admin
     * devicePwd : 888888
     * deviceVersion : V1.0
     * deviceConfigs :
     * deviceRemark ://分享设备的权限标志“0”是仅观看，“1”是能控制和看回放
     * deviceStatus : 1
     * ownFlag : 0
     * createdDate : 1523808000000
     * updatedDate : 1523808000000
     * ptz:1      摇头机为1其余为0
     */
    @Id(autoincrement = true)
    private Long id;
    private String deviceInfoId;
    private String deviceUid;
    private String deviceName;
    private String deviceAccount;
    private String devicePwd;
    private String deviceVersion;
    private String deviceConfigs;
    private String deviceRemark = "0";//分享权限
    private String deviceStatus;
    private String ownFlag;
    private String createdDate;
    private String updatedDate;
    private Long userId;//外联用户
    private int order = -1;//用户排序
    private boolean isAdmin = true;//是否是管理员设备
    private String background;//摄像头背景图地址
    private String reserve1;//预留字段，
    private String reserve2;//预留字段
    private String reserve3;//预留字段
    private String reserve4;//预留字段
    private int videoPath;//播放录像路径，0是设备SD卡，1是云存储
    private int ptz;
    private int recType = -1;//0是报警录像，1是全天录像
    private int storageTime;//几天报警
    private int status;
    private Long endTime;
    private int isOpenCloud;

    public long getCloudDeadline() {
        return cloudDeadline;
    }

    public void setCloudDeadline(long cloudDeadline) {
        this.cloudDeadline = cloudDeadline;
    }

    @Transient
    private long cloudDeadline;//当前云存储套餐截止时间

    @Generated(hash = 1449836520)
    public DeviceEntity() {
    }

    @Generated(hash = 353655623)
    public DeviceEntity(Long id, String deviceInfoId, String deviceUid, String deviceName,
                        String deviceAccount, String devicePwd, String deviceVersion, String deviceConfigs,
                        String deviceRemark, String deviceStatus, String ownFlag, String createdDate,
                        String updatedDate, Long userId, int order, boolean isAdmin, String background,
                        String reserve1, String reserve2, String reserve3, String reserve4, int videoPath, int ptz,
                        int recType, int storageTime, int status, Long endTime, int isOpenCloud) {
        this.id = id;
        this.deviceInfoId = deviceInfoId;
        this.deviceUid = deviceUid;
        this.deviceName = deviceName;
        this.deviceAccount = deviceAccount;
        this.devicePwd = devicePwd;
        this.deviceVersion = deviceVersion;
        this.deviceConfigs = deviceConfigs;
        this.deviceRemark = deviceRemark;
        this.deviceStatus = deviceStatus;
        this.ownFlag = ownFlag;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
        this.userId = userId;
        this.order = order;
        this.isAdmin = isAdmin;
        this.background = background;
        this.reserve1 = reserve1;
        this.reserve2 = reserve2;
        this.reserve3 = reserve3;
        this.reserve4 = reserve4;
        this.videoPath = videoPath;
        this.ptz = ptz;
        this.recType = recType;
        this.storageTime = storageTime;
        this.status = status;
        this.endTime = endTime;
        this.isOpenCloud = isOpenCloud;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDeviceInfoId() {
        return this.deviceInfoId;
    }

    public void setDeviceInfoId(String deviceInfoId) {
        this.deviceInfoId = deviceInfoId;
    }

    public String getDeviceUid() {
        return this.deviceUid;
    }

    public void setDeviceUid(String deviceUid) {
        this.deviceUid = deviceUid;
    }

    public String getDeviceName() {
        return this.deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceAccount() {
        return this.deviceAccount;
    }

    public void setDeviceAccount(String deviceAccount) {
        this.deviceAccount = deviceAccount;
    }

    public String getDevicePwd() {
        return this.devicePwd;
    }

    public void setDevicePwd(String devicePwd) {
        this.devicePwd = devicePwd;
    }

    public String getDeviceVersion() {
        return this.deviceVersion;
    }

    public void setDeviceVersion(String deviceVersion) {
        this.deviceVersion = deviceVersion;
    }

    public String getDeviceConfigs() {
        return this.deviceConfigs;
    }

    public void setDeviceConfigs(String deviceConfigs) {
        this.deviceConfigs = deviceConfigs;
    }

    public String getDeviceRemark() {
        return this.deviceRemark;
    }

    public void setDeviceRemark(String deviceRemark) {
        this.deviceRemark = deviceRemark;
    }

    public String getDeviceStatus() {
        return this.deviceStatus;
    }

    public void setDeviceStatus(String deviceStatus) {
        this.deviceStatus = deviceStatus;
    }

    public String getOwnFlag() {
        return this.ownFlag;
    }

    public void setOwnFlag(String ownFlag) {
        this.ownFlag = ownFlag;
    }

    public String getCreatedDate() {
        return this.createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getUpdatedDate() {
        return this.updatedDate;
    }

    public void setUpdatedDate(String updatedDate) {
        this.updatedDate = updatedDate;
    }

    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public int getOrder() {
        return this.order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public boolean getIsAdmin() {
        return this.isAdmin;
    }

    public void setIsAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public String getBackground() {
        return this.background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public String getReserve1() {
        return this.reserve1;
    }

    public void setReserve1(String reserve1) {
        this.reserve1 = reserve1;
    }

    public String getReserve2() {
        return this.reserve2;
    }

    public void setReserve2(String reserve2) {
        this.reserve2 = reserve2;
    }

    public String getReserve3() {
        return this.reserve3;
    }

    public void setReserve3(String reserve3) {
        this.reserve3 = reserve3;
    }

    public String getReserve4() {
        return this.reserve4;
    }

    public void setReserve4(String reserve4) {
        this.reserve4 = reserve4;
    }

    public int getPtz() {
        return this.ptz;
    }

    public void setPtz(int ptz) {
        this.ptz = ptz;
    }

    public int getRecType() {
        return this.recType;
    }

    public void setRecType(int recType) {
        this.recType = recType;
    }

    public int getStorageTime() {
        return this.storageTime;
    }

    public void setStorageTime(int storageTime) {
        this.storageTime = storageTime;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getVideoPath() {
        return this.videoPath;
    }

    public void setVideoPath(int videoPath) {
        this.videoPath = videoPath;
    }

    public Long getEndTime() {
        return this.endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public int getIsOpenCloud() {
        return this.isOpenCloud;
    }

    public void setIsOpenCloud(int isOpenCloud) {
        this.isOpenCloud = isOpenCloud;
    }

    public DeviceConfigBean getDeviceConfigBean() {
        return new Gson().fromJson(deviceConfigs, DeviceConfigBean.class);
    }

    @Override
    public String toString() {
        return "DeviceEntity{" +
                "id=" + id +
                ", deviceInfoId='" + deviceInfoId + '\'' +
                ", deviceUid='" + deviceUid + '\'' +
                ", deviceName='" + deviceName + '\'' +
                ", deviceAccount='" + deviceAccount + '\'' +
                ", devicePwd='" + devicePwd + '\'' +
                ", deviceVersion='" + deviceVersion + '\'' +
                ", deviceConfigs='" + deviceConfigs + '\'' +
                ", deviceRemark='" + deviceRemark + '\'' +
                ", deviceStatus='" + deviceStatus + '\'' +
                ", ownFlag='" + ownFlag + '\'' +
                ", createdDate='" + createdDate + '\'' +
                ", updatedDate='" + updatedDate + '\'' +
                ", userId=" + userId +
                ", order=" + order +
                ", isAdmin=" + isAdmin +
                ", background='" + background + '\'' +
                ", reserve1='" + reserve1 + '\'' +
                ", reserve2='" + reserve2 + '\'' +
                ", reserve3='" + reserve3 + '\'' +
                ", reserve4='" + reserve4 + '\'' +
                ", videoPath=" + videoPath +
                ", ptz=" + ptz +
                ", recType=" + recType +
                ", storageTime=" + storageTime +
                ", status=" + status +
                '}';
    }
}
