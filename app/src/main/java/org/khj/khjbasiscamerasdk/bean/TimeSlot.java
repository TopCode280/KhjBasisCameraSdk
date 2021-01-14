package org.khj.khjbasiscamerasdk.bean;

import org.khj.khjbasiscamerasdk.utils.DateUtils;
import java.io.Serializable;

/**
 * 时间段
 * Created by HDL on 2017/9/4.
 */

public class TimeSlot implements Serializable{
    /**
     * 开始时间
     */
    public long startTime;
    /**
     * 结束时间
     */
    public long endTime;
    /**
     * 当前天数的开始时间（凌晨00:00:00）毫秒值
     */
    private long currentDayStartTimeMillis;

    public int getAlarmType() {
        return alarmType;
    }

    public void setAlarmType(int alarmType) {
        this.alarmType = alarmType;
    }

    private int alarmType=1;//1全天录制，2移动侦测，4声音侦测,6移动加声音

    public TimeSlot(long currentDayStartTimeMillis, long startTime, long endTime, int alarmType) {
        this.currentDayStartTimeMillis = currentDayStartTimeMillis;
        this.startTime = startTime;
        this.endTime = endTime;
        this.alarmType = alarmType;
    }
    public TimeSlot(long currentDayStartTimeMillis, long startTime, long endTime) {
        this.currentDayStartTimeMillis = currentDayStartTimeMillis;
        this.startTime = startTime;
        this.endTime = endTime;
    }
    public TimeSlot(long startTime, long endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    /**
     * 获取开始时间.
     * 当天持续秒数---->减去了当前开始时间的毫秒值（eg  00:01:00---->60）
     *
     * @return
     */
    public float getStartTime() {
        if (currentDayStartTimeMillis > startTime) {
            return 0;
        }
        return (startTime - DateUtils.getTodayStart(startTime)) / 1000f;
    }

    public long getStartTimeMillis() {
        if (currentDayStartTimeMillis > startTime) {
            return 0;
        }
        return (startTime - DateUtils.getTodayStart(startTime));
    }


    /**
     * 获取结束时间
     * 当天持续秒数---->减去了当前开始时间的毫秒值（eg  00:01:00---->60）
     *
     * @return
     */
    public float getEndTime() {
        if (currentDayStartTimeMillis + 24 * 60 * 60 * 1000 <= endTime) {
            return 24 * 60 * 60 - 1;
        }
        return (endTime - DateUtils.getTodayStart(endTime)) / 1000f;
    }

    public long getEndTimeMillis() {
        if (currentDayStartTimeMillis + 24 * 60 * 60 * 1000 <= endTime) {
            return (24 * 60 * 60 - 1) * 1000;
        }
        return (endTime - DateUtils.getTodayStart(endTime));
    }

    @Override
    public String toString() {
        return "TimeSlot{" +
                "startTime=" + startTime +
                ", endTime=" + endTime +
                ", currentDayStartTimeMillis=" + currentDayStartTimeMillis +
                ", alarmType=" + alarmType +
                '}';
    }
}
