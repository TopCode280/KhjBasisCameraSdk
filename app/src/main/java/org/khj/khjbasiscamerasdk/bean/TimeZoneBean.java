package org.khj.khjbasiscamerasdk.bean;

/**
 * Created by ShuRun on 2018/5/5.
 */
public class TimeZoneBean {
    private String time;
    private boolean isSelectedTime;

    public TimeZoneBean(String time) {
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isSelectedTime() {
        return isSelectedTime;
    }

    public void setSelectedTime(boolean selectedTime) {
        isSelectedTime = selectedTime;
    }
}
