package org.khj.khjbasiscamerasdk.bean;

import java.util.List;

public class DeviceConfigBean {
    private String sign;

    public List<IotBean> getIotBeanList() {
        return iotBeanList;
    }

    private List<IotBean> iotBeanList;

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }



}
