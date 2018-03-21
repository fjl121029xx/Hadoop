package com.li.mr.hive;

import org.apache.hadoop.hive.ql.exec.UDF;

import java.util.HashMap;

public class Phone2Area extends UDF {

    private static HashMap<String,String> areMap = new HashMap();

    static {

        areMap.put("138","北京");
        areMap.put("137","太原");
        areMap.put("139","上海");
    }

    public String evaluate(String phone) {

        String area = areMap.get(phone.substring(0,3));
        return area;
    }
}
