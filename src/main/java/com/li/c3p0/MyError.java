package com.li.c3p0;

public class MyError extends Throwable {
    public MyError(String s, Exception e) {
        e.printStackTrace();
        System.out.println(s);
    }
}
