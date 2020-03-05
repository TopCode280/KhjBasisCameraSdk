package org.khj.khjbasiscamerasdk.bean;

public class MulticastBean {

    private String uid;
    private String type;
    private String account;
    private int capability; // 能力集合

    public int getCapability() {
        return capability;
    }

    public void setCapability(int capability) {
        this.capability = capability;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }
}
