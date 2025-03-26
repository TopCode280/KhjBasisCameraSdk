package org.khj.khjbasiscamerasdk.newP2PUtil;

import android.text.TextUtils;

import com.google.gson.Gson;

import org.khj.khjbasiscamerasdk.newP2PUtil.bean.SetCloudAiSwitchBean;

public class GetBufferJsonUtil {

    public int requestCommonId = 0;
    private static GetBufferJsonUtil instance;


    public static GetBufferJsonUtil getInstance() {
        if (instance == null) {
            synchronized (GetBufferJsonUtil.class) {
                if (instance == null) {
                    instance = new GetBufferJsonUtil();
                }
            }
        }
        return instance;
    }

    public int getRequestCommonId() {
        return requestCommonId;
    }

    public int randomRequestId() {
        requestCommonId = (int) ((Math.random() * 9 + 1) * Math.pow(10, 5));
        return requestCommonId;
    }

    // true 拨打电话  false 挂断
    public boolean parseDeviceVideoCall(String devVideoCallStr) {
        if (TextUtils.isEmpty(devVideoCallStr)) {
            return false;
        }
        BaseMethodBean baseMethodBean = new Gson().fromJson(devVideoCallStr, BaseMethodBean.class);
        return (boolean) baseMethodBean.arg;
    }

    // retCode 0成功，-1超时未接，-2APP挂断
    public String getVideoReplyMsg(int retCode) {
        BaseMethodBean baseMethodBean = new BaseMethodBean();
        baseMethodBean.methodName = "deviceVideoCall";
        baseMethodBean.requestId = randomRequestId();
        baseMethodBean.retCode = retCode;
        return new Gson().toJson(baseMethodBean, BaseMethodBean.class);
    }

    // true 拨打 false挂断
    public String getVideoCallMsg(boolean retCode) {
        BaseMethodBean baseMethodBean = new BaseMethodBean();
        baseMethodBean.methodName = "appVideoCall";
        baseMethodBean.requestId = randomRequestId();
        baseMethodBean.arg = retCode;
        return new Gson().toJson(baseMethodBean, BaseMethodBean.class);
    }

    // 设置云端ai开关
    public String setCloudAISwitch(boolean switchStatus) {
        BaseMethodBean baseMethodBean = new BaseMethodBean();
        baseMethodBean.methodName = "setProperty";
        SetCloudAiSwitchBean setCloudAiSwitchBean = new SetCloudAiSwitchBean();
        setCloudAiSwitchBean.propertyName = "cloudAISwitch";
        setCloudAiSwitchBean.propertyValue = switchStatus;
        baseMethodBean.arg = setCloudAiSwitchBean;
        baseMethodBean.requestId = randomRequestId();
        return new Gson().toJson(baseMethodBean, BaseMethodBean.class);
    }
}
