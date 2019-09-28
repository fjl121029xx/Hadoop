package com.li.flink.home.table.bean;

public class Pojo {

    public Long key;
    public Integer value;
    public Long recordTime;

    public Pojo() {
    }

    public Pojo(Long key, Integer value, Long recordTime) {
        this.key = key;
        this.value = value;
        this.recordTime = recordTime;
    }

    @Override
    public String toString() {
        return "Pojo{" +
                "key=" + key +
                ", value=" + value +
                ", recordTime=" + recordTime +
                '}';
    }


}
