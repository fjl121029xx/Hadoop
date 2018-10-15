package com.li.mapreduce.flowsum;

import org.apache.hadoop.io.WritableComparable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class FlowBean implements WritableComparable<FlowBean> {


    private String phone;
    private long up_flow;
    private long down_flow;
    private long s_flow;

    public FlowBean() {
    }

    public FlowBean(String phone, long up_flow, long down_flow) {
        this.phone = phone;
        this.up_flow = up_flow;
        this.down_flow = down_flow;
        this.s_flow = up_flow + down_flow;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public long getUp_flow() {
        return up_flow;
    }

    public void setUp_flow(long up_flow) {
        this.up_flow = up_flow;
    }

    public long getDown_flow() {
        return down_flow;
    }

    public void setDown_flow(long down_flow) {
        this.down_flow = down_flow;
    }

    public long getS_flow() {
        return s_flow;
    }

    public void setS_flow(long s_flow) {
        this.s_flow = s_flow;
    }

    @Override
    public void write(DataOutput out) throws IOException {

        out.writeUTF(phone);
        out.writeLong(up_flow);
        out.writeLong(down_flow);
        out.writeLong(s_flow);
    }

    @Override
    public void readFields(DataInput in) throws IOException {

        this.phone = in.readUTF();
        this.up_flow = in.readLong();
        this.down_flow = in.readLong();
        this.s_flow = in.readLong();
    }

    @Override
    public String toString() {
        return "FlowBean{" +
                "phone='" + phone + '\'' +
                ", up_flow=" + up_flow +
                ", down_flow=" + down_flow +
                ", s_flow=" + s_flow +
                '}';
    }

    @Override
    public int compareTo(FlowBean o) {
        int i = Long.compare(this.getS_flow(), o.getS_flow());
        return i;
    }
}
