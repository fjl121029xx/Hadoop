package com.li.hive;

import org.apache.hadoop.hive.ql.exec.UDAF;
import org.apache.hadoop.hive.ql.exec.UDAFEvaluator;
import org.apache.hadoop.io.DoubleWritable;

/**
 * inin（）函数可以用来做初始化操作，一般会将统计变量置空，重置内部状态
 iterate方法是函数的入口，参数个数和类型和udaf实现功能息息相关
 terminatePartial 需要部分聚集是调用该函数，因为计算是不同的数据块会分到不同的map端，计算之后再传输到reduce端，很多计算是可以在map后面继续计算一次，比如求最大值（求平均值则不可以），这个时候就会调用terminatePartial函数，函数必须返回一个封装了聚集计算当前状态的对象，传入reduce端
 merge函数，数据传输到reduce端前调用该函数，所以入仓必须和terminatePartial返回值相同
 terminate函数：hive最终聚集时会调用terminate，返回计算结果
 */
public class  Mean extends UDAF {
    public static class MeanDoubleUDAFEval implements UDAFEvaluator {
        public static class PartialResult {
            double sum;
            long count;
        }

        private PartialResult pResult;

        @Override
        public void init() {
            pResult = null;
        }

        public boolean iterate(DoubleWritable value) {
            if (value == null) {
                return true;
            }
            if (pResult == null) {
                pResult = new PartialResult();
            }
            pResult.sum += value.get();
            pResult.count++;
            return true;
        }

        public PartialResult terminatePartial() {
            return pResult;
        }

        public boolean merge(PartialResult other) {
            if (other == null) {
                return true;
            }
            if (pResult == null) {
                pResult = new PartialResult();
            }
            pResult.sum += other.sum;
            pResult.count++;
            return true;
        }

        public DoubleWritable terminate() {
            if (pResult == null) {
                return null;
            }
            return new DoubleWritable(pResult.sum / pResult.count);
        }
    }
}
