package com.li.hive;


import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.parse.SemanticException;
import org.apache.hadoop.hive.ql.udf.generic.AbstractGenericUDAFResolver;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDAFEvaluator;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorUtils;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfo;
import org.apache.hadoop.hive.serde2.io.DoubleWritable;

/**
 * https://blog.csdn.net/kent7306/article/details/50110067
 */
public class GenericUdafMemberLevel2 extends AbstractGenericUDAFResolver {
    @Override
    public GenericUDAFEvaluator getEvaluator(TypeInfo[] parameters)
            throws SemanticException {

        return new GenericUdafMeberLevelEvaluator();
    }

    public static class GenericUdafMeberLevelEvaluator extends GenericUDAFEvaluator {
        private PrimitiveObjectInspector inputOI;
        private PrimitiveObjectInspector inputOI2;
        private PrimitiveObjectInspector outputOI;
        private DoubleWritable result;

        @Override
        // 确定各个阶段输入输出参数的数据格式ObjectInspectors
        /**
         * PARTIAL1, map阶段的map阶段
         * PARTIAL2, map端的Combiner阶段
         * FINAL, mapreduce的reduce阶段
         * COMPLETE mapreduce只有map，没有reduce
         */
        /**
         * 确定各个阶段输入输出参数的数据格式ObjectInspectorsj
         */
        public ObjectInspector init(Mode m, ObjectInspector[] parameters)
                throws HiveException {
            super.init(m, parameters);

            //init input //必须得有
            if (m == Mode.PARTIAL1 || m == Mode.COMPLETE) {
                inputOI = (PrimitiveObjectInspector) parameters[0];
                inputOI2 = (PrimitiveObjectInspector) parameters[1];
//              result = new DoubleWritable(0);
            }
            //init output
            if (m == Mode.PARTIAL2 || m == Mode.FINAL) {
                outputOI = (PrimitiveObjectInspector) parameters[0];
                result = new DoubleWritable(0);
                return PrimitiveObjectInspectorFactory.writableDoubleObjectInspector;
            } else {
                result = new DoubleWritable(0);
                return PrimitiveObjectInspectorFactory.writableDoubleObjectInspector;
            }

        }

        /**
         * class for storing count value.
         */
        static class SumAgg implements AggregationBuffer {
            boolean empty;
            double value;
        }

        /**
         * 保存数据聚集结果的类
         * @return
         * @throws HiveException
         */
        @Override
        //创建新的聚合计算的需要的内存，用来存储mapper,combiner,reducer运算过程中的相加总和。
        //使用buffer对象前，先进行内存的清空——reset
        public AggregationBuffer getNewAggregationBuffer() throws HiveException {
            SumAgg buffer = new SumAgg();
            reset(buffer);
            return buffer;
        }

        /**
         * 重置聚集结果
         * @param agg
         * @throws HiveException
         */
        @Override
        //重置为0
        //mapreduce支持mapper和reducer的重用，所以为了兼容，也需要做内存的重用。
        public void reset(AggregationBuffer agg) throws HiveException {
            ((SumAgg) agg).value = 0.0;
            ((SumAgg) agg).empty = true;
        }

        private boolean warned = false;

        //迭代
        //只要把保存当前和的对象agg，再加上输入的参数，就可以了。

        /**
         * map阶段，迭代处理输入sql传过来的列数据
         * @param agg
         * @param parameters
         * @throws HiveException
         */
        @Override
        public void iterate(AggregationBuffer agg, Object[] parameters)
                throws HiveException {
            // parameters == null means the input table/split is empty
            if (parameters == null) {
                return;
            }
            try {
                double flag = PrimitiveObjectInspectorUtils.getDouble(parameters[1], inputOI2);
                if (flag > 1.0)   //参数条件
                    merge(agg, parameters[0]);   //这里将迭代数据放入combiner进行合并
            } catch (NumberFormatException e) {
                if (!warned) {
                    warned = true;
                }
            }

        }

        /**
         * combiner合并map返回的结果，还有reducer合并mapper或combiner返回的结果。
         * @param agg
         * @param partial
         */
        @Override
        //这里的操作就是具体的聚合操作。
        public void merge(AggregationBuffer agg, Object partial) {
            if (partial != null) {
                // 通过ObejctInspector取每一个字段的数据
                if (inputOI != null) {
                    double p = PrimitiveObjectInspectorUtils.getDouble(partial,
                            inputOI);
                    ((SumAgg) agg).value += p;
                } else {
                    double p = PrimitiveObjectInspectorUtils.getDouble(partial,
                            outputOI);
                    ((SumAgg) agg).value += p;
                }
            }
        }

        /**
         * // map与combiner结束返回结果，得到部分数据聚集结果
         * @param agg
         * @return
         */
        @Override
        public Object terminatePartial(AggregationBuffer agg) {
            return terminate(agg);
        }

        /**
         * reducer阶段，输出最终结果
         * @param agg
         * @return
         */
        @Override
        public Object terminate(AggregationBuffer agg) {
            SumAgg myagg = (SumAgg) agg;
            result.set(myagg.value);
            return result;
        }
    }
}
