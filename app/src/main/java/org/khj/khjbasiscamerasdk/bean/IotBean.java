package org.khj.khjbasiscamerasdk.bean;

public class IotBean {
    private String sn;
    private String pwd;
    private Integer type;
    private Integer subType;
    private String iotName;
    private boolean k_close;
    private int code;
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }


    public boolean isK_close() {
        return k_close;
    }

    public void setK_close(boolean k_close) {
        this.k_close = k_close;
    }



    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getSubType() {
        return subType;
    }

    public void setSubType(Integer subType) {
        this.subType = subType;
    }

    public String getIotName() {
        return iotName;
    }

    public void setIotName(String iotName) {
        this.iotName = iotName;
    }
}
