package org.khj.khjbasiscamerasdk.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * 时间工具
 * Created by HDL on 2017/7/25.
 */

public class DateUtils {
    private static SimpleDateFormat format = new SimpleDateFormat("HH:mm");

    /**
     * 获取当前时间的起点（00:00:00）
     *
     * @param currentTime
     * @return
     */
    public static long getTodayStart(long currentTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(currentTime));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTimeInMillis();
    }

    /**
     * 获取当前时间的小时值
     *
     * @param currentTime
     * @return
     */
    public static int getHour(long currentTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(currentTime));
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 获取当前时间的分钟值
     *
     * @param currentTime
     * @return
     */
    public static int getMinute(long currentTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(currentTime));
        return calendar.get(Calendar.MINUTE);
    }

    /**
     * 获取当前时间的分钟值
     *
     * @param currentTime
     * @return
     */
    public static int getSecond(long currentTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(currentTime));
        return calendar.get(Calendar.SECOND);
    }

    /**
     * 获取当前时间的分钟值+毫秒值
     *
     * @param currentTime
     * @return
     */
    public static int getMinuteMillisecond(long currentTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(currentTime));
        return calendar.get(Calendar.MINUTE) * 60 * 1000 + calendar.get(Calendar.SECOND) * 1000 + calendar.get(Calendar.MILLISECOND);
    }

    /**
     * 获取当前时间的毫秒值
     *
     * @param currentTime
     * @return
     */
    public static int getMillisecond(long currentTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(currentTime));
        return calendar.get(Calendar.MILLISECOND);
    }

    /**
     * 获取当前时间的终点（23:59:59）
     *
     * @param currentTime
     * @return
     */
    public static long getTodayEnd(long currentTime) {
        return getTodayStart(currentTime) + 24 * 60 * 60 * 1000L - 1000;
    }

    /**
     * 获取指定时间的年月日
     *
     * @param currentTime
     * @return
     */
    public static String getDateByCurrentTiem(long currentTime) {
        return new SimpleDateFormat("yyyy-MM-dd").format(currentTime);
    }
    /**
     * 获取指定时间的年月日
     *
     * @param currentTime
     * @return
     */
    public static String getDateByCurrentTimeMills2(long currentTime) {
        return new SimpleDateFormat("yyyy_MM_dd").format(currentTime);
    }

    /**
     * 获取指定时间的年月日
     *
     * @param currentTime
     * @return
     */
    public static String getDateTime(long currentTime) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(currentTime);
    }
    public static String getDateTime2(long currentTime) {
        return new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(currentTime);
    }

    /**
     * 根据下标获取HH:mm格式的时间
     *
     * @param timeIndex
     * @return
     */
    public static String getHourMinute(int timeIndex) {
//        return format.format(timeIndex * 60 * 1000 + 16 * 60 * 60 * 1000);

//        return format.format(timeIndex * 60 * 1000 + 16 * 60 * 60 * 1000);
        TimeZone aDefault = TimeZone.getDefault();
        int rawOffset = aDefault.getRawOffset();
//        KLog.i("时区偏移："+rawOffset+"*"+rawOffset/(60*60*1000));
        return format.format(timeIndex * 60 * 1000 -rawOffset);
    }

    /**
     * 获取指定日期的时间（如：10:11:12）
     *
     * @param currentTime
     * @return
     */
    public static String getTime(long currentTime) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        return format.format(new Date(currentTime));
    }
    /**
     * 获取时间（如:11:12）
     *
     * @param currentTime
     * @return
     */
    public static String getMinuteTime(long currentTime) {
        SimpleDateFormat format = new SimpleDateFormat("mm:ss");
        return format.format(new Date(currentTime));
    }

    /**
     * 根据当前的秒数计算时间
     *
     * @param currentSecond
     * @return
     */
    public static String getTimeByCurrentSecond(long currentSecond) {
        int seconds= (int) (currentSecond%60);
        currentSecond = currentSecond / 60;
        int minute = (int) (currentSecond % 60);
        int hour = (int) (currentSecond / 60);
        if (hour >= 24) {
            hour = hour % 24;
        }
        return (hour < 10 ? "0" + hour : hour) + ":" + (minute < 10 ? "0" + minute : minute)+ ":" + (seconds < 10 ? "0" + seconds : seconds);
    }

    /**
     * 根据当前的秒数计算时间
     *
     * @param currentSecond
     * @return
     */
    public static String getTimeByCurrentHours(int currentSecond) {
        currentSecond = currentSecond * 10;
        currentSecond = currentSecond / 60;
        int minute = currentSecond % 60;
        int hour = currentSecond / 60;
        if (hour >= 24) {
            hour = hour % 24;
        }
        return (hour < 10 ? "0" + hour : hour) + ":" + (minute < 10 ? "0" + minute : minute);
    }

    /**
     * 判断时间是否在时间段内
     *
     * @param nowTime
     * @param beginTime
     * @param endTime
     * @return
     */
    public static boolean isCurrentTimeArea(long nowTime, long beginTime, long endTime) {
        return nowTime >= beginTime && nowTime <= endTime;
    }
}
