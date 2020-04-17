package com.li.hive.example.udaf.v1;

import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDAF;
import org.apache.hadoop.hive.ql.exec.UDAFEvaluator;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Description(name = "compare", value = "_FUNC_(dimension,measure,dimen_mode,time_diff_type,measure_name) - Returns the mean of a set of numbers")
public class Compare extends UDAF {

    public static Logger logger = Logger.getLogger(Compare.class);

    public static class ArgsState {
        private Map<String, String> cat;
    }

    public static class Evaluator implements UDAFEvaluator {
        ArgsState argsState;

        //初始化函数,map和reduce均会执行该函数,起到初始化所需要的变量的作用
        public Evaluator() {
            argsState = new ArgsState();
            init();
        }

        // 初始化函数间传递的中间变量
        public void init() {
            argsState.cat = new HashMap<>();
        }

        //map阶段，返回值为boolean类型，当为true则程序继续执行，当为false则程序退出
        public boolean iterate(List<String> dimensions,
                               String measure, String dimen_mode, String time_diff_type, String measure_name) {


            String aggr_key = dimensions.stream().map(s -> {
                if (Compare.hasRD(s) || Compare.hasRD2(s)) {
                    return Compare.dayformat(s, dimen_mode);
                } else {
                    return s;
                }

            }).reduce((a, b) -> String.format("%s_%s", a, b)).get();


            if (measure_name.equals("count")) {
                double ic = Double.parseDouble(argsState.cat.getOrDefault(aggr_key, "0.00"));
                measure = Double.toString(ic + 1);
                argsState.cat.put(aggr_key, measure);
            } else if (measure_name.equals("sum")) {
                double is = Double.parseDouble(argsState.cat.getOrDefault(aggr_key, "0.00"));
                measure = Double.toString(is + Double.parseDouble(measure));
                argsState.cat.put(aggr_key, measure);
            } else if (measure_name.equals("max")) {
                double ima = Double.parseDouble(argsState.cat.getOrDefault(aggr_key, "0.00"));
                if (Double.parseDouble(measure) < ima) {
                    measure = Double.toString(ima);
                }
                argsState.cat.put(aggr_key, measure);
            } else if (measure_name.equals("min")) {
                double imi = Double.parseDouble(argsState.cat.getOrDefault(aggr_key, "999999999.00"));
                if (Double.parseDouble(measure) > imi) {
                    measure = Double.toString(imi);
                }
                argsState.cat.put(aggr_key, measure);
            } else if (measure_name.equals("avg")) {
                String cat_v_avg = argsState.cat.getOrDefault(aggr_key, "0,0");
                double a = Double.parseDouble(cat_v_avg.split(",")[0]);
                double b = Double.parseDouble(cat_v_avg.split(",")[1]) + 1.0;
                a = a + Double.parseDouble(measure);
                argsState.cat.put(aggr_key, (String.format("%s,%s", a, b)));
            } else if (measure_name.equals("discount")) {
                String cat_v_discount = argsState.cat.getOrDefault(aggr_key, "");
                if (cat_v_discount.equals("")) {
                    cat_v_discount = measure;
                } else if (!cat_v_discount.contains(measure)) {
                    cat_v_discount = String.format("%s,%s", cat_v_discount, measure);
                }
                argsState.cat.put(aggr_key, cat_v_discount);
            } else {
                double i = Double.parseDouble(argsState.cat.getOrDefault(aggr_key, "0.00"));
                measure = i + measure;
                argsState.cat.put(aggr_key, measure);
            }

            argsState.cat.put("measure_name", measure_name);
            argsState.cat.put("dimen_mode", dimen_mode);
            argsState.cat.put("time_diff_type", time_diff_type);

            return true;
        }

        /**
         * 类似于combiner,在map范围内做部分聚合，将结果传给merge函数中的形参mapOutput
         * 如果需要聚合，则对iterator返回的结果处理，否则直接返回iterator的结果即可
         */
        public Map<String, String> terminatePartial() {
            return argsState.cat;
        }

        // reduce 阶段，用于逐个迭代处理map当中每个不同key对应的 terminatePartial的结果
        public boolean merge(Map<String, String> mapOutput) {

            String measure_name = mapOutput.get("measure_name");
            String dimen_mode = mapOutput.get("dimen_mode");
            String time_diff_type = mapOutput.get("time_diff_type");

            mapOutput.remove("measure_name");
            mapOutput.remove("dimen_mode");
            mapOutput.remove("time_diff_type");

            Map<String, String> cat1 = argsState.cat;
            String measure_name1 = argsState.cat.get("measure_name");
            String dimen_mode1 = argsState.cat.get("dimen_mode");
            String time_diff_type1 = argsState.cat.get("time_diff_type");

            measure_name = measure_name == null ? measure_name1 : measure_name;
            dimen_mode = dimen_mode == null ? dimen_mode1 : dimen_mode;
            time_diff_type = time_diff_type == null ? time_diff_type1 : time_diff_type;

            argsState.cat.remove("measure_name");
            argsState.cat.remove("dimen_mode");
            argsState.cat.remove("time_diff_type");

            Map<String, String> cat2 = mapOutput;

            String finalMeasure_name = measure_name;
            mapOutput.forEach((key, value) -> {
                String b = cat1.getOrDefault(key, "");
                String a = value;
                if (finalMeasure_name.equals("count")) {
                    if (b.equals("")) {
                        b = "0.00";
                    }
                    a = Double.toString(Double.parseDouble(a) + Double.parseDouble(b));
                    cat1.put(key, a);
                } else if (finalMeasure_name.equals("sum")) {
                    if (b.equals("")) {
                        b = "0.00";
                    }
                    a = Double.toString(Double.parseDouble(a) + Double.parseDouble(b));
                    cat1.put(key, a);
                } else if (finalMeasure_name.equals("max")) {
                    if (b.equals("")) {
                        b = "0.00";
                    }
                    if (Double.parseDouble(b) > Double.parseDouble(a)) {
                        a = b;
                    }
                    cat1.put(key, a);
                } else if (finalMeasure_name.equals("min")) {
                    if (b.equals("")) {
                        b = "999999999.00";
                    }
                    if (Double.parseDouble(b) > Double.parseDouble(a)) {
                        a = b;
                    }
                    cat1.put(key, a);
                } else if (finalMeasure_name.equals("avg")) {
                    String a1 = a.split(",")[0];
                    String a2 = a.split(",")[1];
                    String b1 = "0.00";
                    String b2 = "0.00";
                    if (!b.equals("")) {
                        b1 = b.split(",")[0];
                        b2 = b.split(",")[1];
                    }
                    a = (Double.parseDouble(a1) + Double.parseDouble(b1)) + "," + (Double.parseDouble(a2) + Double.parseDouble(b2));
                    cat1.put(key, a);
                } else if (finalMeasure_name.equals("discount")) {
                    String[] b1_discount = b.split(",");
                    for (String str : b1_discount) {
                        if (a.equals("")) {
                            a = str;
                        } else if (!a.contains(str)) {
                            a = String.format("%s,%s", a, str);
                        }
                    }
                    cat1.put(key, a);
                } else {
                    if (b.equals("")) {
                        b = "0.00";
                    }
                    a = Double.toString(Double.parseDouble(a) + Double.parseDouble(b));
                    cat1.put(key, a);
                }
            });

            cat1.put("measure_name", measure_name);
            cat1.put("dimen_mode", dimen_mode);
            cat1.put("time_diff_type", time_diff_type);
//            argsState.cat.putAll(cat1);
            if (measure_name == null || measure_name.equals("") ||
                    dimen_mode == null || dimen_mode.equals("") ||
                    time_diff_type == null || time_diff_type.equals("")) {
                argsState.cat.putAll(cat1);
                return true;
            } else {
                //
                return true;
            }
            //
//            return true;
        }

        // 处理merge计算完成后的结果，即对merge完成后的结果做最后的业务处理
        public Map<String, String> terminate() {

            String measure_name = argsState.cat.get("measure_name");
            String dimen_mode = argsState.cat.get("dimen_mode");
            String time_diff_type = argsState.cat.get("time_diff_type");

            argsState.cat.remove("measure_name");
            argsState.cat.remove("dimen_mode");
            argsState.cat.remove("time_diff_type");


            Map<String, Double> dog = new HashMap<>();

            if (measure_name.equals("discount")) {
                argsState.cat.forEach((key, value) -> dog.put(key, Double.parseDouble(String.format("%d", value.split(",").length))));
            } else if (measure_name.equals("avg")) {
                argsState.cat.forEach((key, value) -> {
                    String[] tmp = value.split(",");
                    dog.put(key, (Double.parseDouble(tmp[0])) / Double.parseDouble(tmp[1]));
                });
            } else {
                argsState.cat.forEach((key, value) -> dog.put(key, Double.parseDouble(value)));
            }


            Map<String, String> result = new HashMap<>();

            if (dimen_mode.equals("y")) {
//                if (time_diff_type.equals("0")) {
                time_diff_type = "0";
                Map<String, Double> dog2 = new HashMap<>();
                dog.forEach((key, aDouble) -> {
                    String[] k = key.split("_");
                    String k_1 = Arrays.stream(k).reduce((a, b) -> String.format("%s_%s", a, b)).get();
                    String rd = k[0];
                    Calendar ca = Calendar.getInstance();
                    ca.set(Integer.parseInt(rd.substring(0, 4)), 0, 1);
                    ca.add(Calendar.YEAR, -1);
                    String day = Compare.getTime(ca);
                    k[0] = Compare.dayformat(day, dimen_mode);
                    dog2.put(k_1, dog.getOrDefault(Arrays.stream(k).reduce((a, b) -> String.format("%s_%s", a, b)).get(), 0.00));
                });
                Map<String, String> result_dog = new HashMap<>();
                dog.forEach((key, value) -> {
                    Double value2 = dog2.getOrDefault(key, 0.00);
                    result_dog.put(key, Double.toString(value - value2));
                });
                return result_dog;
//                } else {
//                    throw new RuntimeException("dimen_mode y must match time_diff_type[0]");
//                }
            } else if (dimen_mode.equals("ym")) {
                if (time_diff_type.equals("1")) {
                    Map<String, Double> dog2 = new HashMap<>();
                    dog.forEach((key, aDouble) -> {
                        String[] k = key.split("_");
                        String k_1 = Arrays.stream(k).reduce((a, b) -> String.format("%s_%s", a, b)).get();
                        String rd = k[0];
                        Calendar ca = Calendar.getInstance();
                        ca.set(Integer.parseInt(rd.substring(0, 4)), Integer.parseInt(rd.substring(rd.indexOf("年") + 1, rd.indexOf("月"))) - 1, 1);
                        ca.add(Calendar.YEAR, -1);
                        String day = Compare.getTime(ca);
                        k[0] = Compare.dayformat(day, dimen_mode);
                        dog2.put(k_1, dog.getOrDefault(Arrays.stream(k).reduce((a, b) -> String.format("%s_%s", a, b)).get(), 0.00));
                    });

                    Map<String, String> result_dog = new HashMap<>();
                    dog.forEach((key, value) -> {
                        Double value2 = dog2.getOrDefault(key, 0.00);
                        result_dog.put(key, Double.toString(value - value2));
                    });
                    return result_dog;
                } else /*if (time_diff_type.equals("0"))*/ {
                    time_diff_type = "0";
                    Map<String, Double> dog2_0 = new HashMap<>();
                    dog.forEach((key, aDouble) -> {
                        String[] k = key.split("_");
                        String k_1 = Arrays.stream(k).reduce((a, b) -> String.format("%s_%s", a, b)).get();
                        String rd = k[0];
                        Calendar ca = Calendar.getInstance();
                        ca.set(Integer.parseInt(rd.substring(0, 4)), Integer.parseInt(rd.substring(rd.indexOf("年") + 1, rd.indexOf("月"))) - 1, 1);
                        ca.add(Calendar.MONTH, -1);
                        String day = Compare.getTime(ca);
                        k[0] = Compare.dayformat(day, dimen_mode);
                        dog2_0.put(k_1, dog.getOrDefault(Arrays.stream(k).reduce((a, b) -> String.format("%s_%s", a, b)).get(), 0.00));
                    });

                    Map<String, String> result_dog_0 = new HashMap<>();
                    dog.forEach((key, value) -> {
                        Double value2 = dog2_0.getOrDefault(key, 0.00);
                        result_dog_0.put(key, Double.toString(value - value2));
                    });
                    return result_dog_0;
                } /*else {
                    throw new RuntimeException("dimen_mode ym must match time_diff_type[0,1]、\r\n"+
                            );
                }*/

            } else if (dimen_mode.equals("yq")) {
                if (time_diff_type.equals("1")) {
                    Map<String, Double> dog2 = new HashMap<>();
                    dog.forEach((key, aDouble) -> {
                        String[] k = key.split("_");
                        String k_1 = Arrays.stream(k).reduce((a, b) -> String.format("%s_%s", a, b)).get();
                        String rd = k[0];
                        Calendar ca = Calendar.getInstance();

                        int m = Integer.parseInt(rd.substring(rd.indexOf("年") + 1, rd.indexOf("季度")));
                        int mon = 1;
                        switch (m) {
                            case 1:
                                mon = 1;
                                break;
                            case 2:
                                mon = 4;
                                break;
                            case 3:
                                mon = 7;
                                break;
                            default:
                                mon = 10;
                                break;
                        }
                        ca.set(Integer.parseInt(rd.substring(0, 4)), mon - 1, 1);
                        ca.add(Calendar.YEAR, -1);
                        String day = Compare.getTime(ca);
                        k[0] = Compare.dayformat(day, dimen_mode);
                        dog2.put(k_1, dog.getOrDefault(Arrays.stream(k).reduce((a, b) -> String.format("%s_%s", a, b)).get(), 0.00));
                    });
                    Map<String, String> result_dog_0 = new HashMap<>();
                    dog.forEach((key, value) -> {
                        Double value2 = dog2.getOrDefault(key, 0.00);
                        result_dog_0.put(key, Double.toString(value - value2));
                    });
                    return result_dog_0;
                } else /*if (time_diff_type.equals("0"))*/ {
                    time_diff_type = "0";
                    Map<String, Double> dog2 = new HashMap<>();
                    dog.forEach((key, aDouble) -> {
                        String[] k = key.split("_");
                        String k_1 = Arrays.stream(k).reduce((a, b) -> String.format("%s_%s", a, b)).get();
                        String rd = k[0];
                        Calendar ca = Calendar.getInstance();

                        int m = Integer.parseInt(rd.substring(rd.indexOf("年") + 1, rd.indexOf("季度")));
                        int mon = 1;
                        switch (m) {
                            case 1:
                                mon = 1;
                                break;
                            case 2:
                                mon = 4;
                                break;
                            case 3:
                                mon = 7;
                                break;
                            default:
                                mon = 10;
                                break;
                        }
                        ca.set(Integer.parseInt(rd.substring(0, 4)), mon - 1, 1);
                        ca.add(Calendar.MONTH, -1);
                        String day = Compare.getTime(ca);
                        k[0] = Compare.dayformat(day, dimen_mode);
                        dog2.put(k_1, dog.getOrDefault(Arrays.stream(k).reduce((a, b) -> String.format("%s_%s", a, b)).get(), 0.00));
                    });
                    Map<String, String> result_dog_0 = new HashMap<>();
                    dog.forEach((key, value) -> {
                        Double value2 = dog2.getOrDefault(key, 0.00);
                        result_dog_0.put(key, Double.toString(value - value2));
                    });
                    return result_dog_0;
                } /*else {
                    throw new RuntimeException("dimen_mode yq must match time_diff_type[0,1]");
                }*/
            } else if (dimen_mode.equals("yw")) {
                if (time_diff_type.equals("1")) {

                    Map<String, Double> dog2 = new HashMap<>();
                    dog.forEach((key, aDouble) -> {
                        String[] k = key.split("_");
                        String k_1 = Arrays.stream(k).reduce((a, b) -> String.format("%s_%s", a, b)).get();
                        String rd = k[0];
                        Calendar ca = Calendar.getInstance();

                        ca.setFirstDayOfWeek(Calendar.MONDAY);
                        ca.set(Calendar.YEAR, Integer.parseInt(rd.substring(0, 4)));
                        ca.set(Calendar.WEEK_OF_YEAR, Integer.parseInt(rd.substring(rd.indexOf("年") + 1, rd.indexOf("周"))));
                        ca.add(Calendar.YEAR, -1);
                        String day = Compare.getTime(ca);
                        k[0] = Compare.dayformat(day, dimen_mode);
                        dog2.put(k_1, dog.getOrDefault(Arrays.stream(k).reduce((a, b) -> String.format("%s_%s", a, b)).get(), 0.00));
                    });
                    Map<String, String> result_dog_0 = new HashMap<>();
                    dog.forEach((key, value) -> {
                        Double value2 = dog2.getOrDefault(key, 0.00);
                        result_dog_0.put(key, Double.toString(value - value2));
                    });
                    return result_dog_0;
                } else /*if (time_diff_type.equals("0"))*/ {
                    time_diff_type = "0";
                    Map<String, Double> dog2 = new HashMap<>();
                    dog.forEach((key, aDouble) -> {
                        String[] k = key.split("_");
                        String k_1 = Arrays.stream(k).reduce((a, b) -> String.format("%s_%s", a, b)).get();
                        String rd = k[0];
                        Calendar ca = Calendar.getInstance();

                        ca.setFirstDayOfWeek(Calendar.MONDAY);
                        ca.set(Calendar.YEAR, Integer.parseInt(rd.substring(0, 4)));
                        ca.set(Calendar.WEEK_OF_YEAR, Integer.parseInt(rd.substring(rd.indexOf("年") + 1, rd.indexOf("周"))));
                        ca.add(Calendar.WEEK_OF_YEAR, -1);
                        String day = Compare.getTime(ca);
                        k[0] = Compare.dayformat(day, dimen_mode);
                        dog2.put(k_1, dog.getOrDefault(Arrays.stream(k).reduce((a, b) -> String.format("%s_%s", a, b)).get(), 0.00));
                    });
                    Map<String, String> result_dog_0 = new HashMap<>();
                    dog.forEach((key, value) -> {
                        Double value2 = dog2.getOrDefault(key, 0.00);
                        result_dog_0.put(key, Double.toString(value - value2));
                    });
                    return result_dog_0;
                } /*else {
                    throw new RuntimeException("dimen_mode yw must match time_diff_type[0,1]");
                }*/
            } else {
                if (time_diff_type.equals("3")) {

                    Map<String, Double> dog2 = new HashMap<>();
                    dog.forEach((key, aDouble) -> {
                        String[] k = key.split("_");
                        String k_1 = Arrays.stream(k).reduce((a, b) -> String.format("%s_%s", a, b)).get();
                        String rd = k[0];
                        Calendar ca = Calendar.getInstance();
                        ca.set(Integer.parseInt(rd.substring(0, 4)), Integer.parseInt(rd.substring(rd.indexOf("年") + 1, rd.indexOf("月"))) - 1,
                                Integer.parseInt(rd.substring(rd.indexOf("月") + 1, rd.indexOf("日"))));
                        ca.add(Calendar.YEAR, -1);
                        String day = Compare.getTime(ca);
                        k[0] = Compare.dayformat(day, dimen_mode);
                        dog2.put(k_1, dog.getOrDefault(Arrays.stream(k).reduce((a, b) -> String.format("%s_%s", a, b)).get(), 0.00));
                    });
                    Map<String, String> result_dog_0 = new HashMap<>();
                    dog.forEach((key, value) -> {
                        Double value2 = dog2.getOrDefault(key, 0.00);
                        result_dog_0.put(key, Double.toString(value - value2));
                    });
                    return result_dog_0;
                } else if (time_diff_type.equals("2")) {

                    Map<String, Double> dog2 = new HashMap<>();
                    dog.forEach((key, aDouble) -> {
                        String[] k = key.split("_");
                        String k_1 = Arrays.stream(k).reduce((a, b) -> String.format("%s_%s", a, b)).get();
                        String rd = k[0];
                        Calendar ca = Calendar.getInstance();
                        ca.set(Integer.parseInt(rd.substring(0, 4)), Integer.parseInt(rd.substring(rd.indexOf("年") + 1, rd.indexOf("月"))) - 1,
                                Integer.parseInt(rd.substring(rd.indexOf("月") + 1, rd.indexOf("日"))));
                        ca.add(Calendar.MONTH, -1);
                        String day = Compare.getTime(ca);
                        k[0] = Compare.dayformat(day, dimen_mode);
                        dog2.put(k_1, dog.getOrDefault(Arrays.stream(k).reduce((a, b) -> String.format("%s_%s", a, b)).get(), 0.00));
                    });
                    Map<String, String> result_dog_0 = new HashMap<>();
                    dog.forEach((key, value) -> {
                        Double value2 = dog2.getOrDefault(key, 0.00);
                        result_dog_0.put(key, Double.toString(value - value2));
                    });
                    return result_dog_0;
                } else if (time_diff_type.equals("1")) {

                    Map<String, Double> dog2 = new HashMap<>();
                    dog.forEach((key, aDouble) -> {
                        String[] k = key.split("_");
                        String k_1 = Arrays.stream(k).reduce((a, b) -> String.format("%s_%s", a, b)).get();
                        String rd = k[0];
                        Calendar ca = Calendar.getInstance();
                        ca.set(Integer.parseInt(rd.substring(0, 4)), Integer.parseInt(rd.substring(rd.indexOf("年") + 1, rd.indexOf("月"))) - 1,
                                Integer.parseInt(rd.substring(rd.indexOf("月") + 1, rd.indexOf("日"))));
                        ca.add(Calendar.WEEK_OF_YEAR, -1);
                        String day = Compare.getTime(ca);
                        k[0] = Compare.dayformat(day, dimen_mode);
                        dog2.put(k_1, dog.getOrDefault(Arrays.stream(k).reduce((a, b) -> String.format("%s_%s", a, b)).get(), 0.00));
                    });
                    Map<String, String> result_dog_0 = new HashMap<>();
                    dog.forEach((key, value) -> {
                        Double value2 = dog2.getOrDefault(key, 0.00);
                        result_dog_0.put(key, Double.toString(value - value2));
                    });
                    return result_dog_0;
                } else/*e if (time_diff_type.equals("0")) */ {
                    time_diff_type = "0";
                    Map<String, Double> dog2 = new HashMap<>();
                    dog.forEach((key, aDouble) -> {
                        String[] k = key.split("_");
                        String k_1 = Arrays.stream(k).reduce((a, b) -> String.format("%s_%s", a, b)).get();
                        String rd = k[0];
                        Calendar ca = Calendar.getInstance();
                        ca.set(Integer.parseInt(rd.substring(0, 4)),
                                Integer.parseInt(rd.substring(rd.indexOf("年") + 1, rd.indexOf("月"))) - 1,
                                Integer.parseInt(rd.substring(rd.indexOf("月") + 1, rd.indexOf("日"))));
                        ca.add(Calendar.DAY_OF_YEAR, -1);
                        String day = Compare.getTime(ca);
                        k[0] = Compare.dayformat(day, dimen_mode);
                        dog2.put(k_1, dog.getOrDefault(Arrays.stream(k).reduce((a, b) -> String.format("%s_%s", a, b)).get(), 0.00));
                    });
                    Map<String, String> result_dog_0 = new HashMap<>();
                    dog.forEach((key, value) -> {
                        Double value2 = dog2.getOrDefault(key, 0.00);
                        result_dog_0.put(key, Double.toString(value - value2));
                    });
                    return result_dog_0;
                } /*else {
                    throw new RuntimeException(
                            "dimen_mode ymd must match time_diff_type[0,1,2,3] " + time_diff_type);
                }*/
            }


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