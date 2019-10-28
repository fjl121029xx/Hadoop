package com.li.elaticsearch.java.api;


import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class EsCRUD {
    static Logger logger = LogManager.getLogger(EsCRUD.class);

    public static void main(String[] args) throws Exception {

        EsUtil eu = new EsUtil();
        eu.openConnectES();

//        eu.createIndex();
//        eu.insert();
        eu.query();
    }
}
