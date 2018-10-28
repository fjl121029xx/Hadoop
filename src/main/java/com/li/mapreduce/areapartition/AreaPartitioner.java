package com.li.mapreduce.areapartition;

import org.apache.hadoop.mapreduce.Partitioner;

import java.util.HashMap;

public class AreaPartitioner<KEY,VALUE> extends Partitioner<KEY,VALUE>{

    private static HashMap<String,Integer> areaMap = new HashMap<String,Integer >();

    static {
        areaMap.put("135",0);
        areaMap.put("137",1);
        areaMap.put("138",2);
        areaMap.put("139",3);
        areaMap.put("134",4);
        areaMap.put("136",5);

    }

    @Override
    public int getPartition(KEY key, VALUE value, int numPartitions) {

        int areaCode = areaMap.get(key.toString().subSequence(0,3))==null?6:areaMap.get(key.toString().subSequence(0,3));

        return areaCode;
    }
}
