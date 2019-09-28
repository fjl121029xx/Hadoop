package com.li.flink.home.table.user.defined.sources.batch;

import com.alibaba.fastjson.JSONObject;
import org.apache.flink.api.common.functions.MapPartitionFunction;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.hadoop.mapreduce.HadoopInputFormat;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.table.api.TableSchema;
import org.apache.flink.table.api.Types;
import org.apache.flink.table.sources.BatchTableSource;
import org.apache.flink.table.util.TableConnectorUtil;
import org.apache.flink.types.Row;
import org.apache.flink.util.Collector;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;

import java.io.IOException;

/**
 *
 *
 */
public class JsonSource implements BatchTableSource<Row> {

    private JsonSchema jsonSchema;
    private TableSchema tableSchema;

    private String[] getFieldNames() {
        return jsonSchema.getFieldNames();
    }

    public TypeInformation[] getFieldTypes() {

        TypeInformation[] fieldTypes = new TypeInformation[jsonSchema.getFieldNames().length];
        fieldTypes[0] = Types.LONG();
        fieldTypes[1] = Types.INT();
        fieldTypes[2] = Types.LONG();

        return fieldTypes;
    }


    @Override
    public DataSet<Row> getDataSet(ExecutionEnvironment env) {

        try {
            Job job = Job.getInstance();
            HadoopInputFormat<LongWritable, Text> hadoopIF = new HadoopInputFormat<>(new TextInputFormat()
                    , LongWritable.class, Text.class, job);
            TextInputFormat.addInputPath(job, new Path(""));

            DataSource<Tuple2<LongWritable, Text>> text = env.createInput(hadoopIF);

            DataSet<Row> result = text.mapPartition(new MapPartitionFunction<Tuple2<LongWritable, Text>, Row>() {
                @Override
                public void mapPartition(Iterable<Tuple2<LongWritable, Text>> values, Collector<Row> out) throws Exception {

                    for (Tuple2<LongWritable, Text> line : values) {

                        JSONObject jo = JSONObject.parseObject(line.f1.toString());
                        Row row = Row.of(jo.getLong("k"), jo.getInteger("v"), jo.getLong("t"));
                        out.collect(row);
                    }
                }
            });

            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public TypeInformation<Row> getReturnType() {
        return new RowTypeInfo(getFieldTypes(), getFieldNames());
    }

    @Override
    public TableSchema getTableSchema() {
        return new TableSchema(getFieldNames(), getFieldTypes());
    }

    @Override
    public String explainSource() {
        return TableConnectorUtil.generateRuntimeName(this.getClass(), getFieldNames());
    }
}


