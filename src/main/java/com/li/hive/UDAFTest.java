package com.li.hive;

import org.apache.hadoop.hive.ql.exec.UDFArgumentTypeException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.parse.SemanticException;
import org.apache.hadoop.hive.ql.udf.generic.AbstractGenericUDAFResolver;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDAFEvaluator;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorUtils;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfo;
import org.apache.hadoop.io.LongWritable;

/**
 * 开发UDAF通用有两个步骤
 * 第一个是编写resolver类，resolver负责类型检查，操作符重载。
 * 第二个是编写evaluator类，evaluator真正实现UDAF的逻辑
 *
 * 执行过程与UDF类似，该Java、类的功能是第一列的值大于第二列计数加1。
 */
public class UDAFTest extends AbstractGenericUDAFResolver {


    @Override
    public GenericUDAFEvaluator getEvaluator(TypeInfo[] info)
            throws SemanticException {
        if (info.length != 2) {
            throw new UDFArgumentTypeException(info.length - 1,
                    "Exactly two argument is expected.");
        }

        //返回处理逻辑的类
        return new GenericEvaluate();

    }

    public static class GenericEvaluate extends GenericUDAFEvaluator {

        private LongWritable result;
        private PrimitiveObjectInspector inputIO1;
        private PrimitiveObjectInspector inputIO2;

        //这个方法map与reduce阶段都需要执行

        /**
         * map阶段：parameters长度与udaf输入的参数个数有关
         * reduce阶段：parameters长度为1
         */
        //初始化
        @Override
        public ObjectInspector init(Mode m, ObjectInspector[] parameters)
                throws HiveException {
            super.init(m, parameters);

            //返回最终的结果
            result = new LongWritable(0);

            inputIO1 = (PrimitiveObjectInspector) parameters[0];
            if (parameters.length > 1) {
                inputIO2 = (PrimitiveObjectInspector) parameters[1];
            }

            return PrimitiveObjectInspectorFactory.writableBinaryObjectInspector;
        }

        //map阶段
        @Override
        public void iterate(AggregationBuffer agg, Object[] parameters)//agg缓存结果值
                throws HiveException {

            assert (parameters.length == 2);

            if (parameters == null || parameters[0] == null || parameters[1] == null) {
                return;
            }

            double base = PrimitiveObjectInspectorUtils.getDouble(parameters[0], inputIO1);
            double tmp = PrimitiveObjectInspectorUtils.getDouble(parameters[1], inputIO2);

            if (base > tmp) {
                ((CountAgg) agg).count++;
            }

        }

        //获得一个聚合的缓冲对象，每个map执行一次
        @Override
        public AggregationBuffer getNewAggregationBuffer() throws HiveException {

            CountAgg agg = new CountAgg();

            reset(agg);

            return agg;
        }

        //自定义类用于计数
        public static class CountAgg implements AggregationBuffer {
            long count;//计数，保存每次临时的结果
        }

        //重置
        @Override
        public void reset(AggregationBuffer countagg) throws HiveException {
            CountAgg agg = (CountAgg) countagg;
            agg.count = 0;
        }

        //该方法当做iterate执行后，部分结果返回。
        @Override
        public Object terminatePartial(AggregationBuffer agg)
                throws HiveException {

            result.set(((CountAgg) agg).count);

            return result;
        }


        @Override
        public void merge(AggregationBuffer agg, Object partial)
                throws HiveException {
            if (partial != null) {
                long p = PrimitiveObjectInspectorUtils.getLong(partial, inputIO1);
                ((CountAgg) agg).count += p;
            }
        }

        @Override
        public Object terminate(AggregationBuffer agg) throws HiveException {
            result.set(((CountAgg) agg).count);
            return result;
        }
    }
}
