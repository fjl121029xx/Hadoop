package com.li.mr.mr2mysql;

import com.li.mr.areapartition.AreaPartitioner;
import com.li.mr.areapartition.FlowSumArea;
import com.li.mr.flowsum.FlowBean;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.filecache.DistributedCache;
import org.apache.hadoop.mapreduce.lib.db.DBConfiguration;
import org.apache.hadoop.mapreduce.lib.db.DBInputFormat;
import org.apache.hadoop.mapreduce.lib.db.DBOutputFormat;
import org.apache.hadoop.mapreduce.lib.db.DBWritable;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Mysql2MR {

    public static class FlowRecord implements Writable, DBWritable {

        String phone;
        Long up_flow;
        Long down_flow;
        Long sum_flow;

        public FlowRecord() {
        }

        public FlowRecord(String phone, Long up_flow, Long down_flow) {
            this.phone = phone;
            this.up_flow = up_flow;
            this.down_flow = down_flow;
            this.sum_flow = up_flow + down_flow;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public Long getUp_flow() {
            return up_flow;
        }

        public void setUp_flow(Long up_flow) {
            this.up_flow = up_flow;
        }

        public Long getDown_flow() {
            return down_flow;
        }

        public void setDown_flow(Long down_flow) {
            this.down_flow = down_flow;
        }

        public Long getSum_flow() {
            return sum_flow;
        }

        public void setSum_flow(Long sum_flow) {
            this.sum_flow = sum_flow;
        }

        @Override
        public void write(DataOutput out) throws IOException {

            Text.writeString(out, this.phone);
            out.writeLong(this.up_flow);
            out.writeLong(this.down_flow);
            out.writeLong(this.sum_flow);
        }

        @Override
        public void readFields(DataInput in) throws IOException {

            this.phone = Text.readString(in);
            this.up_flow = in.readLong();
            this.down_flow = in.readLong();
            this.sum_flow = in.readLong();
        }

        @Override
        public void write(PreparedStatement ps) throws SQLException {

            ps.setString(1, this.phone);
            ps.setLong(2, this.up_flow);
            ps.setLong(3, this.down_flow);
            ps.setLong(4, this.sum_flow);

        }

        @Override
        public void readFields(ResultSet resultSet) throws SQLException {

            this.phone = resultSet.getString("phone");
            this.up_flow = resultSet.getLong("up_flow");
            this.down_flow = resultSet.getLong("down_flow");
            this.sum_flow = resultSet.getLong("sum_flow");
        }

        @Override
        public String toString() {
            return "FlowRecord[" +
                    "phone='" + phone + '\'' +
                    ", up_flow=" + up_flow +
                    ", down_flow=" + down_flow +
                    ", sum_flow=" + sum_flow +
                    ']';
        }
    }

    public static class Mr2MysqlMapper extends Mapper<LongWritable, Text, Text, FlowRecord> {

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

            String line = value.toString();
            String[] fields = StringUtils.split(line, " ");

            String phone = fields[1];
            long up_flow = Long.parseLong(fields[7]);
            long down_flow = Long.parseLong(fields[8]);

            FlowRecord flowRecord = new FlowRecord(phone, up_flow, down_flow);

            context.write(new Text(phone), flowRecord);
        }
    }

    public static class Mr2MysqlReducer extends Reducer<Text, FlowRecord, FlowRecord, NullWritable> {

        @Override
        protected void reduce(Text key, Iterable<FlowRecord> values, Context context) throws IOException, InterruptedException {

            long sum_up_flow = 0;
            long sum_down_flow = 0;

            for (FlowRecord flowRecord : values) {

                sum_up_flow += flowRecord.getUp_flow();
                sum_down_flow += flowRecord.getDown_flow();
            }

            FlowRecord flowRecord = new FlowRecord(key.toString(), sum_up_flow, sum_down_flow);

            context.write(flowRecord, NullWritable.get());
        }
    }

    public static void main(String[] args) throws Exception {

        Configuration conf = new Configuration();
        DistributedCache.addFileToClassPath(new Path("hdfs://192.168.233.134:9000/mysql-connector-java-5.1.45.jar"),conf);
        DBConfiguration.configureDB(conf,"com.mysql.jdbc.Driver","jdbc:mysql://192.168.233.128:3306/mr","root","password");

        Job job = Job.getInstance(conf);
        job.setJarByClass(Mysql2MR.class);

        job.setMapperClass(Mr2MysqlMapper.class);
        job.setReducerClass(Mr2MysqlReducer.class);
        //设置输出格式
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(FlowRecord.class);
        //设置mapper输出格式
        job.setOutputKeyClass(FlowRecord.class);
        job.setOutputValueClass(NullWritable.class);

        job.setOutputFormatClass(DBOutputFormat.class);

        String[] fields = {"phone","up_flow","down_flow","sum_flow"};

        String in = "hdfs://192.168.233.134:9000/userflow/in/HTTP_20130313143750.dat";
        FileInputFormat.setInputPaths(job, new Path(in));

        DBOutputFormat.setOutput(job,"flow",fields);

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
