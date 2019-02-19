package com.li.flink.home.table.user.defined.sources.batch;

import com.alibaba.fastjson.JSONObject;
import org.apache.flink.api.common.io.CheckpointableInputFormat;
import org.apache.flink.api.common.io.FileInputFormat;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.ResultTypeQueryable;
import org.apache.flink.core.fs.FileInputSplit;

import java.io.IOException;

public class JsonInputFormat extends FileInputFormat<String> implements ResultTypeQueryable<String>,
        CheckpointableInputFormat<FileInputSplit, JSONObject> {

    @Override
    public JSONObject getCurrentState() throws IOException {
        return null;
    }

    @Override
    public void reopen(FileInputSplit split, JSONObject state) throws IOException {

    }

    @Override
    public boolean reachedEnd() throws IOException {
        return false;
    }

    @Override
    public String nextRecord(String reuse) throws IOException {
        return null;
    }

    @Override
    public TypeInformation<String> getProducedType() {
        return null;
    }

    // row which is returned

}
