package org.khj.khjbasiscamerasdk.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class SearchDeviceInfoBean implements Parcelable {

    private String uid;


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }


    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel arg0, int arg1) {
        // TODO Auto-generated method stub
        // 1.必须按成员变量声明的顺序封装数据，不然会出现获取数据出错
        // 2.序列化对象
        arg0.writeString(uid);
    }

    public static final Creator CREATOR = new Creator(){

        @Override
        public SearchDeviceInfoBean createFromParcel(Parcel source) {
            // TODO Auto-generated method stub
            // 必须按成员变量声明的顺序读取数据，不然会出现获取数据出错
            SearchDeviceInfoBean p = new SearchDeviceInfoBean();
            p.setUid(source.readString());
            return p;
        }

        @Override
        public SearchDeviceInfoBean[] newArray(int size) {
            // TODO Auto-generated method stub
            return new SearchDeviceInfoBean[size];
        }
    };
}
