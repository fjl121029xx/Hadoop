package com.li.flink.home.table.user.defined.sources.batch;

import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;

import java.io.Serializable;
import java.util.Map;

public class JsonSchema implements Serializable {

    public String[] getFieldNames() {

        return new String[]{"k", "v", "t"};
    }


    TypeInformation<?>[] getFieldsTypes() {


        TypeInformation<?>[] typeInformation = new TypeInformation[3];
        typeInformation[0]= Types.LONG;
        typeInformation[1]= Types.INT;
        typeInformation[2]= Types.LONG;

        return typeInformation;
    }
}
