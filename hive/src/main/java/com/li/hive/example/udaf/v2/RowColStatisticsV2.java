package com.li.hive.example.udaf.v2;

import org.apache.commons.lang.text.StrBuilder;
import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDAF;
import org.apache.hadoop.hive.ql.exec.UDAFEvaluator;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Description(name = "row_col_stat", value = "_FUNC_(dimension,measure,dimen_mode,time_diff_type,measure_name) - Returns the mean of a set of numbers")
public class RowColStatisticsV2 extends UDAF {

    public static Logger logger = Logger.getLogger(RowColStatisticsV2.class);

    public static class ArgsState {
        private Map<String, Map<String, String>> cat;
        private Map<String, String> dog;
        private Map<String, String> info;

    }

    public static class Evaluator implements UDAFEvaluator {
        ArgsState argsState;

        public Evaluator() {
            argsState = new ArgsState();
            init();
        }

        public void init() {
            if (argsState.cat == null) {
                argsState.cat = new HashMap<>();
            }
            if (argsState.dog == null) {
                argsState.dog = new HashMap<>();
            }
            if (argsState.info == null) {
                argsState.info = new HashMap<>();
            }
        }

        public boolean iterate(List<String> input0, List<String> compare, List<String> measure_value,
                               String rowcol_num, String dimen_mode, List<String> measure_func, List<String> rowsumtype) {

            // 行统计维度 cat
            // 时间格式化 ymd ...
            Map<String, String> info = new HashMap<>();
            Map<String, String> dog = new HashMap<>();
            info.put("rowcol", rowcol_num);
            // dimensions
            // 行键
            List<String> dimensions = input0.stream().map(new Function<String, String>() {
                @Override
                public String apply(String d) {
                    return hasRD(d) > 7 ? dayformat(d, dimen_mode, hasRD(d)) : d;
                }
            }).collect(Collectors.toList());
            // dimension_length
            info.put("dimension_length", Integer.toString(dimensions.size()));

            String dimension_key = dimensions.stream().reduce((a, b) -> String.format("%s△%s", a, b)).get();
            dimensions.addAll(compare);
            String compare_key = dimensions.stream().reduce((a, b) -> String.format("%s△%s", a, b)).get();
            // compare_length
            info.put("compare_length", Integer.toString(compare.size()));
            // measure_length
            info.put("measure_length", Integer.toString(measure_value.size()));
            // rowsumtype
            String rowsum_type = rowsumtype.stream().reduce((a, b) -> String.format("%s_%s", a, b)).get();
            info.put("rowsumtype", rowsum_type);

            //
            info.put("measure_func", measure_func.stream().reduce((a, b) -> String.format("%s,%s", a, b)).get());

            Map<String, String> measure = new HashMap<>();
            for (int i = 0; i < measure_func.size(); i++) {
                measure.put(measure_func.get(i), measure_value.get(i));
            }

            Map<String, String> subCat = argsState.cat.getOrDefault(dimension_key, new HashMap<>());
            for (Map.Entry<String, String> mea : measure.entrySet()) {

                String key = mea.getKey();
                String value = mea.getValue();
                String[] key_arr = key.split("-");

                String mea_key = compare_key + "△" + key;

                if (key_arr[1].equals("sum")) {
                    double v = Double.parseDouble(subCat.getOrDefault(mea_key, "0.00"));
                    v = (v + Double.parseDouble(value));
                    subCat.put(mea_key, Double.toString(v));

                } else if (key_arr[1].equals("count")) {
                    double v = Double.parseDouble(subCat.getOrDefault(mea_key, "0.00"));
                    v = (v + 1);
                    subCat.put(mea_key, Double.toString(v));
                } else if (key_arr[1].equals("max")) {
                    double v = Double.parseDouble(subCat.getOrDefault(mea_key, "0.00"));
                    if (Double.parseDouble(value) > v) {
                        v = Double.parseDouble(value);
                    }
                    subCat.put(mea_key, Double.toString(v));
                } else if (key_arr[1].equals("min")) {
                    double v = Double.parseDouble(subCat.getOrDefault(mea_key, "9999999.00"));
                    if (Double.parseDouble(value) < v) {
                        v = Double.parseDouble(value);
                    }
                    subCat.put(mea_key, Double.toString(v));
                } else if (key_arr[1].equals("avg")) {
                    String v = subCat.getOrDefault(mea_key, "0,0");
                    double a = Double.parseDouble(v.split(",")[0]);
                    double b = Double.parseDouble(v.split(",")[1]) + 1.0;
                    a = a + Double.parseDouble(value);
                    subCat.put(mea_key, String.format("%s,%s", a, b));
                } else {
                    String v = subCat.getOrDefault(mea_key, "");
                    if (v.equals("")) {
                        v = value;
                    } else if (!v.contains(value)) {
                        v = String.format("%s,%s", v, value);
                    }
                    subCat.put(mea_key, v);
                }
            }
            argsState.cat.put(dimension_key, subCat);

            // 行统计
            for (Map.Entry<String, String> en : measure.entrySet()) {
                String key = en.getKey();

                String dimension_tmp_key = String.format("%s△%s", dimension_key, key);
                String m_value = en.getValue();

                if (key.endsWith("sum")) {

                    double v = Double.parseDouble(argsState.dog.getOrDefault(dimension_tmp_key, "0.00"));
                    v = v + Double.parseDouble(m_value);
                    argsState.dog.put(dimension_tmp_key, Double.toString(v));
                } else if (key.endsWith("count")) {

                    double v = Double.parseDouble(argsState.dog.getOrDefault(dimension_tmp_key, "0.00"));
                    v = v + 1;
                    argsState.dog.put(dimension_tmp_key, Double.toString(v));
                } else if (key.endsWith("max")) {

                    double v = Double.parseDouble(argsState.dog.getOrDefault(dimension_tmp_key, "0.00"));
                    if (Double.parseDouble(m_value) > v) {
                        v = Double.parseDouble(m_value);
                    }
                    argsState.dog.put(dimension_tmp_key, Double.toString(v));
                } else if (key.endsWith("min")) {

                    double v = Double.parseDouble(argsState.dog.getOrDefault(dimension_tmp_key, "9999999.00"));
                    if (Double.parseDouble(m_value) < v) {
                        v = Double.parseDouble(m_value);
                    }
                    argsState.dog.put(dimension_tmp_key, Double.toString(v));
                } else if (key.endsWith("avg")) {

                    String v = argsState.dog.getOrDefault(dimension_tmp_key, "0,0");
                    double a = Double.parseDouble(v.split(",")[0]);
                    double b = Double.parseDouble(v.split(",")[1]) + 1.0;
                    a = a + Double.parseDouble(m_value);
                    argsState.dog.put(dimension_tmp_key, String.format("%s,%s", a, b));
                } else {
                    String v = argsState.dog.getOrDefault(dimension_tmp_key, "");
                    if (v.equals("")) {
                        v = m_value;
                    } else if (!v.contains(m_value)) {
                        v = String.format("%s,%s", v, m_value);
                    }
                    argsState.dog.put(dimension_tmp_key, v);
                }
            }

            argsState.cat.put("dog", argsState.dog);
            argsState.cat.put("info", info);

            return true;
        }


        public Map<String, Map<String, String>> terminatePartial() {
            return argsState.cat;
        }

        public boolean merge(Map<String, Map<String, String>> mapOutput) {

            Map<String, String> dog2 = mapOutput.getOrDefault("dog", new HashMap<>());
            Map<String, String> info2 = mapOutput.getOrDefault("info", new HashMap<>());
            if (info2 == null || info2.size() == 0) {
                return true;
            }
            mapOutput.remove("dog");
            mapOutput.remove("info");

            Map<String, String> info1 = argsState.cat.getOrDefault("info", new HashMap<>());
            Map<String, String> dog1 = argsState.cat.getOrDefault("dog", new HashMap<>());
            argsState.cat.remove("dog");
            argsState.cat.remove("info");

            Map<String, Map<String, String>> cat1 = argsState.cat;


            for (Map.Entry<String, Map<String, String>> en : mapOutput.entrySet()) {
                String outkey = en.getKey();
                Map<String, String> cat21 = en.getValue();
                Map<String, String> cat11 = cat1.getOrDefault(outkey, new HashMap<>());

                for (Map.Entry<String, String> inen : cat21.entrySet()) {
                    String subk = inen.getKey();

                    if (subk.contains("count")) {

                        double dvalue = Double.parseDouble(cat11.getOrDefault(subk, "0.00")) +
                                Double.parseDouble(cat21.getOrDefault(subk, "0.00"));
                        cat11.put(subk, Double.toString(dvalue));
                    } else if (subk.contains("max")) {
                        double dvalue1 = Double.parseDouble(cat11.getOrDefault(subk, "0.00"));
                        double dvalue2 = Double.parseDouble(cat21.getOrDefault(subk, "0.00"));
                        if (dvalue2 > dvalue1) {
                            dvalue1 = dvalue2;
                        }
                        cat11.put(subk, Double.toString(dvalue1));
                    } else if (subk.contains("min")) {

                        double dvalue1 = Double.parseDouble(cat11.getOrDefault(subk, "9999999.00"));
                        double dvalue2 = Double.parseDouble(cat21.getOrDefault(subk, "9999999.00"));
                        if (dvalue2 < dvalue1) {
                            dvalue1 = dvalue2;
                        }
                        cat11.put(subk, Double.toString(dvalue1));
                    } else if (subk.contains("uniqueCount")) {
                        String dvalue1 = cat11.getOrDefault(subk, "");
                        String dvalue2 = cat21.getOrDefault(subk, "");

                        String[] dvalue2_arr = dvalue2.split(",");
                        for (String f : dvalue2_arr) {
                            if (dvalue1.equals("")) {
                                dvalue1 = f;
                            } else if (!dvalue1.contains(f)) {
                                dvalue1 = String.format("%s,%s", dvalue1, f);
                            }
                        }
                        cat11.put(subk, dvalue1);
                    } else if (subk.contains("avg")) {
                        String dvalue1 = cat11.getOrDefault(subk, "0,0");
                        String dvalue2 = cat21.getOrDefault(subk, "0,0");

                        double a1 = Double.parseDouble(dvalue1.split(",")[0]);
                        double a2 = Double.parseDouble(dvalue1.split(",")[1]);
                        double b1 = Double.parseDouble(dvalue2.split(",")[0]);
                        double b2 = Double.parseDouble(dvalue2.split(",")[1]);
                        cat11.put(subk, ((a1 + b1) + "," + (a2 + b2)));
                    } else {
                        double dvalue = Double.parseDouble(cat11.getOrDefault(subk, "0.00")) +
                                Double.parseDouble(cat21.getOrDefault(subk, "0.00"));
                        cat11.put(subk, Double.toString(dvalue));
                    }
                }
                cat1.put(outkey, cat11);
            }

            argsState.cat.putAll(cat1);

            for (Map.Entry<String, String> en : dog2.entrySet()) {
                String keyd = en.getKey();

                if (keyd.contains("count")) {
                    double v1 = Double.parseDouble(dog2.getOrDefault(keyd, "0.00"));
                    double v2 = Double.parseDouble(dog1.getOrDefault(keyd, "0.00"));
                    double result = v1 + v2;
                    dog1.put(keyd, Double.toString(result));
                } else if (keyd.contains("max")) {
                    double v1 = Double.parseDouble(dog2.getOrDefault(keyd, "0.00"));
                    double v2 = Double.parseDouble(dog1.getOrDefault(keyd, "0.00"));

                    if (v2 > v1) {
                        v1 = v2;
                    }
                    dog1.put(keyd, Double.toString(v1));
                } else if (keyd.contains("min")) {
                    double v1 = Double.parseDouble(dog2.getOrDefault(keyd, "9999999.00"));
                    double v2 = Double.parseDouble(dog1.getOrDefault(keyd, "9999999.00"));

                    if (v2 < v1) {
                        v1 = v2;
                    }
                    dog1.put(keyd, Double.toString(v1));
                } else if (keyd.contains("uniqueCount")) {

                    String v1 = dog2.getOrDefault(keyd, "");
                    String v2 = dog1.getOrDefault(keyd, "");
                    if (v2.equals("")) {
                        v2 = v1;
                    } else {
                        String[] vrr = v1.split(",");
                        for (String f : vrr) {
                            if (!v2.contains(f)) {
                                v2 = String.format("%s,%s", v2, f);
                            }
                        }
                    }
                    dog1.put(keyd, v2);

                } else if (keyd.contains("avg")) {

                    String v1 = dog2.getOrDefault(keyd, "0,0");
                    String v2 = dog1.getOrDefault(keyd, "0,0");
                    double a1 = Double.parseDouble(v1.split(",")[0]);
                    double a2 = Double.parseDouble(v1.split(",")[1]);
                    double b1 = Double.parseDouble(v2.split(",")[0]);
                    double b2 = Double.parseDouble(v2.split(",")[1]);

                    dog1.put(keyd, ((a1 + b1) + "," + (a2 + b2)));
                } else {
                    double v1 = Double.parseDouble(dog2.getOrDefault(keyd, "0.00"));
                    double v2 = Double.parseDouble(dog1.getOrDefault(keyd, "0.00"));
                    double result = v1 + v2;
                    dog1.put(keyd, Double.toString(result));
                }
            }

//            argsState.cat.put("fish", fish);
            argsState.cat.put("dog", dog1);
            argsState.cat.put("info", info2);
            return true;
        }

        // str2double
        public double str2double(String key, String v) {
            if (key.contains("avg")) {
                String[] enarr = v.split(",");
                return Double.parseDouble(enarr[0]) / Double.parseDouble(enarr[1]);
            } else if (key.contains("uniqueCount")) {
                return Double.parseDouble(new String(v.split(",").length + ""));
            } else {
                return Double.parseDouble(v);
            }
        }

        // 处理merge计算完成后的结果，即对merge完成后的结果做最后的业务处理
        public Map<String, Double> terminate() {

            Map<String, String> dog = argsState.cat.getOrDefault("dog", new HashMap<>());
            Map<String, String> info = argsState.cat.getOrDefault("info", new HashMap<>());
            if (info == null || info.size() == 0) {
                throw new RuntimeException("info is null \r\n " + argsState.cat.toString());
            }

            int dimension_length = Integer.parseInt(info.getOrDefault("dimension_length", "0"));
            int compare_length = Integer.parseInt(info.getOrDefault("compare_length", "0"));
            int measure_length = Integer.parseInt(info.getOrDefault("measure_length", "0"));
            //  measure_func
            String measure_name = info.get("measure_func");
            String[] measure_arr = measure_name.split(",");

            String[] rowsumtype = info.getOrDefault("rowsumtype", "").split("_");
            if (rowsumtype.length == 0) {
                throw new RuntimeException("rowsumtype size is 0");
            }
            String rowcol = info.get("rowcol");

            argsState.cat.remove("dog");
            argsState.cat.remove("info");

            Map<String, Map<String, String>> cat = argsState.cat;
            if (cat == null || cat.size() == 0) {
                throw new RuntimeException("cat is null");
            }

            Map<String, Double> dimension = new HashMap<>();
            for (Map.Entry<String, String> en : dog.entrySet()) {
                String value = en.getValue();
                String key = en.getKey();
                double v = str2double(key, value);
                dimension.put(key, v);
            }


            Map<String, Double> result = new HashMap<>();

            // 行合计
            Map<String, Double> rowsumMap = new HashMap<>();
            // 分列小计
            Map<String, Double> measure_totalMap = new HashMap<>();
            Map<String, Integer> dimenkey2compareSize = new HashMap<>();
            // 数值小计
            Map<String, Double> subMeasureSum = new HashMap<>();


            Map<String, String> compareValue = new HashMap<>();
            for (Map<String, String> l : cat.values()) {
                compareValue.putAll(l);
            }

            for (Map<String, String> value : cat.values()) {
                for (String measure_key : value.keySet()) {
                    // doCompare(dimension_length, compare_length, measure_arr, rowcol, result, dimension, compareValue, k);
                    String compare_key = measure_key.substring(0, measure_key.lastIndexOf("△"));
                    String compare_key_tmp = measure_key.substring(0, measure_key.lastIndexOf("△"));
//                    if (1 == 1) {
//                        throw new RuntimeException(compare_key);
//                    }
                    String[] all_keys = compare_key.split("△");

                    // create dimension key
                    String dimensionKeys = all_keys[0];
                    for (int i = 1; i < dimension_length; i++) {
                        try {
                            dimensionKeys = String.format("%s△%s", dimensionKeys, all_keys[i]);
                        } catch (Exception e) {
                            dimensionKeys = String.format("%s△%s", dimensionKeys, "");
                        }

                    }

                    int dimenCompareSize = dimenkey2compareSize.getOrDefault(dimensionKeys, 0);
                    dimenCompareSize++;
                    dimenkey2compareSize.put(dimensionKeys, dimenCompareSize);

                    // create compare key
                    String compareKeys = "";
                    if (compare_length > 0) {
                        compareKeys = all_keys[0];
                        for (int i = (dimension_length + 1); i < (dimension_length + compare_length); i++) {
                            compareKeys = compareKeys + "△" + all_keys[i];
                        }
                    }

                    double rowsum = rowsumMap.getOrDefault(dimensionKeys, 0.00);
                    if (rowsum == 0.00 && rowsumtype[0].equals("max-1")) {
                        rowsum = -9999999;
                    } else if (rowsum == 0.00 && rowsumtype[0].equals("min-1")) {
                        rowsum = 9999999;
                    }

                    double measure_total = measure_totalMap.getOrDefault(compare_key, 0.00);
                    if (measure_total == 0.00 && rowsumtype[rowsumtype.length - 1].equals("max-1")) {
                        measure_total = -9999999;
                    } else if (measure_total == 0.00 && rowsumtype[rowsumtype.length - 1].equals("min-1")) {
                        measure_total = 9999999;
                    }

                    for (int i = 0; i < measure_arr.length; i++) {
                        String s = measure_arr[i];
                        String tmp_key = String.format("%s△%s", compare_key, s);

                        String cv = compareValue.getOrDefault(tmp_key, "0.00");
                        double v = 0.00;

                        if (tmp_key.endsWith("avg")) {
                            v = Double.parseDouble(cv.split(",")[0]) / Double.parseDouble(cv.split(",")[1]);
                        } else if (tmp_key.endsWith("uniqueCount")) {
                            v = cv.split(",").length * 1.0;
                        } else {
                            try {
                                v = Double.parseDouble(cv);
                            } catch (Exception e) {
                                throw new RuntimeException(measure_key + "  " + tmp_key + "  " + cv);
                            }

                        }

                        double submeasure = subMeasureSum.getOrDefault(dimensionKeys + "△" + s, 0.00);
                        if (submeasure == 0.00 && rowsumtype[i + 1].equals("max-1")) {
                            submeasure = -9999999999999.0;
                        } else if (submeasure == 0.00 && rowsumtype[i + 1].equals("min-1")) {
                            submeasure = 9999999999.0;
                        }

                        if (rowsumtype[i + 1].equals("sum-1") || rowsumtype[i + 1].equals("avg-1")) {
                            submeasure = submeasure + v / measure_length;
                            subMeasureSum.put(String.format("%s△%s", dimensionKeys, s), submeasure);
                        } else if (rowsumtype[i + 1].equals("max-1")) {
                            if (v > submeasure) {
                                submeasure = v;
                            }
                            subMeasureSum.put(String.format("%s△%s", dimensionKeys, s), submeasure);
                        } else if (rowsumtype[i + 1].equals("min-1")) {
                            if (v < submeasure) {
                                submeasure = v;
                            }
                            subMeasureSum.put(String.format("%s△%s", dimensionKeys, s), submeasure);
                        }

                        if (rowsumtype[rowsumtype.length - 1].equals("max-1")) {
                            if (v > measure_total) {
                                measure_total = v;
                            }
                        } else if (rowsumtype[rowsumtype.length - 1].equals("min-1")) {
                            if (v < measure_total) {
                                measure_total = v;
                            }
                        } else {
                            measure_total = measure_total + v / measure_length;
                        }
                        measure_totalMap.put(compare_key, measure_total);

                        if (rowsumtype[0].equals("max-1")) {
                            if (rowsumtype[rowsumtype.length - 1].split("-")[1].equals("1")) {
                                if (measure_total > rowsum) {
                                    rowsum = measure_total;
                                }
                            }
                            if (v > rowsum) {
                                rowsum = v;
                            }
                        } else if (rowsumtype[0].equals("min-1")) {
                            if (v < rowsum) {
                                rowsum = v;
                            }
                        }
//                        measure_total = measure_total + v;
                        compare_key_tmp = String.format("%s△%s", compare_key_tmp, v);
                    }

                    // 行合计
                    for (int i = 0; i < measure_arr.length; i++) {
                        String s = measure_arr[i];
                        double v = dimension.getOrDefault(dimensionKeys + "△" + s, 0.00);
                        if (rowsumtype[0].equals("sum-1") || rowsumtype[0].equals("avg-1")) {
                            rowsum = rowsum + v / measure_length;
                        }
                    }

                    rowsumMap.put(dimensionKeys, rowsum);
                    result.put(compare_key_tmp, 0.00);
                }
            }

            if (dimension.size() == 0) {
                throw new RuntimeException("dimension is null");
            }


            Map<String, Double> result_bak = new HashMap<>();
            for (Map.Entry<String, Double> en : result.entrySet()) {

                String r = en.getKey();
                String dkey = getKey(r, dimension_length);
                int size = dimenkey2compareSize.getOrDefault(dkey, 1);
                for (int i = 0; i < measure_arr.length; i++) {
                    double v = subMeasureSum.getOrDefault(dkey + "△" + measure_arr[i], 0.00);

                    if (rowsumtype[i + 1].equals("avg-1")) {
                        v /= ((double) size / measure_length);
                    }
                    r = r + "△" + v;
                }

                String ckey = getKey(r, dimension_length + compare_length);
                int comsize = dimenkey2compareSize.getOrDefault(dkey, 0);
//                double rowsum = rowsumMap.getOrDefault(dkey, 0.00) / (comsize / measure_length);

                double rowsum = rowsumMap.getOrDefault(dkey, 0.00);
                if (rowsumtype[0].equals("sum-1") || rowsumtype[0].equals("avg-1")) {
                    rowsum = rowsum / (comsize / measure_length);
                }

                if (rowsumtype[0].equals("avg-1")) {
                    rowsum = rowsum / size;
                }
                double measure_total = measure_totalMap.get(ckey);
                if (rowsumtype[rowsumtype.length - 1].equals("avg-1")) {
                    measure_total = measure_total / measure_length;
                }

                if (Integer.parseInt(rowcol) == 1 || Integer.parseInt(rowcol) == 7) {
                    r = r + "△" + rowsum + "△" + measure_total + "△";
                    result_bak.put(r, en.getValue());
                } else {
                    r = r + "△";
                    result_bak.put(r, en.getValue());
                }
            }

            if (rowcol.equals("7")) {
                StrBuilder mea = new StrBuilder();
                for (int i = 0; i < measure_length; i++) {
                    if (i == measure_length - 1) {
                        mea.append("0.0");
                    } else {
                        mea.append("0.0△");
                    }
                }

                Map<String, String> totalSumMap = new HashMap<>();
                String total_key = "总计";
                for (int i = 1; i < dimension_length; i++) {
                    total_key += "△";
                }
                for (String k : result_bak.keySet()) {

                    String[] all_key = k.split("△");
                    List<String> compare_list = splitArray(k.split("△"), dimension_length, dimension_length + compare_length);
                    List<String> measure_list = splitArray(k.split("△"), dimension_length + compare_length, dimension_length + compare_length + measure_length);

                    String total_pre_key = total_key;
                    if (compare_list != null && compare_list.size() > 0) {
                        total_pre_key = String.format("%s△%s", total_key, compare_list.stream().reduce((a, b) -> String.format("%s△%s", a, b)).get());
                    }

                    String[] tt = totalSumMap.getOrDefault(total_pre_key, mea.toString()).split("△");
                    StringBuilder res = new StringBuilder();
                    for (int i = 0; i < measure_list.size(); i++) {
                        double d = Double.parseDouble(tt[i]) + Double.parseDouble(measure_list.get(i));
                        if (i == measure_list.size() - 1) {
                            res.append(d);
                        } else {
                            res.append(d).append("△");
                        }
                    }
                    Double sumRes = Arrays.stream(res.toString().split("△")).map(Double::parseDouble).reduce((a, b) -> a + b).get();
                    totalSumMap.put(total_pre_key, String.format("%s△%s△%s△%s△columnSum", res, res, sumRes, sumRes));
                }

                Map<String, Double> total_rowsumMap7 = new HashMap();
                Map<String, Double> total_measure_totalMap7 = new HashMap();
                Map<String, Integer> total_dimenkey2compareSize7 = new HashMap();
                Map<String, Double> total_subMeasureSum7 = new HashMap();

                for (Map.Entry<String, String> en : totalSumMap.entrySet()) {
                    String key = en.getKey();
                    String value = en.getValue();

                    String[] valueArr = value.split("△");
                    int valueArrlength = valueArr.length;

                    String dkey = getKey(key, dimension_length);
                    String ckey = getKey(key, dimension_length + compare_length);

                    int dimenCompareSize = total_dimenkey2compareSize7.getOrDefault(dkey, 0);
                    dimenCompareSize += 1;
                    total_dimenkey2compareSize7.put(dkey, dimenCompareSize);

                    double rowsumTmp = total_rowsumMap7.getOrDefault(dkey, 0.0);
                    if (rowsumTmp == 0.00 && rowsumtype[0].equals("max-1")) {
                        rowsumTmp = -9999999;
                    } else if (rowsumTmp == 0.00 && rowsumtype[0].equals("min-1")) {
                        rowsumTmp = 9999999;
                    }
                    double measure_totalTmp = total_measure_totalMap7.getOrDefault(ckey, 0.0);
                    if (measure_totalTmp == 0.00 && rowsumtype[rowsumtype.length - 1].equals("max-1")) {
                        measure_totalTmp = -9999999;
                    } else if (measure_totalTmp == 0.00 && rowsumtype[rowsumtype.length - 1].equals("min-1")) {
                        measure_totalTmp = 9999999;
                    }

                    String[] measure_value = getKey(value, measure_length).split("△");
                    String rowsum = valueArr[valueArrlength - 1 - 1 - 1];
                    String mesure_total = valueArr[valueArrlength - 1 - 1];

                    String rowsumCol = rowsumtype[0];
                    String mesure_totalCol = rowsumtype[rowsumtype.length - 1];

                    //  行合计
                    if (rowsumCol.equals("max-1")) {
                        for (String i : measure_value) {
                            double v = Double.parseDouble(i);
                            if (v > rowsumTmp) {
                                rowsumTmp = v;
                            }
                        }
                        total_rowsumMap7.put(dkey, rowsumTmp);
                    } else if (rowsumCol.equals("min-1")) {
                        for (String i : measure_value) {
                            double v = Double.parseDouble(i);
                            if (v < rowsumTmp) {
                                rowsumTmp = v;
                            }
                        }
                        total_rowsumMap7.put(dkey, rowsumTmp);
                    } else {
                        for (String i : measure_value) {
                            rowsumTmp += Double.parseDouble(i);
                        }
                        total_rowsumMap7.put(dkey, rowsumTmp);
                    }

                    //分列小计
                    if (mesure_totalCol.equals("max-1")) {
                        for (String i : measure_value) {
                            double v = Double.parseDouble(i);
                            if (v > measure_totalTmp) {
                                measure_totalTmp = v;
                            }
                        }
                        total_measure_totalMap7.put(ckey, measure_totalTmp);
                    } else if (mesure_totalCol.equals("min-1")) {
                        for (String i : measure_value) {
                            double v = Double.parseDouble(i);
                            if (v < measure_totalTmp) {
                                measure_totalTmp = v;
                            }
                        }
                        total_measure_totalMap7.put(ckey, measure_totalTmp);
                    } else {
                        for (String i : measure_value) {
                            measure_totalTmp += Double.parseDouble(i);
                        }
                        total_measure_totalMap7.put(ckey, measure_totalTmp);
                    }

                    // 数值小计
                    for (int j = 0; j < measure_arr.length; j++) {

                        String submeasureKey = dkey + "△" + measure_arr[j];
                        double submeasureTmp = total_subMeasureSum7.getOrDefault(submeasureKey, 0.00);
                        if (submeasureTmp == 0.00 && rowsumtype[j + 1].equals("max-1")) {
                            submeasureTmp = -9999999999999.0;
                        } else if (submeasureTmp == 0.00 && rowsumtype[j + 1].equals("min-1")) {
                            submeasureTmp = 9999999999.0;
                        }

                        if (rowsumtype[j + 1].equals("max-1")) {
                            double v = Double.parseDouble(measure_value[j]);
                            if (v > submeasureTmp) {
                                submeasureTmp = v;
                            }
                            total_subMeasureSum7.put(submeasureKey, submeasureTmp);
                        } else if (rowsumtype[j + 1].equals("min-1")) {
                            double v = Double.parseDouble(measure_value[j]);
                            if (v < submeasureTmp) {
                                submeasureTmp = v;
                            }
                            total_subMeasureSum7.put(submeasureKey, submeasureTmp);
                        } else {
                            double v = Double.parseDouble(measure_value[j]);
                            submeasureTmp += v;
                            total_subMeasureSum7.put(submeasureKey, submeasureTmp);
                        }
                    }
                }

                // ===== 分类小计
                Map<String, String> sub_ttal = new HashMap<>();
                for (String k : result_bak.keySet()) {
                    List<String> dimension_list = splitArray(k.split("△"), 0, dimension_length);
                    List<String> compare_list = splitArray(k.split("△"), dimension_length, dimension_length + compare_length);
                    List<String> measure_list = splitArray(k.split("△"), dimension_length + compare_length, dimension_length + compare_length + measure_length);

                    for (int i = 1; i < dimension_list.size(); i++) {
                        String dkey = splitList(dimension_list, 0, i).stream().reduce((a, b) -> a + "△" + b).get() + "△小计";
                        if (dkey.split("△").length < dimension_length) {
                            for (int j = 0; j < (dimension_length - dkey.split("△").length); j++) {
                                dkey += "△";
                            }
                        }

                        String subtotal_tmp_key = dkey;
                        if (compare_list != null && compare_list.size() > 0) {
                            subtotal_tmp_key = dkey + "△" + compare_list.stream().reduce((a, b) -> a + "△" + b).get();
                        }
                        String[] tt = sub_ttal.getOrDefault(subtotal_tmp_key, mea.toString()).split("△");
                        StringBuilder res = new StringBuilder();
                        for (int j = 0; j < measure_list.size(); j++) {
                            double d = Double.parseDouble(tt[j]) + Double.parseDouble(measure_list.get(j));
                            if (j == measure_list.size() - 1) {
                                res.append(d);
                            } else {
                                res.append(d).append("△");
                            }
                        }
                        Double sumRes = Arrays.stream(res.toString().split("△")).map(Double::parseDouble).reduce((a, b) -> a + b).get();
                        sub_ttal.put(subtotal_tmp_key, res + "△" + res + "△" + sumRes + "△" + sumRes + "△columnSum_subtotal_");
                    }
                }

                Map<String, Double> sub_total_rowsumMap7 = new HashMap();
                Map<String, Double> sub_total_measure_totalMap7 = new HashMap();
                Map<String, Integer> sub_total_dimenkey2compareSize7 = new HashMap();
                Map<String, Double> sub_total_subMeasureSum7 = new HashMap();
                for (Map.Entry<String, String> en : sub_ttal.entrySet()) {
                    String key = en.getKey();
                    String value = en.getValue();

                    String[] valueArr = value.split("△");
                    int valueArrlength = valueArr.length;

                    String dkey = getKey(key, dimension_length);
                    String ckey = getKey(key, dimension_length + compare_length);


                    int dimenCompareSize = sub_total_dimenkey2compareSize7.getOrDefault(dkey, 0);
                    dimenCompareSize += 1;
                    dimenkey2compareSize.put(dkey, dimenCompareSize);


                    double rowsumTmp = sub_total_rowsumMap7.getOrDefault(dkey, 0.0);
                    double measure_totalTmp = sub_total_measure_totalMap7.getOrDefault(ckey, 0.0);

                    if (rowsumTmp == 0.00 && rowsumtype[0].equals("max-1")) {
                        rowsumTmp = -9999999;
                    } else if (rowsumTmp == 0.00 && rowsumtype[0].equals("min-1")) {
                        rowsumTmp = 9999999;
                    }
                    if (measure_totalTmp == 0.00 && rowsumtype[rowsumtype.length - 1].equals("max-1")) {
                        measure_totalTmp = -9999999;
                    } else if (measure_totalTmp == 0.00 && rowsumtype[rowsumtype.length - 1].equals("min-1")) {
                        measure_totalTmp = 9999999;
                    }


                    String[] measure_value = getKey(value, measure_length).split("△");
                    String rowsum = valueArr[valueArrlength - 1 - 1 - 1];
                    String mesure_total = valueArr[valueArrlength - 1 - 1];

                    String rowsumCol = rowsumtype[0];
                    String mesure_totalCol = rowsumtype[rowsumtype.length - 1];

                    //  行合计
                    if (rowsumCol.equals("max-1")) {
                        for (String i : measure_value) {
                            double v = Double.parseDouble(i);
                            if (v > rowsumTmp) {
                                rowsumTmp = v;
                            }
                        }
                        sub_total_rowsumMap7.put(dkey, rowsumTmp);
                    } else if (rowsumCol.equals("min-1")) {
                        for (String i : measure_value) {
                            double v = Double.parseDouble(i);
                            if (v < rowsumTmp) {
                                rowsumTmp = v;
                            }
                        }
                        sub_total_rowsumMap7.put(dkey, rowsumTmp);
                    } else {
                        for (String i : measure_value) {
                            rowsumTmp += Double.parseDouble(i);
                        }
                        sub_total_rowsumMap7.put(dkey, rowsumTmp);
                    }

                    //分列小计
                    if (mesure_totalCol.equals("max-1")) {
                        for (String i : measure_value) {
                            double v = Double.parseDouble(i);
                            if (v > measure_totalTmp) {
                                measure_totalTmp = v;
                            }
                        }
                        sub_total_measure_totalMap7.put(ckey, measure_totalTmp);
                    } else if (mesure_totalCol.equals("min-1")) {
                        for (String i : measure_value) {
                            double v = Double.parseDouble(i);
                            if (v < measure_totalTmp) {
                                measure_totalTmp = v;
                            }
                        }
                        sub_total_measure_totalMap7.put(ckey, measure_totalTmp);
                    } else {
                        for (String i : measure_value) {
                            measure_totalTmp += Double.parseDouble(i);
                        }
                        sub_total_measure_totalMap7.put(ckey, measure_totalTmp);
                    }

                    // 数值小计
                    for (int j = 0; j < measure_arr.length; j++) {

                        String submeasureKey = dkey + "△" + measure_arr[j];
                        double submeasureTmp = sub_total_subMeasureSum7.getOrDefault(submeasureKey, 0.00);
                        if (submeasureTmp == 0.00 && rowsumtype[j + 1].equals("max-1")) {
                            submeasureTmp = -9999999;
                        } else if (submeasureTmp == 0.00 && rowsumtype[j + 1].equals("min-1")) {
                            submeasureTmp = 9999999;
                        }

                        if (rowsumtype[j + 1].equals("max-1")) {
                            double v = Double.parseDouble(measure_value[j]);
                            if (v > submeasureTmp) {
                                submeasureTmp = v;
                            }
                            sub_total_subMeasureSum7.put(submeasureKey, submeasureTmp);
                        } else if (rowsumtype[j + 1].equals("min-1")) {
                            double v = Double.parseDouble(measure_value[j]);
                            if (v < submeasureTmp) {
                                submeasureTmp = v;
                            }
                            sub_total_subMeasureSum7.put(submeasureKey, submeasureTmp);
                        } else {
                            double v = Double.parseDouble(measure_value[j]);
                            submeasureTmp += v;
                            sub_total_subMeasureSum7.put(submeasureKey, submeasureTmp);
                        }
                    }
                }

                // add total result
                for (Map.Entry<String, String> i : totalSumMap.entrySet()) {
                    String key = i.getKey();
                    String dkey = getKey(key, dimension_length);
                    String ckey = getKey(key, dimension_length + compare_length);
                    String measure = getKey(i.getValue(), measure_length);
                    int size = total_dimenkey2compareSize7.getOrDefault(dkey, 1);
                    String submeasure = "△";
                    for (int j = 0; j < measure_arr.length; j++) {
                        double v = total_subMeasureSum7.getOrDefault(dkey + "△" + measure_arr[j], 0.00) / size;
                        if (rowsumtype[j + 1].equals("avg-1")) {
                            v = v / (size);
                        } else {
                            v = v;
                        }
                        submeasure = submeasure + (v) + "△";
                    }

                    double rowsum = total_rowsumMap7.getOrDefault(dkey, 0.00);
                    double measure_total = total_measure_totalMap7.getOrDefault(ckey, 0.00);

                    if (rowsumtype[0].equals("avg-1")) {
                        rowsum = rowsum / (size * measure_length);
                    } else {
                        rowsum = rowsum / (size);
                    }
                    if (rowsumtype[rowsumtype.length - 1].equals("avg-1")) {
                        measure_total = measure_total / (measure_length);
                    }

                    result_bak.put(i.getKey() + "△" + measure + "" + submeasure + "" + rowsum + "△" + measure_total + "△columnSum", 0.00);
                }

                // add sub result
                for (Map.Entry<String, String> i : sub_ttal.entrySet()) {
                    String key = i.getKey();
                    String dkey = getKey(key, dimension_length);
                    String ckey = getKey(key, dimension_length + compare_length);
                    String measure = getKey(i.getValue(), measure_length);
                    String submeasure = "△";
                    int size = sub_total_dimenkey2compareSize7.getOrDefault(dkey, 1);

                    for (int j = 0; j < measure_arr.length; j++) {
                        double v = sub_total_subMeasureSum7.getOrDefault(dkey + "△" + measure_arr[j], 0.00) / size;
                        if (rowsumtype[j + 1].equals("avg-1")) {
                            v = v / (size);
                        } else {
                            v = v;
                        }
                        submeasure = submeasure + v + "△";
                    }

                    double rowsum = sub_total_rowsumMap7.getOrDefault(dkey, 0.00);
                    double measure_total = sub_total_measure_totalMap7.getOrDefault(ckey, 0.00);

//                    int size = sub_total_dimenkey2compareSize7.getOrDefault(dkey, 1);
                    if (rowsumtype[0].equals("avg-1")) {
                        rowsum = rowsum / (size * measure_length);
                    } else /*if (rowsumtype[0].equals("sum-1"))*/ {
                        rowsum = rowsum / (size);
                    }
                    if (rowsumtype[rowsumtype.length - 1].equals("avg-1")) {
                        measure_total = measure_total / (measure_length);
                    }
                    result_bak.put(i.getKey() + "△" + measure + "" + submeasure + "" + rowsum + "△" + measure_total + "△columnSum_subtotal_", 0.00);
                }
            } else if (rowcol.equals("6") || rowcol.equals("4") || rowcol.equals("2")) {
                StrBuilder mea = new StrBuilder();
                for (int i = 0; i < measure_length; i++) {
                    if (i == measure_length - 1) {
                        mea.append("0.0");
                    } else {
                        mea.append("0.0△");
                    }
                }
                String total_key = "总计";
                for (int i = 1; i < dimension_length; i++) {
                    total_key += "△";
                }

                Map<String, String> totalSumMap = new HashMap<>();
                for (String k : result_bak.keySet()) {
                    List<String> compare_list = splitArray(k.split("△"), dimension_length, dimension_length + compare_length);
                    List<String> measure_list = splitArray(k.split("△"), dimension_length + compare_length, dimension_length + compare_length + measure_length);

                    String total_tmp_key = total_key;
                    if (compare_list != null && compare_list.size() > 0) {
                        total_tmp_key = total_tmp_key + "△" + compare_list.stream().reduce((a, b) -> a + "△" + b).get();
                    }

                    String[] tt = totalSumMap.getOrDefault(total_tmp_key, mea.toString()).split("△");
                    StringBuilder res = new StringBuilder();
                    for (int j = 0; j < measure_list.size(); j++) {
                        double d = Double.parseDouble(tt[j]) + Double.parseDouble(measure_list.get(j));
                        if (j == measure_list.size() - 1) {
                            res.append(d);
                        } else {
                            res.append(d).append("△");
                        }
                    }
                    totalSumMap.put(total_tmp_key, res.toString());
                }
                Map<String, String> sub_ttal = new HashMap<>();
                for (String k : result_bak.keySet()) {
                    List<String> dimension_list = splitArray(k.split("△"), 0, dimension_length);
                    List<String> compare_list = splitArray(k.split("△"), dimension_length, dimension_length + compare_length);
                    List<String> measure_list = splitArray(k.split("△"), dimension_length + compare_length, dimension_length + compare_length + measure_length);

                    for (int i = 1; i < dimension_list.size(); i++) {
                        String dkey = splitList(dimension_list, 0, i).stream().reduce((a, b) -> a + "△" + b).get() + "△小计";
                        if (dkey.split("△").length < dimension_length) {
                            for (int j = 0; j < (dimension_length - dkey.split("△").length); j++) {
                                dkey += "△";
                            }
                        }

                        String subtotal_tmp_key = dkey;
                        String total_tmp_key = total_key;
                        if (compare_list != null && compare_list.size() > 0) {
                            subtotal_tmp_key = subtotal_tmp_key + "△" + compare_list.stream().reduce((a, b) -> a + "△" + b).get();
                        }

                        String[] tt = sub_ttal.getOrDefault(subtotal_tmp_key, mea.toString()).split("△");
                        StringBuilder res = new StringBuilder();
                        for (int j = 0; j < measure_list.size(); j++) {
                            double d = Double.parseDouble(tt[j]) + Double.parseDouble(measure_list.get(j));
                            if (j == measure_list.size() - 1) {
                                res.append(d);
                            } else {
                                res.append(d).append("△");
                            }
                        }
                        sub_ttal.put(subtotal_tmp_key, res.toString());
                    }

                }

                if (rowcol.equals("4") && !rowcol.equals("2")) {
                    for (Map.Entry<String, String> en : totalSumMap.entrySet()) {
                        String key = String.format("%s△%s△columnSum", en.getKey(), en.getValue());
                        result_bak.put(key, 0.00);
                    }
                }
                if (!rowcol.equals("4") && rowcol.equals("2")) {
                    for (Map.Entry<String, String> en : sub_ttal.entrySet()) {
                        String key = String.format("%s△%s△columnSum_subtotal_", en.getKey(), en.getValue());
                        result_bak.put(key, 0.00);
                    }
                }
                if (rowcol.equals("6")) {
                    for (Map.Entry<String, String> en : totalSumMap.entrySet()) {
                        String key = String.format("%s△%s△columnSum", en.getKey(), en.getValue());
                        result_bak.put(key, 0.00);
                    }

                    for (Map.Entry<String, String> en : sub_ttal.entrySet()) {
                        String key = String.format("%s△%s△columnSum_subtotal_", en.getKey(), en.getValue());
                        result_bak.put(key, 0.00);
                    }
                }
            }

            return result_bak;
        }

        private static String getKey(String key,
                                     int i) {
            int index = -1;
            for (int j = 0; j < i; j++) {
                index = key.indexOf("△", index + 1);
            }
            if (index == -1) {
                return key;
            } else {
                return key.substring(0, index);
            }
        }

        private static void doCompare(int dimension_length,
                                      int compare_length,
                                      String[] measure_arr,
                                      String rowcol,
                                      Map<String, Double> result,
                                      Map<String, Double> dimension,
                                      Map<String, String> compareValue,
                                      String measure_key) {
            String compare_key = measure_key.substring(0, measure_key.lastIndexOf("△"));
            String compare_key_tmp = measure_key.substring(0, measure_key.lastIndexOf("△"));

            String[] all_keys = compare_key.split("△");

            String dimensionKeys = all_keys[0];
            for (int i = 1; i < dimension_length; i++) {
                try {
                    dimensionKeys = String.format("%s△%s", dimensionKeys, all_keys[i]);
                } catch (Exception e) {
                    dimensionKeys = String.format("%s△%s", dimensionKeys, "");
                }

            }
            String compareKeys = "";
            if (compare_length > 0) {
                compareKeys = all_keys[0];
                for (int i = (dimension_length + 1); i < (dimension_length + compare_length); i++) {
                    compareKeys = compareKeys + "△" + all_keys[i];
                }
            }

            double tmp_count = 0.00;
            double tmp_count2 = 0.00;

            for (int i = 0; i < measure_arr.length; i++) {
                String s = measure_arr[i];
                String tmp_key = String.format("%s△%s", compare_key, s);

                double v = Double.parseDouble(compareValue.getOrDefault(tmp_key, "0.00"));
                tmp_count = tmp_count + v;
                compare_key_tmp = String.format("%s△%s", compare_key_tmp, v);
            }
            if (rowcol.equals("1") || rowcol.equals("7")) {
                for (int i = 0; i < measure_arr.length; i++) {
                    String s = measure_arr[i];
                    double v = dimension.getOrDefault(dimensionKeys + "△" + s, 0.00);
                    tmp_count2 = tmp_count2 + v;
                    compare_key_tmp = String.format("%s△%s", compare_key_tmp, v);
                }

                compare_key_tmp = String.format("%s△%s", compare_key_tmp, tmp_count2);
                compare_key_tmp = String.format("%s△%s△", compare_key_tmp, tmp_count);
            } else {
                compare_key_tmp = String.format("%s△", compare_key_tmp);
            }
            result.put(compare_key_tmp, 0.00);
        }
    }

    public static String getTime(Calendar ca) {
        String m = Integer.toString(ca.get(Calendar.MONTH) + 1);
        String d = Integer.toString(ca.get(Calendar.DAY_OF_MONTH));
        if (m.length() < 2) {
            m = String.format("0%s", m);
        }
        if (d.length() < 2) {
            d = String.format("0%s", d);
        }

        return String.format("%d%s%s", ca.get(Calendar.YEAR), m, d);
    }

    public static String dayformat(String day, String dimen_mode, int format) {
        if (day.length() != 8 && day.length() != 19 && day.length() != 10) {
            return day;
        }
        Calendar ca = Calendar.getInstance();
        if (format == 19 || format == 10) {
            ca.set(Integer.parseInt(day.substring(0, 4)), Integer.parseInt(day.substring(5, 7)) - 1, Integer.parseInt(day.substring(8, 10)));
        } else if (format == 8) {
            ca.set(Integer.parseInt(day.substring(0, 4)), Integer.parseInt(day.substring(4, 6)) - 1, Integer.parseInt(day.substring(6, 8)));
        } else {
            return day;
        }

        if (dimen_mode.equals("y")) {
            return ca.get(Calendar.YEAR) + "年";
        } else if (dimen_mode.equals("yq")) {
            int m = ca.get(Calendar.MONTH);
            if (m >= 0 && m < 3) {
                return String.format("%d年1季度", ca.get(Calendar.YEAR));
            } else if (m >= 3 && m < 6) {
                return String.format("%d年2季度", ca.get(Calendar.YEAR));
            } else if (m >= 6 && m < 9) {
                return String.format("%d年3季度", ca.get(Calendar.YEAR));
            } else {
                return String.format("%d年4季度", ca.get(Calendar.YEAR));
            }
        } else if (dimen_mode.equals("ym")) {
            String mint = Integer.toString(ca.get(Calendar.MONTH) + 1);
            if (mint.length() < 2) {
                mint = String.format("0%s", mint);
            }

            return String.format("%d年%s月", ca.get(Calendar.YEAR), mint);
        } else if (dimen_mode.equals("yw")) {
            return String.format("%d年%d周", ca.get(Calendar.YEAR), ca.get(Calendar.WEEK_OF_YEAR));
        } else {
            String m = Integer.toString(ca.get(Calendar.MONTH) + 1);
            String d = Integer.toString(ca.get(Calendar.DAY_OF_MONTH));
            if (m.length() < 2) {
                m = String.format("0%s", m);
            }
            if (d.length() < 2) {
                d = String.format("0%s", d);
            }
            return String.format("%d年%s月%s日", ca.get(Calendar.YEAR), m, d);
        }
    }

    public static int hasRD(String input) {
        String s = "\\d+-\\d+-\\d+ \\d+:\\d+:\\d+";
        Pattern pattern = Pattern.compile(s);
        Matcher ma = pattern.matcher(input);
        if (ma.find()) return 19;
        else {
            s = "\\d+-\\d+-\\d+";
            pattern = Pattern.compile(s);
            ma = pattern.matcher(input);
            if (ma.find()) return 10;
            else {
                s = "^[0-9]*$";
                pattern = Pattern.compile(s);
                ma = pattern.matcher(input);
                if (ma.find()) return 8;
                else return 0;
            }
        }
    }

    public static List<String> splitArray(String[] arr, int a, int b) {
        List<String> newArr = new ArrayList<>();
        for (int i = a; i < b; i++) {
            newArr.add(arr[i]);
        }
        return newArr;
    }

    public static List<String> splitList(List<String> arr, int a, int b) {
        List<String> newArr = new ArrayList<>();
        for (int i = a; i < b; i++) {
            newArr.add(arr.get(i));
        }
        return newArr;
    }


    public static String arrshow(String[] arr) {
        StringBuilder sb = new StringBuilder();
        if (arr != null && arr.length > 0) {

            for (String s :
                    arr) {
                sb.append(s + "\t");

            }
        }
        return sb.toString();
    }

}
