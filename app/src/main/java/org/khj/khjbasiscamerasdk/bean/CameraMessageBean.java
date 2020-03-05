package org.khj.khjbasiscamerasdk.bean;

/**
 * 设备推送消息
 */
public class CameraMessageBean {
    private String data;
    private int type;

    public CameraMessageBean(int type, String data) {
        this.data = data;
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
