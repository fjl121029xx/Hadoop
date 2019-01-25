package com.li.jdk8.features.function_interface;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class Test1 {

    public static void main(String[] args) {

        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);

        Predicate<Integer> predicate = n -> n % 2 == 0;
        // n 是一个参数传递到 Predicate 接口的 test 方法
        // n 如果存在则 test 方法返回 true

        System.out.println("输出所有数据:");
        // 传递参数 n
        eval(list, predicate);
    }

    public static void eval(List<Integer> list, Predicate<Integer> predicate) {
        for (Integer n : list) {

            if (predicate.test(n)) {
                System.out.println(n + " ");
            }
        }
    }
}
