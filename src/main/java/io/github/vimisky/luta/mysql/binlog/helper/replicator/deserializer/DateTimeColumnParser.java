package io.github.vimisky.luta.mysql.binlog.helper.replicator.deserializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

//java.sql.Timestamp, 是java.util.Date的子类

/**
 * 因为binlog connector设置了deserialize的属性，所以时间字段都会转换为Long类型。
 *         eventDeserializer.setCompatibilityMode(
 *                 EventDeserializer.CompatibilityMode.DATE_AND_TIME_AS_LONG,
 *                 EventDeserializer.CompatibilityMode.CHAR_AND_BINARY_AS_BYTE_ARRAY
 *         );
 * */

/**
 * 关于时区
 * https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/TimeZone.html
 * TimeZone tz = TimeZone.getTimeZone("America/Los_Angeles"); 内置时区ID，America/Los_Angeles. 可以通过TimeZone.getAvailableIDs()获取所有支持的时区
 * TimeZone tz = TimeZone.getTimeZone("GMT+0800"); 自定义时区ID
 * TimeZone tz = TimeZone.getTimeZone("GMT+08:00"); 自定义时区ID
 * */
/**关于Pattern
 * https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html
 * */
public class DateTimeColumnParser {
    private static final Logger logger = LoggerFactory.getLogger(DateTimeColumnParser.class);

    public static Integer yearToInteger(Serializable data) {
        if (data == null)
            return null;
        BinlogRowDataDeserializer.printColumnInstanceOf(data);
        return (Integer) data;
    }

    public static String yearToString(Serializable data) {
        if (data == null)
            return null;
        String ret = "";
        DateTimeFormatter DAY_FORMATTER = DateTimeFormatter.ofPattern("yyyy");
        Year year = (Year) data;
        ret = year.format(DAY_FORMATTER);
        return ret;
    }


    public static Long anyToLong(Serializable data) {

        if (data == null)
            return null;

        return (Long) data;
    }

    public static Date anyToDate(Serializable data) {

        if (data == null)
            return null;

        return new Date((Long) data);
    }

    public static String dateToString(Serializable data){
        if (data == null)
            return null;
        String ret = "";
        DateTimeFormatter DAY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = (LocalDate) data;
        ret = localDate.format(DAY_FORMATTER);
        return ret;
    }

    public static String timeToString(Serializable data){
        if (data == null)
            return null;
        String ret = "";
        DateTimeFormatter DAY_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
        Duration duration = (Duration) data;
        ret = duration.toHoursPart() + ":" + duration.toMinutesPart() + ":" + duration.toSecondsPart();
        return ret;
    }

    public static String timestampToString(Serializable data, String columnType){
        if (data == null)
            return null;
        String ret = "";
        ZonedDateTime zoneDateTime = (ZonedDateTime) data;
        LocalDateTime localDateTime = zoneDateTime.toLocalDateTime();
        DateTimeFormatter DAY_FORMATTER = null;

        switch (columnType) {
            case "timestamp":
                DAY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                ret = localDateTime.format(DAY_FORMATTER);
                break;
            case "timestamp(1)":
                DAY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
                ret = localDateTime.format(DAY_FORMATTER);
                break;
            case "timestamp(2)":
                DAY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SS");
                ret = localDateTime.format(DAY_FORMATTER);
                break;
            case "timestamp(3)":
                DAY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
                ret = localDateTime.format(DAY_FORMATTER);
                break;
            case "timestamp(4)":
                DAY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSS");
                ret = localDateTime.format(DAY_FORMATTER);
                break;
            case "timestamp(5)":
                DAY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSS");
                ret = localDateTime.format(DAY_FORMATTER);
                break;
            case "timestamp(6)":
                DAY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
                ret = localDateTime.format(DAY_FORMATTER);
                break;
            default:
                ret = "类型" + columnType + "无法处理";
                break;
        }

        return ret;
    }

    public static String anyToString(Serializable data, String timezoneID) {

        if (data == null)
            return null;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (timezoneID != null) {
            dateFormat.setTimeZone(TimeZone.getTimeZone(timezoneID));
        } else {
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        }
        return dateFormat.format(new Date((Long) data));
    }

    public static String datetimeToString(Serializable data, String columnType) {

        if (data == null)
            return null;

        String ret = null;
        DateTimeFormatter DAY_FORMATTER = null;
        LocalDateTime dataLocalDateTime = (LocalDateTime)data;

        switch (columnType) {
            case "datetime":
                DAY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                ret = dataLocalDateTime.format(DAY_FORMATTER);
                break;
            case "datetime(1)":
                DAY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
                ret = dataLocalDateTime.format(DAY_FORMATTER);
                break;
            case "datetime(2)":
                DAY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SS");
                ret = dataLocalDateTime.format(DAY_FORMATTER);
                break;
            case "datetime(3)":
                DAY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
                ret = dataLocalDateTime.format(DAY_FORMATTER);
                break;
            case "datetime(4)":
                DAY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSS");
                ret = dataLocalDateTime.format(DAY_FORMATTER);
                break;
            case "datetime(5)":
                DAY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSS");
                ret = dataLocalDateTime.format(DAY_FORMATTER);
                break;
            case "datetime(6)":
                DAY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
                ret = dataLocalDateTime.format(DAY_FORMATTER);
                break;
            default:
                ret = "类型" + columnType + "无法处理";
                break;
        }

        return ret;
    }

}
