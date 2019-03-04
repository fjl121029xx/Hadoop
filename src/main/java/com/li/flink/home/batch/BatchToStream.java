package com.li.flink.home.batch;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.io.InputFormat;
import org.apache.flink.api.common.io.OutputFormat;
import org.apache.flink.api.common.operators.Keys;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.aggregation.AggregationFunction;
import org.apache.flink.api.java.aggregation.SumAggregationFunction;
import org.apache.flink.api.java.io.TextInputFormat;
import org.apache.flink.api.java.io.TextOutputFormat;
import org.apache.flink.api.java.operators.*;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.core.fs.Path;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

import java.util.List;

public class BatchToStream {

//    public static void main(String[] args) throws Exception {
//
//        // set up the execution environment
//        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
//
//        env.setParallelism(1);
//        // get input data
//        DataSet<String> text = env.fromElements(
//                "To be, or not to be,--that is the question:--",
//                "Whether 'tis nobler in the mind to suffer",
//                "The slings and arrows of outrageous fortune",
//                "Or to take arms against a sea of troubles,"
//        );
//        //输入文件
////        DataSet<String> a = env.readTextFile("F:\\test.txt");
//
//        DataSet<Tuple2<String, Integer>> b = text.flatMap(new LineSplitter());
//
//        //DataSet<Tuple2<String, Integer>> d = b.sum(1);
//        DataSet<Tuple2<String, Integer>> c = b.groupBy(0)
//                .sum(1);
//        //sink必须单独写？？？？，放在上一行后头会报错,原因是因为返回的是datasink类型
//        c.writeAsText("F:\\output\\batchToStream");
//
////		DataSet<Tuple2<String, Integer>> counts = env.readTextFile("/home/maqy/桌面/out/test")
////				// split up the lines in pairs (2-tuples) containing: (word,1)
////				.flatMap(new LineSplitter())
////				// group by the tuple field "0" and sum up tuple field "1"
////				.groupBy(0)
////				.sum(1);
////
////		// execute and print result
////		counts.writeAsText("/home/maqy/桌面/out/out1");
//
//        StreamExecutionEnvironment envStream = batchToStream(env);
//        //执行程序的是流的Environment
//        //env.execute("batch job~~~~~~~~~~~~~~");
//        envStream.execute("StreamJob~~~~~~~~~~~~~");
//
//    }
//
//    //实现批的环境到流的环境的转换,传入envBatch，返回StreamExecutionEnvironment
//    public static StreamExecutionEnvironment batchToStream(ExecutionEnvironment envBatch) throws Exception {
//        //创建一个新的流环境，用于返回的
//        StreamExecutionEnvironment envStream = StreamExecutionEnvironment.getExecutionEnvironment();
//        //设置并行度只能在这里设置，不然没用
//        envStream.setParallelism(1);
//
//        //这里考虑下用DataSet 还是用 Environment,得到环境中的sinks
//        List<DataSink<?>> batchSinks = envBatch.getSinks();
//        for (DataSink dataSink : batchSinks) {
//            //先定义一个数据流
//            DataStream first = null;
//            //对每个sink进行操作，找到源头？
//            DataSet dataSetLast = dataSink.getDataSet();
//            //Operator继承了DataSet
//
//            DataSet p = dataSetLast;
//
//            //不这么写，first会为null，初步判断是因为first在datasource时创建的时候，重新定向到新的地址了
//            first = preVisit(p, envStream, first);
//            //转换sink
//            OutputFormat dataSinkOutputFormat = dataSink.getFormat();
//            if (dataSinkOutputFormat instanceof TextOutputFormat) {
//                System.out.println("dataSinkOutputFormat is a TextOutputFormat");
//                Path path = ((TextOutputFormat) dataSinkOutputFormat).getOutputFilePath();
//                first.writeUsingOutputFormat(new TextOutputFormat(path));
//            }
//            //first.addSink(dataSink.)
//        }
//        //System.out.println("size:"+batchSinks.size());
//        //返回流环境，后期还可以考虑是否可以合并到原本存在的流环境中，甚至将各个datastream也加以返回，重新利用
//        return envStream;
//    }
//
//    //从尾向前遍历，并转化
//    public static DataStream preVisit(DataSet dataSet, StreamExecutionEnvironment envStream, DataStream first) {
//
//        if (!(dataSet instanceof DataSource)) {       // && (dataSet != null)
//            //如果没有到DataSource节点，则递归
//            first = preVisit(getPre(dataSet), envStream, first);
//        }
//        //对节点进行相应的操作
//        if (dataSet == null) {
//            System.out.println("source is null");
//        } else if (dataSet instanceof DataSource) {
//            //得到源头后，看源属于哪一种类型，然后添加到流中
//            //而且从dataSource可以得到输出的数据类型
//            //这里可以得到输入的数据的类型，但还不知道怎么用到DataStream中
////			TypeInformation sourceTypeInfo = ((DataSource) dataSet).getResultType();
////			Class sourceType = sourceTypeInfo.getTypeClass();
//            //System.out.println("sourceType:"+sourceTypeInfo.getTypeClass());
//            InputFormat inputFormat = ((DataSource) dataSet).getInputFormat();
//            if (inputFormat instanceof TextInputFormat) {
//                //后期可以考虑是否可以直接转换算子
//                System.out.println("inputFormat is TextInputFormat");
//                String filePath = ((TextInputFormat) inputFormat).getFilePath().toString();
//                System.out.println("输入的文件路径为：" + filePath);
//                //这里相当于让first重新指向一个新地址了？？？？
//                first = envStream.readTextFile(filePath);
//            }
//        } else if (dataSet instanceof SingleInputOperator) {
//            System.out.println("SingleInputOperator yes");
//            //如果是SingleInputOperator，再判断具体类型,SingleInputOperator中有DataSet 类型的 input。
//            //((SingleInputOperator) dataSet).getInput();
//            if (dataSet instanceof AggregateOperator) {
//                System.out.println("AggregateOperator yes");
//                //可以有多个aggregationFunctions，还有个对应的List<Integer> fields,默认好像是4
//                List<AggregationFunction<?>> aggregationFunctions = ((AggregateOperator) dataSet).getAggregationFunctions();
//                List<Integer> fields = ((AggregateOperator) dataSet).getFields();
//
//                //首先要得到是否被groupBy过了，即是否可以得到UnsortedGrouping类型,得不到则是null
//                Grouping grouping = ((AggregateOperator) dataSet).getGrouping();
//                //如果grouping不是null的话，则说明经过了groupBy，则进行相应的转换
//                if (grouping != null) {
//                    int position = 0; //暂时只考虑一个的情况
//                    if (grouping instanceof SortedGrouping) {
//                        System.out.println("SortedGrouping yes");
//                    } else if (grouping instanceof UnsortedGrouping) {
//                        System.out.println("UnsortedGrouping yes");
//                        //Keys中有keyFields和originalKeyTypes，这里的后者是String
//                        Keys keys = grouping.getKeys();
//                        if (keys instanceof Keys.ExpressionKeys) {
//                            System.out.println("Keys.ExpressionKeys yes");
//                            //这里还没弄清楚有多个时的意思
//                            int numOfKeyFields = keys.getNumberOfKeyFields();
//                            int[] positions = keys.computeLogicalKeyPositions();
//                            if (numOfKeyFields == 1) {
//                                position = positions[0];
//                            }
//                            //这里还方便了我，在流中不用考虑UnsortedGrouping这种东西
//                            first = first.keyBy(position);
//                        } else if (keys instanceof Keys.SelectorFunctionKeys) {
//                            System.out.println("Keys.SelectorFunctionKeys yes");
//                        }
//                    }
//                }
//
//                //先考虑数目为1的情况，因为需要先keyby再sum
//                if (aggregationFunctions.size() == 1 && fields.size() == 1) {
//                    if (aggregationFunctions.get(0) instanceof SumAggregationFunction) {
//                        if (first instanceof KeyedStream) {
//                            first = ((KeyedStream) first).sum(fields.get(0));
//                        } else {
//                            System.out.println("Stream中sum的话一定要keyby么，似乎是。。。");
//                        }
//                    }
//                }
//
//                //AggregateOperator aggregateOperator = (AggregateOperator) dataSet;
//
//            } else if (dataSet instanceof SingleInputUdfOperator) {
//                System.out.println("SingleInputUdfOperator yes");
//                if (dataSet instanceof FlatMapOperator) {
//                    System.out.println("FlatMapOperator yes");
//                    FlatMapFunction flatMapFunction = ((FlatMapOperator) dataSet).getFlatMapFunction();
//                    first = first.flatMap(flatMapFunction);
//                }
//            } else {
//                System.out.println("Not sure what SingleInputOperator");
//            }
//        } else if (dataSet instanceof TwoInputOperator) {
//            System.out.println("TwoInputOperator yes");
//        } else {
//            System.out.println("not sure what Operator");
//        }
//
//        return first;
//    }
//
//    //得到一个前驱
//    public static DataSet getPre(DataSet dataSet) {
//        if (dataSet instanceof Operator) {
//            System.out.println("Operator yes");
//            if (dataSet instanceof DataSource) {
//                System.out.println("DataSource yes");
//                return (DataSource) dataSet;
//            } else if (dataSet instanceof SingleInputOperator) {
//                System.out.println("SingleInputOperator yes");
//                //如果是SingleInputOperator，再判断具体类型,SingleInputOperator中有DataSet 类型的 input。
//                return ((SingleInputOperator) dataSet).getInput();
//            } else if (dataSet instanceof TwoInputOperator) {
//                System.out.println("TwoInputOperator yes");
//                /////
//            } else {
//                System.out.println("not sure what Operator");
//                /////
//            }
//        } else {
//            System.out.println("no Operator");
//            /////
//        }
//        return null;
//    }
//
//    /**
//     * Implements the string tokenizer that splits sentences into words as a user-defined
//     * FlatMapFunction. The function takes a line (String) and splits it into
//     * multiple pairs in the form of "(word,1)" (Tuple2<String, Integer>).
//     */
//    public static final class LineSplitter implements FlatMapFunction<String, Tuple2<String, Integer>> {
//
//        @Override
//        public void flatMap(String value, Collector<Tuple2<String, Integer>> out) {
//            // normalize and split the line
//            String[] tokens = value.toLowerCase().split("\\W+");
//
//            // emit the pairs
//            for (String token : tokens) {
//                if (token.length() > 0) {
//                    out.collect(new Tuple2<String, Integer>(token, 1));
//                }
//            }
//        }
//    }
}
