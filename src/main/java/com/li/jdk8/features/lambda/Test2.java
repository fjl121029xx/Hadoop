package com.li.jdk8.features.lambda;

public class Test2 {

    final static String salutation = "Hello! ";

    public static void main(String[] args) {

        GreetingService greetService1 = message ->
                System.out.println(salutation + message);
        greetService1.sayMessage("Runoob");
    }

    interface GreetingService {
        void sayMessage(String message);
    }
}
