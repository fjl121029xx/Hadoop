package com.li.flink.home.libraries.cep;

import com.alibaba.fastjson.JSONArray;
import org.apache.flink.cep.pattern.Pattern;
import org.apache.flink.cep.pattern.conditions.SimpleCondition;

public class CepBuilder<E> {

    private JSONArray jsonArray;

    public CepBuilder() {
    }

    public CepBuilder(JSONArray jsonArray) {
        this.jsonArray = jsonArray;
    }

    public JSONArray getJsonArray() {
        return jsonArray;
    }

    public void setJsonArray(JSONArray jsonArray) {
        this.jsonArray = jsonArray;
    }


    public Pattern<Log, Log> patternSequenceBuilder() {

        Pattern<Log, Log> pattern = Pattern.<Log>begin("start").where(new SimpleCondition<Log>() {

            @Override
            public boolean filter(Log log) throws Exception {
                return log.sql.contains("start");
            }
        }).followedByAny("middle").where(new SimpleCondition<Log>() {

            @Override
            public boolean filter(Log log) throws Exception {
                return log.sql.contains("end");
            }
        });

        return pattern;
    }

    public Pattern<Log, Log> patternSequenceBuilder2() {

        Pattern<Log, Log> pattern = Pattern.<Log>begin("start").where(new SimpleCondition<Log>() {

            @Override
            public boolean filter(Log log) throws Exception {
                return log.sql.contains("start");
            }
        }).followedByAny("middle").where(new SimpleCondition<Log>() {

            @Override
            public boolean filter(Log log) throws Exception {
                return log.sql.contains("select");
            }
        }).followedByAny("end").where(new SimpleCondition<Log>() {

            @Override
            public boolean filter(Log log) throws Exception {
                return log.sql.contains("end");
            }
        });

        return pattern;
    }


}
