package com.li.elaticsearch;


public class EsCRUD {


    public static void main(String[] args) throws Exception {

        EsUtil eu = new EsUtil();
        eu.openConnectES();

//        eu.createIndex();
//        eu.insert();
        eu.query();
    }
}
