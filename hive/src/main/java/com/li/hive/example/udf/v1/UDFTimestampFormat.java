package com.li.hive.example.udf.v1;

import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.hive.ql.exec.UDFArgumentException;

import java.sql.Timestamp;
import java.util.Calendar;


@Description(name = "udftimestampformat",
        value = "_FUNC_(timestamp, format) - " +
                "date:timestamp  , day options[y,yq,ym,yw,ymd,yh]")

public class UDFTimestampFormat extends UDF {

    public String evaluate(Timestamp date, String format) throws UDFArgumentException {

        if (date == null) {
            throw new RuntimeException("date is null , "+date);
        }

        Calendar ca = Calendar.getInstance();
        ca.setTime(date);

        String result = "";
        switch (format) {
            case "y":
                result = ca.get(Calendar.YEAR) + "年";
                break;
            case "yq":
                int m = ca.get(Calendar.MONTH);
                if (m < 3) {
                    result = ca.get(Calendar.YEAR) + "年1季度";
                    break;
                } else if (m < 6) {
                    result = ca.get(Calendar.YEAR) + "年2季度";
                    break;
                } else if (m < 9) {
                    result = ca.get(Calendar.YEAR) + "年3季度";
                    break;
                } else {
                    result = ca.get(Calendar.YEAR) + "年4季度";
                    break;
                }
            case "ym": {
                String mint = Integer.toString((ca.get(Calendar.MONTH) + 1));
                if (mint.length() < 2) {
                    mint = "0" + mint;
                }

                result = String.format("%d年%s月", ca.get(Calendar.YEAR), mint);
                break;
            }
            case "yw":
                result = String.format("%d年%d周", ca.get(Calendar.YEAR), ca.get(Calendar.WEEK_OF_YEAR));
                break;
            case "ymd":
                String mon = Integer.toString((ca.get(Calendar.MONTH) + 1));
                String d = Integer.toString(ca.get(Calendar.DAY_OF_MONTH));
                if (mon.length() < 2) {
                    mon = "0" + mon;
                }
                if (d.length() < 2) {
                    d = "0" + d;
                }
                result = String.format("%d年%s月%s日", ca.get(Calendar.YEAR), mon, d);
                break;
            case "ymdh":
                String mond = Integer.toString((ca.get(Calendar.MONTH) + 1));
                String day = Integer.toString(ca.get(Calendar.DAY_OF_MONTH));
                if (mond.length() < 2) {
                    mond = "0" + mond;
                }
                if (day.length() < 2) {
                    day = "0" + day;
                }
                result = String.format("%d年%s月%s日%d时", ca.get(Calendar.YEAR), mond, day, ca.get(Calendar.HOUR_OF_DAY));
                break;
            default:
                throw new RuntimeException("nonexistent dimen_mode. [y,yq,ym,yw,ymd,ymdh]]");

        }
        return result;
    }
}
