package com.li.hive.example.udf.v1;

import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.exec.UDFArgumentLengthException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDF;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.StringObjectInspector;

import java.util.Calendar;

@Description(name = "udfreportdateformat",
        value = "_FUNC_(date, format) - " +
                "date:String [20200316/2020-03-16/2020-03-16 18:11:39] , day options[y,yq,ym,yw,ymd,yh]")
public class UDFReportDateFormat extends GenericUDF {

    private StringObjectInspector stringObjectInspector01;
    private StringObjectInspector stringObjectInspector02;

    @Override
    public ObjectInspector initialize(ObjectInspector[] objectInspectors) throws UDFArgumentException {
        if (objectInspectors.length != 2) {
            throw new UDFArgumentLengthException("arrayContainsExample only takes 2 arguments: String,  String");
        }
        // 1. 检查是否接收到正确的参数类型
        ObjectInspector a = objectInspectors[0];
        ObjectInspector b = objectInspectors[1];
        if (!(a instanceof StringObjectInspector) || !(b instanceof StringObjectInspector)) {
            throw new UDFArgumentException("first argument must be a String, second argument must be a String");
        }

        this.stringObjectInspector01 = (StringObjectInspector) a;
        this.stringObjectInspector02 = (StringObjectInspector) b;

        return PrimitiveObjectInspectorFactory.javaStringObjectInspector;
    }

    public Calendar createCalendar(Integer[] iarr) {
        Calendar ca = Calendar.getInstance();
        ca.set(iarr[0],
                iarr[1],
                iarr[2],
                iarr[3],
                iarr[4],
                iarr[5]);
        return ca;
    }

    public Integer[] createDateArr(String date) throws UDFArgumentLengthException {

        if (date == null) {
            throw new UDFArgumentLengthException("date is null , " + date);
        }
        if (date.length() == 19) {
            return new Integer[]{Integer.parseInt(date.substring(0, 4)),
                    (Integer.parseInt(date.substring(5, 7)) - 1),
                    Integer.parseInt(date.substring(8, 10)),
                    Integer.parseInt(date.substring(11, 13)),
                    Integer.parseInt(date.substring(14, 16)),
                    Integer.parseInt(date.substring(17, 19))};
        } else if (date.length() == 10) {
            return new Integer[]{Integer.parseInt(date.substring(0, 4)),
                    (Integer.parseInt(date.substring(5, 7)) - 1),
                    Integer.parseInt(date.substring(8, 10)),
                    0, 0, 0};
        } else {
            return new Integer[]{Integer.parseInt(date.substring(0, 4)),
                    (Integer.parseInt(date.substring(4, 6)) - 1),
                    Integer.parseInt(date.substring(6, 8)),
                    0, 0, 0};
        }
    }

    @Override
    public Object evaluate(DeferredObject[] deferredObjects) throws HiveException {

        String date = this.stringObjectInspector01.getPrimitiveJavaObject(deferredObjects[0].get());
        String format = this.stringObjectInspector02.getPrimitiveJavaObject(deferredObjects[1].get());

        if (date == null) {
            throw new UDFArgumentLengthException("date is null , " + date);
        }

        String result = "";
        switch (format) {
            case "y":
                result = this.createDateArr(date)[0] + "年";
                break;
            case "yq":
                int m = this.createDateArr(date)[1];
                if (m < 3) {
                    result = this.createDateArr(date)[0] + "年1季度";
                    break;
                } else if (m < 6) {
                    result = this.createDateArr(date)[0] + "年2季度";
                    break;
                } else if (m < 9) {
                    result = this.createDateArr(date)[0] + "年3季度";
                    break;
                } else {
                    result = this.createDateArr(date)[0] + "年4季度";
                    break;
                }
            case "ym": {
                String mint = Integer.toString((this.createDateArr(date)[1] + 1));
                if (mint.length() < 2) {
                    mint = "0" + mint;
                }

                result = String.format("%d年%s月", this.createDateArr(date)[0], mint);
                break;
            }
            case "yw":
                Calendar ca = this.createCalendar(this.createDateArr(date));
                result = String.format("%d年%d周", ca.get(Calendar.YEAR), ca.get(Calendar.WEEK_OF_YEAR));
                break;
            case "ymd":
                String mon = Integer.toString(this.createDateArr(date)[1] + 1);
                String d = Integer.toString(this.createDateArr(date)[2]);
                if (mon.length() < 2) {
                    mon = "0" + mon;
                }
                if (d.length() < 2) {
                    d = "0" + d;
                }
                result = String.format("%d年%s月%s日", this.createDateArr(date)[0], mon, d);
                break;
            case "ymdh":
                ca = this.createCalendar(this.createDateArr(date));
                String mond = Integer.toString(this.createDateArr(date)[1] + 1);
                String day = Integer.toString(this.createDateArr(date)[2]);
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

    @Override
    public String getDisplayString(String[] children) {
        return children[0];
    }
}
