package com.allen.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DateUtil {

    /**
     * 通过时间戳获取本地时间
     * @param timestamp 时间戳
     * @return LocalDateTime本地时间
     */
    public static LocalDateTime getDateTimeOfTimestamp(long timestamp) {
        Instant instant = Instant.ofEpochMilli(timestamp);
        ZoneId zone = ZoneId.systemDefault();
        return LocalDateTime.ofInstant(instant, zone);
    }

    /**
     * 将本地时间转换成时间戳
     * @param localDateTime 本地时间
     * @return 时间戳
     */
    public static long getTimestampOfDateTime(LocalDateTime localDateTime) {
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDateTime.atZone(zone).toInstant();
        return instant.toEpochMilli();
    }

    /**
     * 将字符串时间转换成LocalDateTime时间对象
     * @param time 字符串时间
     * @param format 时间格式
     * @return 本地时间LocalDateTime对象
     */
    public static LocalDateTime parseStringToDateTime(String time, String format) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern(format);
        return LocalDateTime.parse(time, df);
    }

    /**
     * 将字符串时间转换成时间戳
     * @param stringDate 字符串时间
     * @return long型时间戳（秒级）
     */
    public static long toSecond(String stringDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        try {
            return TimeUnit.MILLISECONDS.toSeconds(sdf.parse(stringDate).getTime());
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static long toMillis(String stringDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        try {
            return TimeUnit.MILLISECONDS.toMillis(sdf.parse(stringDate).getTime());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将字符串二进制转换成对应的long型数值
     * @param stringByte 字符串二进制
     * @return long型数值
     */
    public static long parseStringByteToLong(String stringByte) {
       return Long.parseLong(stringByte, 2);
    }

    /**
     * Calendar时间计算
     */
    public static void dateCalculate() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(cal.getTimeInMillis());
        System.out.println(toSecond(date));
//        cal.add(Calendar.DATE, 1);
//        System.out.println(cal.getTimeInMillis());
        System.out.println(cal.getTimeInMillis()/1000);

    }

    public static String currentTime() {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        return df.format(new Date());
    }

    public static void main(String[] args) {
//        System.out.println(getTimestampOfDateTime(parseStringToDateTime("2019-07-01 00:00:00.000","yyyy-MM-dd HH:mm:ss.SSS")));
//        System.out.println(toSecond("2019-07-01 00:00:00.000"));
//        System.out.println(parseStringByteToLong("0110000000000000000000000000000000000000000000000000000000000000"));
//        System.out.println(6917529027641081856L >> 61);
//        System.out.println(1 % 2);
        System.out.println(Long.toBinaryString(6915495343449178113L));
//        System.out.println((4683374623244419073L & 6917529027641081856L) >> 62); // 等于1
//        dateCalculate();
    }
}
