package com.li.hive.udaf;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDFArgumentTypeException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.parse.SemanticException;
import org.apache.hadoop.hive.ql.udf.generic.AbstractGenericUDAFResolver;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDAFEvaluator;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorUtils;
import org.apache.hadoop.hive.serde2.typeinfo.PrimitiveTypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfo;
import org.apache.hadoop.util.StringUtils;
import org.apache.hadoop.io.Text;

//https://blog.csdn.net/weixin_39469127/article/details/89766266

@Description(name = "mycolconcat", value = "_FUNC_(x) - Returns the concat of a set of cols")
public class ConcatUDAF extends AbstractGenericUDAFResolver {
    static final Log LOG = LogFactory.getLog(ConcatUDAF.class.getName());

    @Override
    public GenericUDAFEvaluator getEvaluator(TypeInfo[] parameters)
            throws SemanticException {
        if (parameters.length != 1) {
            throw new UDFArgumentTypeException(parameters.length - 1,
                    "Exactly one argument is expected.");
        }

        if (parameters[0].getCategory() != ObjectInspector.Category.PRIMITIVE) {
            throw new UDFArgumentTypeException(0,
                    "Only primitive type arguments are accepted but "
                            + parameters[0].getTypeName() + " is passed.");
        }
        switch (((PrimitiveTypeInfo) parameters[0]).getPrimitiveCategory()) {
            case BYTE:
            case SHORT:
            case INT:
            case LONG:
            case FLOAT:
            case DOUBLE:
            case STRING:
            case TIMESTAMP:
                return new ConcatUDAFEvaluator();
            case BOOLEAN:
            default:
                throw new UDFArgumentTypeException(0,
                        "Only numeric or string type arguments are accepted but "
                                + parameters[0].getTypeName() + " is passed.");
        }
    }

    public static class ConcatUDAFEvaluator extends GenericUDAFEvaluator {

        //Mode的各部分的输入都是String类型，输出也是，所以对应的OI实例也都一样
        PrimitiveObjectInspector inputOI;

        Text partialResult;

        Text result;

        @Override
        public ObjectInspector init(Mode mode, ObjectInspector[] parameters)
                throws HiveException {
            assert (parameters.length == 1);
            super.init(mode, parameters);

            // init input
            inputOI = (PrimitiveObjectInspector) parameters[0];

            // init output
            result = new Text("");
            return PrimitiveObjectInspectorFactory.writableStringObjectInspector;
        }

        static class ConcatAgg implements AggregationBuffer {
            StringBuilder line = new StringBuilder("");
        }

        ;

        @Override
        public AggregationBuffer getNewAggregationBuffer() throws HiveException {
            ConcatAgg result = new ConcatAgg();
            reset(result);
            return result;
        }

        @Override
        public void reset(AggregationBuffer agg) throws HiveException {
            ConcatAgg myagg = (ConcatAgg) agg;
            myagg.line.delete(0, myagg.line.length());
        }

        boolean warned = false;

        @Override
        public void iterate(AggregationBuffer agg, Object[] parameters) throws HiveException {
            Object p = parameters[0];
            if (p != null) {
                ConcatAgg myagg = (ConcatAgg) agg;
                try {
                    String v = PrimitiveObjectInspectorUtils.getString(p, inputOI);
                    if (myagg.line.length() == 0)
                        myagg.line.append(v);
                    else
                        myagg.line.append("," + v);
                } catch (RuntimeException e) {
                    if (!warned) {
                        warned = true;
                        LOG.warn(getClass().getSimpleName() + " "
                                + StringUtils.stringifyException(e));
                        LOG.warn(getClass().getSimpleName()
                                + " ignoring similar exceptions.");
                    }
                }
            }
        }

        @Override
        public Object terminatePartial(AggregationBuffer agg) throws HiveException {
            ConcatAgg myagg = (ConcatAgg) agg;
            result.set(myagg.line.toString());
            return result;
        }

        @Override
        public void merge(AggregationBuffer agg, Object partial) throws HiveException {
            if (partial != null) {
                try {
                    ConcatAgg myagg = (ConcatAgg) agg;
                    String v = PrimitiveObjectInspectorUtils.getString(partial, inputOI);
                    if (myagg.line.length() == 0)
                        myagg.line.append(v);
                    else
                        myagg.line.append("," + v);
                } catch (RuntimeException e) {
                    if (!warned) {
                        warned = true;
                        LOG.warn(getClass().getSimpleName() + " "
                                + StringUtils.stringifyException(e));
                        LOG.warn(getClass().getSimpleName()
                                + " ignoring similar exceptions.");
                    }
                }
            }
        }

        @Override
        public Object terminate(AggregationBuffer agg) throws HiveException {
            ConcatAgg myagg = (ConcatAgg) agg;
            result.set(myagg.line.toString());
            return result;
        }
    }
}
