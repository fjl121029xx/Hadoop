package com.li.time;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.sun.org.apache.xerces.internal.impl.xpath.regex.ParseException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * @description: Java中传入一个时间段，取出该时间段内所有日期的集合
 * @author: fuzongle
 * @Date: 2019-11-22 16:06
 */
public class Demo01 {
    public static void main(String[] args) throws Exception {


//        findDates("20210128", "20210201");
        findDates("2021-01-28", "2021-01-29", "yyyy-MM-dd", "yyyy-MM-dd HH时", Calendar.HOUR);

    }


    public static List<String> findDates(String dBegin, String dEnd, String format1, String format2, int ca) throws ParseException, java.text.ParseException {
        DateFormat tmp = new SimpleDateFormat(format1 + "HHmmss");
        DateFormat format = new SimpleDateFormat(format1);
        DateFormat hformat = new SimpleDateFormat(format2);

        //设置开始时间
        Calendar calBegin = Calendar.getInstance();
        calBegin.setTime(tmp.parse(dBegin + "000000"));

        //设置结束时间
        Calendar calEnd = Calendar.getInstance();
        Date dayEnd = tmp.parse(dEnd + "235959");
        calEnd.setTime(dayEnd);

        //装返回的日期集合容器
        List<String> Datelist = new ArrayList<String>();
        //将第一个月添加里面去
        Datelist.add(hformat.format(calBegin.getTime()));

        // 每次循环给calBegin日期加一天，直到calBegin.getTime()时间等于dEnd
        while (dayEnd.after(calBegin.getTime())) {
            // 根据日历的规则，为给定的日历字段添加或减去指定的时间量
            calBegin.add(ca, 1);
            if (dayEnd.after(calBegin.getTime())) {
                Datelist.add(hformat.format(calBegin.getTime()));
            }

        }

        return Datelist;
    }
}
