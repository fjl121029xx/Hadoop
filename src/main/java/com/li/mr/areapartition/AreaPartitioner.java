package com.li.mr.areapartition;

import org.apache.hadoop.mapreduce.Partitioner;

import java.util.HashMap;

public class AreaPartitioner<KEY,VALUE> extends Partitioner<KEY,VALUE>{

    private static HashMap<String,Integer> areaMap = new HashMap<String,Integer >();

    static {
        areaMap.put("135",0);
        areaMap.put("137",1);
        areaMap.put("138",2);
    }

    @Override
    public int getPartition(KEY key, VALUE value, int numPartitions) {

        int areaCode = areaMap.get(key.toString().subSequence(0,3))==null?3:areaMap.get(key.toString().subSequence(0,3));

        return areaCode;
    }
}
