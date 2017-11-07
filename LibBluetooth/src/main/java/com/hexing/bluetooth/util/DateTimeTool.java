package com.hexing.bluetooth.util;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by caibinglong
 * on 2017/8/2.
 */

public class DateTimeTool {
    private final static String TAG = DateTimeTool.class.getName();
    public final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public final static SimpleDateFormat sdfDay = new SimpleDateFormat("yyyy-MM-dd");
    public final static SimpleDateFormat sdfMonth = new SimpleDateFormat("yyyy-MM");

    /**
     * 时间格式 04:12
     *
     * @param actionTime 动作时间
     * @return long
     */
    public static String getTimestamp(String actionTime) {
        String[] arr = actionTime.split(":");
        long action = Integer.parseInt(arr[0]) * 60 * 60 * 1000 +
                Integer.parseInt(arr[1]) * 60 * 1000;
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int minute = Calendar.getInstance().get(Calendar.MINUTE);
        int second = Calendar.getInstance().get(Calendar.SECOND);
        long now = hour * 60 * 60 * 1000 + minute * 60 * 1000 + second * 1000;
        if (action < now) {
            action = action + 24 * 60 * 60 * 1000;
        }
        long diff = action - now;
        return convertMillis2Time(diff);
    }

    /**
     * 一分钟 刷新一次时间
     *
     * @param actionTime 定时任务时间
     * @return 减去一分钟
     */
    public static String getLessTime(String actionTime) {
        String[] arr = actionTime.split(":");
        try {
            long action = Integer.parseInt(arr[0]) * 60 * 60 * 1000 + Integer.parseInt(arr[1]) * 60 * 1000 +
                    Integer.parseInt(arr[2]) * 1000;
            action = action - 1000;
            return convertMillis2Time(action);
        } catch (NumberFormatException ex) {
            Log.e(TAG, "getLessTime函数-》" + actionTime);
        }
        return "00:00:00";
    }

    /**
     * 转换毫秒到具体时间, 小时:分钟:秒
     * 参考: http://stackoverflow.com/questions/625433/how-to-convert-milliseconds-to-x-mins-x-seconds-in-java
     *
     * @param millis 毫秒
     * @return 时间字符串
     */
    public static String convertMillis2Time(long millis) {
        int seconds = (int) (millis / 1000) % 60;
        int minutes = (int) ((millis / (1000 * 60)) % 60);
        int hours = (int) ((millis / (1000 * 60 * 60)) % 24);
        StringBuilder timestamp = new StringBuilder();
        timestamp.append(hours <= 9 ? "0" + String.valueOf(hours) : String.valueOf(hours));
        timestamp.append(":");
        timestamp.append(minutes <= 9 ? "0" + String.valueOf(minutes) : String.valueOf(minutes));
        timestamp.append(":");
        timestamp.append(seconds <= 9 ? "0" + String.valueOf(seconds) : String.valueOf(seconds));
        return timestamp.toString();
    }

    public static String getTime() {
        Date curDate = new Date(System.currentTimeMillis());
        return sdf.format(curDate);
    }

    /**
     * 根据年 月 获取对应的月份 天数
     */
    public static int getDaysByYearMonth(int year, int month) {
        Calendar a = Calendar.getInstance();
        a.set(Calendar.YEAR, year);
        a.set(Calendar.MONTH, month - 1);
        a.set(Calendar.DATE, 1);
        a.roll(Calendar.DATE, -1);
        int maxDate = a.get(Calendar.DATE);
        return maxDate;
    }

    /**
     * 获取日期 yyyy-mm-dd
     *
     * @return yyyy-mm-dd
     */
    public static String getDayTime() {
        Calendar nowDate = Calendar.getInstance();
        nowDate.setTime(new Date());
        return sdfDay.format(nowDate.getTime());
    }

    /**
     * 获取当前月 格式 yyyy-mm
     *
     * @return yyyy-mm
     */
    public static String getMonthTime() {
        Calendar nowDate = Calendar.getInstance();
        nowDate.setTime(new Date());
        return sdfMonth.format(nowDate.getTime());
    }

    /**
     * string time 转换 long
     *
     * @param time string
     * @param sdf 格式 yyyy-mm-dd  yyyy-mm  yyyy-MM-dd HH:mm:ss
     * @return long
     */
    public static long getLongTimeByString(String time, SimpleDateFormat sdf) {
        Calendar fistDate = Calendar.getInstance();
        try {
            Date date = sdf.parse(time);
            fistDate.setTime(date);
            return fistDate.getTimeInMillis();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0L;
    }

    /**
     * 获取日期 yyyy-mm-dd
     *
     * @return yyyy-mm-dd
     */
    public static String getNowTime() {
        Calendar nowDate = Calendar.getInstance();
        nowDate.setTime(new Date());
        return sdf.format(nowDate.getTime());
    }

    // strTime要转换的String类型的时间
    // formatType时间格式
    // strTime的时间格式和formatType的时间格式必须相同
    public static long stringToLong(String strTime, String formatType)
            throws ParseException {
        Date date = stringToDate(strTime, formatType); // String类型转成date类型
        if (date == null) {
            return 0;
        } else {
            long currentTime = dateToLong(date); // date类型转成long类型
            return currentTime;
        }
    }

    // strTime要转换的string类型的时间，formatType要转换的格式yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日
    // HH时mm分ss秒，
    // strTime的时间格式必须要与formatType的时间格式相同
    public static Date stringToDate(String strTime, String formatType)
            throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(formatType);
        Date date = null;
        date = formatter.parse(strTime);
        return date;
    }

    // currentTime要转换的long类型的时间
    // formatType要转换的时间格式yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日 HH时mm分ss秒
    public static Date longToDate(long currentTime, String formatType)
            throws ParseException {
        Date dateOld = new Date(currentTime); // 根据long类型的毫秒数生命一个date类型的时间
        String sDateTime = dateToString(dateOld, formatType); // 把date类型的时间转换为string
        Date date = stringToDate(sDateTime, formatType); // 把String类型转换为Date类型
        return date;
    }

    // date要转换的date类型的时间
    public static long dateToLong(Date date) {
        return date.getTime();
    }

    // formatType格式为yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日 HH时mm分ss秒
    // data Date类型的时间
    public static String dateToString(Date data, String formatType) {
        return new SimpleDateFormat(formatType).format(data);
    }

    /**
     * 获取时间差
     *
     * @param startTime 开始时间
     * @param endTime   比较的第二个时间
     * @return long
     */
    public static long getTimeTimestamp(String startTime, String endTime) {
        try {
            Date d1 = sdf.parse(startTime);
            Date d2 = sdf.parse(endTime);
            return d1.getTime() - d2.getTime();
        } catch (Exception e) {
        }
        return 0;
    }
}
