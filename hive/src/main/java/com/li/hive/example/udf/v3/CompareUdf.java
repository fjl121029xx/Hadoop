package com.li.hive.example.udf.v3;

import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.exec.UDFArgumentLengthException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDF;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.TimestampObjectInspector;

@Description(name = "udfcompare",
        value = "_FUNC_() - " +
                "")
public class CompareUdf extends GenericUDF {
    private TimestampObjectInspector dateObjectInspector01;

    @Override
    public ObjectInspector initialize(ObjectInspector[] objectInspectors) throws UDFArgumentException {
        if (objectInspectors.length != 1) {
            throw new UDFArgumentLengthException("arrayContainsExample only takes 2 arguments: date,  date");
        }
        // 1. 检查是否接收到正确的参数类型
        ObjectInspector a = objectInspectors[0];

        if (!(a instanceof TimestampObjectInspector)) {
            throw new UDFArgumentException(String.format("first argument must be a Timestamp, second argument must be a Timestamp %s %s", a.getClass()));
        }

        this.dateObjectInspector01 = (TimestampObjectInspector) a;

        return PrimitiveObjectInspectorFactory.javaLongObjectInspector;
    }


    @Override
    public Object evaluate(DeferredObject[] deferredObjects) throws HiveException {

        return null;
    }

    @Override
    public String getDisplayString(String[] children) {
        return children[0];
    }
}
