package com.mmall.util;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;

public class DateTimeUtil {

    //使用工具joda-time  需在pom文件导入依赖
    //JSON传递数据时是需要将date > str

    public static final String STANDARD_FORMAT = "yyyy-MM-dd HH:mm:ss";


    //str >> Date
    public static Date strToDate(String dateTimeStr,String formatStr){
        //配置格式
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(formatStr);
        //joda-time 将日期按指定格式转为datetime
        DateTime dateTime = dateTimeFormatter.parseDateTime(dateTimeStr);
        //使用toDate方法转换成Util包的date类型
        return dateTime.toDate();
    }

    //Date >> str
    public static String dateToStr(Date date,String formatStr){
        if(date == null){
            return StringUtils.EMPTY;
        }
        DateTime dateTime = new DateTime(date);
        return dateTime.toString(formatStr);
    }

    /*
    按默认格式
     */

    //str >> Date
    public static Date strToDate(String dateTimeStr){
        //配置格式
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(STANDARD_FORMAT);
        //joda-time 将日期按指定格式转为datetime
        DateTime dateTime = dateTimeFormatter.parseDateTime(dateTimeStr);
        //使用toDate方法转换成Util包的date类型
        return dateTime.toDate();
    }

    //Date >> str
    public static String dateToStr(Date date){
        if(date == null){
            return StringUtils.EMPTY;
        }
        DateTime dateTime = new DateTime(date);
        return dateTime.toString(STANDARD_FORMAT);
    }
}
