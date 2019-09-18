package com.li.mapreduce.flowsum;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class FlowSumReducer extends Reducer<Text,FlowBean,Text,FlowBean>{

    @Override
    protected void reduce(Text key, Iterable<FlowBean> values, Context context) throws IOException, InterruptedException {

        long sum_up_flow = 0;
        long sum_down_flow = 0;

        for (FlowBean flowBean : values) {
            sum_up_flow += flowBean.getUp_flow();
            sum_down_flow += flowBean.getDown_flow();
        }

        FlowBean flowBean = new FlowBean(key.toString(),sum_up_flow,sum_down_flow);

        context.write(key,flowBean);
    }
}
