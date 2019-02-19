package com.li.flink.home.table.user.defined.function;

import org.apache.flink.table.functions.ScalarFunction;

public class MyScalarFunctions extends ScalarFunction {


    private int factor = 12;

    public MyScalarFunctions(int factor) {
        this.factor = factor;
    }

    public int eval(String s) {
        return s.hashCode() * 12;
    }

}
