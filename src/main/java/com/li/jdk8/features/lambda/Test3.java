package com.li.jdk8.features.lambda;

public class Test3 {

    public static void main(String args[]) {
//        String first = "";

        Comparator<String> comparator = (first, second) -> System.out.println(Integer.compare(first.length(), second.length())); //编译会出错
        comparator.com("aaaaa", "bb");
    }


    public interface Comparator<T> {
        void com(String a, String b);
    }
}
