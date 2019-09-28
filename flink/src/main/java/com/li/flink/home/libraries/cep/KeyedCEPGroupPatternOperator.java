package com.li.flink.home.libraries.cep;

import org.apache.flink.api.common.functions.Function;
import org.apache.flink.api.common.typeutils.TypeSerializer;
import org.apache.flink.cep.EventComparator;
import org.apache.flink.cep.nfa.aftermatch.AfterMatchSkipStrategy;
import org.apache.flink.cep.nfa.compiler.NFACompiler;
import org.apache.flink.cep.operator.AbstractKeyedCEPPatternOperator;
import org.apache.flink.util.OutputTag;

import java.util.Map;

public class KeyedCEPGroupPatternOperator<IN, KEY> extends AbstractKeyedCEPPatternOperator {


    Map<KEY, NFACompiler.NFAFactory<IN>> nfaFactoryMap;

    public KeyedCEPGroupPatternOperator(TypeSerializer inputSerializer,
                                        boolean isProcessingTime,
                                        NFACompiler.NFAFactory nfaFactory,
                                        EventComparator comparator,
                                        AfterMatchSkipStrategy afterMatchSkipStrategy,
                                        Function function,
                                        OutputTag lateDataOutputTag) {
        super(inputSerializer, isProcessingTime, nfaFactory, comparator, afterMatchSkipStrategy, function, lateDataOutputTag);
    }


    @Override
    protected void processMatchedSequences(Iterable iterable, long l) throws Exception {

    }
}
