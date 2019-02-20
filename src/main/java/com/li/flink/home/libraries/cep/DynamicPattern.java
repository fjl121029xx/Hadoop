package com.li.flink.home.libraries.cep;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections.map.HashedMap;
import org.apache.flink.cep.pattern.Pattern;

import java.util.Map;

public class DynamicPattern {


    public static void main(String[] args) {


        String patternConfiguration = "[" +
                "" +
                "{\"id\":\"start\",\"conditions\":[[[\"sql\",\"contains\",\"select\"],[\"latency\",\">\",1000]],[[\"sql\",\"contains\",\"delete\"]]],\"quantifiers\":[0,1,\"+\"],\"combinind\":\"head\"}," +
                "{\"id\":\"middle\",\"conditions\":[[[\"sql\",\"contains\",\"transaction\"]]],\"combining\":\"followedBy\"}" +
                "]";
        JSONArray jsonArray = JSON.parseArray(patternConfiguration);

        CepBuilder<Log> cepBuilder = new CepBuilder<Log>();
        Pattern<Log, ?> pattern = cepBuilder.patternSequenceBuilder();
        Pattern<Log, ?>  pattern2 = cepBuilder.patternSequenceBuilder2();

        Map<String,  Pattern<Log, ?>> patternMap = new HashedMap();
        patternMap.put("1", pattern);
        patternMap.put("2", pattern2);

    }
}
