package com.li.flink.home.table.user.defined.sources.batch;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.flink.addons.hbase.TableInputSplit;
import org.apache.flink.api.common.io.CheckpointableInputFormat;
import org.apache.flink.api.common.io.FileInputFormat;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.core.fs.FileInputSplit;

import java.io.IOException;

public abstract class AbstractJsonInputFormat<T> extends FileInputFormat<T>
        implements CheckpointableInputFormat<FileInputSplit, JSONObject> {

    protected transient JSONArray table = null;
    // helper variable to decide whether the input is exhausted or not
    protected boolean endReached = false;


    protected abstract String getTableName();

    protected abstract T mapResultToOutType(JSONObject r);

    public abstract void configure(Configuration parameters);


}
