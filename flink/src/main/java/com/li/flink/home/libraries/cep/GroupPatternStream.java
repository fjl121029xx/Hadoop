package com.li.flink.home.libraries.cep;

import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeutils.TypeSerializer;
import org.apache.flink.api.java.typeutils.TypeExtractor;
import org.apache.flink.cep.PatternStream;
import org.apache.flink.cep.nfa.compiler.NFACompiler;
import org.apache.flink.cep.pattern.Pattern;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupPatternStream<K, T> {

    // underlying data stream
    private final DataStream<T> inputStream;

    private final Map<K, Pattern<T, ?>> patterns;

    GroupPatternStream(final DataStream<T> inputStream, final Map<K, Pattern<T, ?>> patterns) {
        this.inputStream = inputStream;
        this.patterns = patterns;
    }



}
