package com.li.hive.example.udf.v2.dif;

import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.exec.UDFArgumentLengthException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDF;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.TimestampObjectInspector;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

@Description(name = "udfhourdiff",
        value = "_FUNC_(start, end) - " +
                "date:Timestamp , day:Timestamp")
public class UDFHourDiff extends GenericUDF {

    private TimestampObjectInspector dateObjectInspector01;
    private TimestampObjectInspector dateObjectInspector02;

    @Override
    public ObjectInspector initialize(ObjectInspector[] objectInspectors) throws UDFArgumentException {
        if (objectInspectors.length != 2) {
            throw new UDFArgumentLengthException("arrayContainsExample only takes 2 arguments: date,  date");
        }
        // 1. 检查是否接收到正确的参数类型
        ObjectInspector a = objectInspectors[0];
        ObjectInspector b = objectInspectors[1];

        if (!(a instanceof TimestampObjectInspector) || !(b instanceof TimestampObjectInspector)) {
            throw new UDFArgumentException(String.format("first argument must be a Timestamp, second argument must be a Timestamp %s %s", a.getClass(), b.getClass()));
        }

        this.dateObjectInspector01 = (TimestampObjectInspector) a;
        this.dateObjectInspector02 = (TimestampObjectInspector) b;

        return PrimitiveObjectInspectorFactory.javaDoubleObjectInspector;
    }


    @Override
    public Object evaluate(DeferredObject[] deferredObjects) throws HiveException {

        Timestamp start = this.dateObjectInspector01.getPrimitiveJavaObject(deferredObjects[0].get());
        Timestamp end = this.dateObjectInspector02.getPrimitiveJavaObject(deferredObjects[1].get());
        if (start == null || end == null) {
            throw new UDFArgumentLengthException(String.format("args has null :=} start is %s end is %s", start, end));
        }
        Calendar startCa = Calendar.getInstance();
        startCa.setTime(new Date(start.getTime()));
        Calendar endCa = Calendar.getInstance();
        endCa.setTime(new Date(end.getTime()));

        BigDecimal startSecond = new BigDecimal(startCa.getTime().getTime());
        BigDecimal endSecond = new BigDecimal(endCa.getTime().getTime());
        BigDecimal div = new BigDecimal(60 * 60 * 1000);
        try {
            BigDecimal result = endSecond.subtract(startSecond).divide(div).setScale(2, BigDecimal.ROUND_UP);
            return result.doubleValue();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(String.format("math start %s end %s div %s", startSecond.toString(), endSecond, div));
        }

    }

    @Override
    public String getDisplayString(String[] children) {
        return children[0];
    }


}
