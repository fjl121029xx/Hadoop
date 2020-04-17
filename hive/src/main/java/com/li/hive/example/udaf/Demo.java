package com.li.hive.example.udaf;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

public class Demo {
    public static void main(String[] args) throws ParseException {

//        2020-01-01 00:00:00	2020-01-08 00:00:00
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Date startTime = sdf.parse("2020-01-01");
        Date endTime = sdf.parse("2020-02-08");
        Calendar startCa = Calendar.getInstance();
        startCa.setTime(startTime);
        Calendar endCa = Calendar.getInstance();
        endCa.setTime(endTime);

        int year = startCa.get(Calendar.YEAR);
        BigDecimal days;
        if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0) {//闰年的判断规则
            days = new BigDecimal(366);
        } else {
            days = new BigDecimal(365);
        }
        LocalDate startDate = LocalDate.of(startCa.get(Calendar.YEAR), startCa.get(Calendar.MONTH) + 1, startCa.get(Calendar.DAY_OF_MONTH));
        LocalDate endDate = LocalDate.of(endCa.get(Calendar.YEAR), endCa.get(Calendar.MONTH) + 1, endCa.get(Calendar.DAY_OF_MONTH));

        BigDecimal between = new BigDecimal(ChronoUnit.DAYS.between(startDate, endDate));
        System.out.println(between);
        System.out.println(days);
        BigDecimal divide = between.divide(days,3, RoundingMode.HALF_UP);
        System.out.println(divide);
    }
}
