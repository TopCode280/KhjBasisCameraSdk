package org.khj.khjbasiscamerasdk.utils;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Author: nanchen
 * Email: liushilin520@foxmail.com
 * Date: 2017-04-14  10:48
 */

public class TimeUtil {

    public static String dateFormat(String timestamp) {
        if (timestamp == null) {
            return "unknown";
        }
        @SuppressLint("SimpleDateFormat") SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");

        try {
            Date date = inputFormat.parse(timestamp);
            return outputFormat.format(date);
        } catch (ParseException e) {
            return "unknown";
        }
    }

    /**
     * ��õ�ǰʱ��
     *
     * @return 2011-06-07
     */
    public static String getCurrDate() {
        Date aDate = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = formatter.format(aDate);
        return formattedDate;


    }

    /**
     * 安卓视频播放器不支持"yyyy-MM-dd HH:mm:ss"文件名
     *
     * @return
     */
    public static String getCurrDateTime() {
        Date aDate = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_");
        String formattedDate = formatter.format(aDate);
        return formattedDate;
    }

    @SuppressLint("SimpleDateFormat")
    public static String getCurrDateTime5() {
        Date aDate = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
        String formattedDate = formatter.format(aDate);
        return formattedDate;
    }

    @SuppressLint("SimpleDateFormat")
    public static String getCurrDateTime(String format) {
        Date aDate = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        String formattedDate = formatter.format(aDate);
        return formattedDate;
    }

    @SuppressLint("SimpleDateFormat")
    public static String getCurrDateTime(long time) {
        Date aDate = new Date(time);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = formatter.format(aDate);
        return formattedDate;
    }

    @SuppressLint("SimpleDateFormat")
    public static String getCurrDateTime2(long time) {
        Date aDate = new Date(time);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
        String formattedDate = formatter.format(aDate);
        return formattedDate;
    }

    @SuppressLint("SimpleDateFormat")
    public static String getCurrDateTime2() {
        Date aDate = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        String formattedDate = formatter.format(aDate);
        return formattedDate;
    }


    /**
     * 将"yyyy_MM_dd_HH_mm_ss"格式的时间转化为Long
     *
     * @param date
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    public static long DateTime2Long(String date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        try {
            Date time = format.parse(date);
            return time.getTime();
        } catch (Exception e) {
            return 0;
        }
    }

    @SuppressLint("SimpleDateFormat")
    public static String getCurrDateTime3() {
        Date aDate = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        String formattedDate = formatter.format(aDate);
        return formattedDate;
    }

    @SuppressLint("SimpleDateFormat")
    public static String ConvertDate(String arg0) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.valueOf(arg0));
        return formatter.format(calendar.getTime());

    }

    @SuppressLint("SimpleDateFormat")
    public static String getDateTime(long time) {
        Date aDate = new Date(time);
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        String formattedDate = formatter.format(aDate);
        return formattedDate;
    }

    /**
     * 毫秒转换成小时
     */
    public static String ms2HMS(int _ms) {
        String HMStime;
        _ms /= 1000;
        int hour = _ms / 3600;
        int mint = (_ms % 3600) / 60;
        int sed = _ms % 60;
        String hourStr = String.valueOf(hour);
        if (hour < 10) {
            hourStr = "0" + hourStr;
        }
        String mintStr = String.valueOf(mint);
        if (mint < 10) {
            mintStr = "0" + mintStr;
        }
        String sedStr = String.valueOf(sed);
        if (sed < 10) {
            sedStr = "0" + sedStr;
        }
        HMStime = hourStr + ":" + mintStr + ":" + sedStr;
        return HMStime;
    }

    /**
     * 毫秒转换成标准时间
     */
    public static String ms2Date(long _ms) {
        Date date = new Date(_ms);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return format.format(date);
    }

    public static String ms2DateOnlyDay(long _ms) {
        Date date = new Date(_ms);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return format.format(date);
    }

    /**
     * 标准时间转换成时间戳
     */
    @SuppressLint("SimpleDateFormat")
    public static long date2Long(String _data) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = format.parse(_data);
            return date.getTime();
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 标准时间转换成时间戳
     */
    @SuppressLint("SimpleDateFormat")
    public static long date2Long(String _data, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        try {
            Date date = simpleDateFormat.parse(_data);
            return date.getTime();
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 标准时间转换
     */
    @SuppressLint("SimpleDateFormat")
    public static String Date2String(Date data) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {

            return format.format(data);
        } catch (Exception e) {
            return "";
        }
    }

    @SuppressLint("SimpleDateFormat")
    public static String Date2String(Date data, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        try {

            return simpleDateFormat.format(data);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 把date转换为10:20格式
     */
    @SuppressLint("SimpleDateFormat")
    public static String date2Hour(Date data) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        try {

            return format.format(data);
        } catch (Exception e) {
            return "转换异常";
        }
    }

    /**
     * 标准时间转换
     */
    public static long String2long(String data) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date time = format.parse(data);
            return time.getTime();
        } catch (Exception e) {
            return 0;
        }
    }

    public static long String2long(String data, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        try {
            Date time = simpleDateFormat.parse(data);
            return time.getTime();
        } catch (Exception e) {
            return 0;
        }
    }

    public static String long2String(long time, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        try {
            Date date = new Date(time);
            String s = simpleDateFormat.format(date);
            return s;
        } catch (Exception e) {
            return "";
        }
    }

    public static String getToday() {
        SimpleDateFormat ToDay = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return ToDay.format(new Date());
    }

    public static String getTodaymm() {
        SimpleDateFormat ToDay = new SimpleDateFormat("MM 月 dd 日 HH 时 mm 分");
        return ToDay.format(new Date());
    }

    public static String getToYdaymm() {
        SimpleDateFormat ToDay = new SimpleDateFormat("YYYY年 MM 月 dd 日 HH 时 mm 分");
        return ToDay.format(new Date());
    }

    public static String getTodaymm(Long timeL) {
        SimpleDateFormat ToDay = new SimpleDateFormat("MM 月 dd 日 HH 时 mm 分");
        return ToDay.format(timeL);
    }

    public static String getToYdaymm(Long timeL) {
        SimpleDateFormat ToDay = new SimpleDateFormat("YYYY年 MM 月 dd 日 HH 时 mm 分");
        return ToDay.format(timeL);
    }

    public static long dateToStamp(String s) throws ParseException {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = simpleDateFormat.parse(s);
        long ts = date.getTime();
        return ts;
    }

    public static Long getTimesmorning() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    @SuppressLint("SimpleDateFormat")
    public static Long getLongTime(String time) {
        String format = "yyyy-MM-dd HH:mm:ss";
        Date aDate = new Date(time);
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        String formattedDate = formatter.format(aDate);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        try {
            Date date = simpleDateFormat.parse(formattedDate);
            return date.getTime();
        } catch (Exception e) {
            return 0L;
        }
    }

    /**
     * 将字符串转位日期类型
     *
     * @param sdate
     * @return
     */
    public static Date toDate(String sdate) {
        try {
            return dateFormater.get().parse(sdate);
        } catch (ParseException e) {
            return null;
        }
    }

    private final static ThreadLocal<SimpleDateFormat> dateFormater = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd");
        }
    };

    private final static ThreadLocal<SimpleDateFormat> dateFormater2 = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd");
        }
    };

    /**
     * 获取今天往后7天的日期
     */
    public static String getSevendate() {
        String mYear; // 当前年
        String mMonth; // 月
        String mDay;
        List<String> dates = new ArrayList<String>();
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));

        for (int i = 0; i < 7; i++) {
            mYear = String.valueOf(c.get(Calendar.YEAR));// 获取当前年份
            mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);// 获取当前月份
            mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH) + i);// 获取当前日份的日期号码
            if (Integer.parseInt(mDay) > MaxDayFromDay_OF_MONTH(Integer.parseInt(mYear), (i + 1))) {
                mDay = String.valueOf(MaxDayFromDay_OF_MONTH(Integer.parseInt(mYear), (i + 1)));
            }
            String date = mYear + "-" + mMonth + "-" + mDay;
            dates.add(date);
        }
        if (dates.size() == 7) {
            return dates.get(6);
        } else {
            return null;
        }
    }

    /**
     * 得到当年当月的最大日期
     **/
    public static int MaxDayFromDay_OF_MONTH(int year, int month) {
        Calendar time = Calendar.getInstance();
        time.clear();
        time.set(Calendar.YEAR, year);
        time.set(Calendar.MONTH, month - 1);//注意,Calendar对象默认一月为0                 
        int day = time.getActualMaximum(Calendar.DAY_OF_MONTH);//本月份的天数
        return day;
    }
}
