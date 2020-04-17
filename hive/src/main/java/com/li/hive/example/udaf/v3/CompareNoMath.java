package com.li.hive.example.udaf.v3;

import org.apache.hadoop.hive.ql.exec.UDAF;
import org.apache.hadoop.hive.ql.exec.UDAFEvaluator;
import org.apache.log4j.Logger;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CompareNoMath extends UDAF {
    public static Logger logger = Logger.getLogger(CompareNoMath.class);

    public static class MutableAggregationBuffer {
        private static ConcurrentHashMap<Long, Object> PartialResult;
    }

    public static class Evaluator implements UDAFEvaluator {
        MutableAggregationBuffer buffer;

        //初始化函数,map和reduce均会执行该函数,起到初始化所需要的变量的作用
        public Evaluator() {
            buffer = new MutableAggregationBuffer();
            init();
        }

        // 初始化函数间传递的中间变量
        public void init() {
            MutableAggregationBuffer.PartialResult = new ConcurrentHashMap<>();
        }

        //map阶段，返回值为boolean类型，当为true则程序继续执行，当为false则程序退出
        public boolean iterate(List<String> dimensions,
                               String measure, String dateFormat, String key) {
            if (dimensions == null || dimensions.size() == 0) {
                throw new RuntimeException("dimensions is null \r\n" + dimensions.toString());
            }
            Object o = MutableAggregationBuffer.PartialResult.getOrDefault(1, new HashMap<String, String>());
            HashMap<String, String> com = (HashMap<String, String>) o;
            String dimeKey = dimensions.stream().reduce((a, b) -> a + "_" + b).get();
            com.put(dimeKey, measure);
            return true;
        }

        public ConcurrentHashMap<Long, Object> terminatePartial() {
            return MutableAggregationBuffer.PartialResult;
        }

        public boolean merge(Map<String, String> mapOutput) {


            return true;
        }

        public String terminate() {
            return null;
        }
    }

    public static String dayformat(String day, String dimen_mode) {
        Calendar ca = Calendar.getInstance();
        if (day.contains("-")) {
            ca.set(Integer.parseInt(day.substring(0, 4)),
                    Integer.parseInt(day.substring(5, 7)) - 1,
                    Integer.parseInt(day.substring(8, 10)));
        } else {
            if (day.contains("年")) {
                // 年
                ca.set(Integer.parseInt(day.substring(0, 4)), 0, 1);
            } else if (day.contains("年") && day.contains("月") && day.contains("日")) {
                // 年月日
                ca.set(Integer.parseInt(day.substring(0, 4)),
                        Integer.parseInt(day.substring(day.indexOf("年") + 1, day.indexOf("月"))) - 1,
                        Integer.parseInt(day.substring(day.indexOf("月") + 1, day.indexOf("日"))));
            } else if (day.contains("年") && day.contains("月")) {
                // 年月
                ca.set(Integer.parseInt(day.substring(0, 4)),
                        Integer.parseInt(day.substring(day.indexOf("年") + 1, day.indexOf("月"))) - 1,
                        1);
            } else if (day.contains("年") && day.contains("周")) {
                // 年周
                ca.setFirstDayOfWeek(Calendar.MONDAY);
                ca.set(Calendar.YEAR, Integer.parseInt(day.substring(0, 4)));
                ca.set(Calendar.WEEK_OF_YEAR, Integer.parseInt(day.substring(day.indexOf("年") + 1, day.indexOf("周"))));
            } else if (day.contains("年") && day.contains("季度")) {
                // 年季度
                int m = Integer.parseInt(day.substring(day.indexOf("年") + 1, day.indexOf("季度")));
                switch (m) {
                    case 1:
                        m = 1;
                        break;
                    case 2:
                        m = 4;
                        break;
                    case 3:
                        m = 7;
                        break;
                    default:
                        m = 10;
                }
                ca.set(Integer.parseInt(day.substring(0, 4)), m - 1, 1);
            } else {
                ca.set(Integer.parseInt(day.substring(0, 4)), Integer.parseInt(day.substring(4, 6)) - 1, Integer.parseInt(day.substring(6, 8)));
            }

        }

        switch (dimen_mode) {
            case "y":
                return ca.get(Calendar.YEAR) + "年";
            case "yq":
                int m = ca.get(Calendar.MONTH);
                if (m >= 0 && m < 3) {
                    return ca.get(Calendar.YEAR) + "年1季度";
                } else if (m >= 3 && m < 6) {
                    return ca.get(Calendar.YEAR) + "年2季度";
                } else if (m >= 6 && m < 9) {
                    return ca.get(Calendar.YEAR) + "年3季度";
                } else {
                    return ca.get(Calendar.YEAR) + "年4季度";
                }
            case "ym": {
                String mint = Integer.toString(ca.get(Calendar.MONTH) + 1);
                if (mint.length() < 2) {
                    mint = "0" + mint;
                }

                return ca.get(Calendar.YEAR) + "年" + mint + "月";
            }
            case "yw":
                return ca.get(Calendar.YEAR) + "年" + (ca.get(Calendar.WEEK_OF_YEAR)) + "周";
            case "ymd":
                String m2 = Integer.toString(ca.get(Calendar.MONTH) + 1);
                String d = Integer.toString(ca.get(Calendar.DAY_OF_MONTH));
                if (m2.length() < 2) {
                    m2 = "0" + m2;
                }
                if (d.length() < 2) {
                    d = "0" + d;
                }
                return ca.get(Calendar.YEAR) + "年" + m2 + "月" + d + "日";
            default:
                throw new RuntimeException("nonexistent dimen_mode. [y,yq,ym,yw,ymd]]");
        }
    }

    public static String getTime(Calendar ca) {
        String m = Integer.toString(ca.get(Calendar.MONTH) + 1);
        String d = Integer.toString(ca.get(Calendar.DAY_OF_MONTH));
        if (m.length() < 2) {
            m = "0" + m;
        }
        if (d.length() < 2) {
            d = "0" + d;
        }

        return ca.get(Calendar.YEAR) + "" + m + "" + d;
    }

    // 判断是否存在reportdate
    public static Boolean hasRD(String input) {
        String s = "\\d+-\\d+-\\d+ \\d+:\\d+:\\d+";
        Pattern pattern = Pattern.compile(s);
        Matcher ma = pattern.matcher(input);
        return ma.find();
    }

    public static Boolean hasRD2(String input) {
        String s = "\\d+-\\d+-\\d+";
        Pattern pattern = Pattern.compile(s);
        Matcher ma = pattern.matcher(input);
        return ma.find();
    }
}
