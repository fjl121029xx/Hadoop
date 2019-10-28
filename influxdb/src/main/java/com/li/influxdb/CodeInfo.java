package com.li.influxdb;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CodeInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String code;
    private String descr;
    private String descrE;
    private String createdBy;
    private Long createdAt;

    private String time;
    private String tagCode;
    private String tagName;

    public void setPropertyValue(String propertyName, Object value) throws InvocationTargetException, IllegalAccessException {
        Method[] methods = this.getClass().getMethods();
        for (int m = 0; m < methods.length; m++) {
            Method met = methods[m];
            if (met.getName().equalsIgnoreCase("set" + propertyName)) {

                Type[] genericParameterTypes = met.getGenericParameterTypes();
                if (genericParameterTypes.length == 1) {
                    String typeName = genericParameterTypes[0].getTypeName();
                    switch (typeName) {
                        case "java.lang.String":
                            met.invoke(this, value.toString());
                            break;
                        case "java.lang.Long":
                            met.invoke(this, (long) (Double.parseDouble(value.toString())));
                            break;
                    }
                }
//                met.invoke(this, value);
            }
        }
    }


    public static void main(String[] args) throws InvocationTargetException, IllegalAccessException {
        CodeInfo ci = new CodeInfo();
        Method[] methods = ci.getClass().getMethods();
        for (int m = 0; m < methods.length; m++) {
            Method met = methods[m];
            if (met.getName().equalsIgnoreCase("setdescr")) {
                Type[] genericParameterTypes = met.getGenericParameterTypes();
                if (genericParameterTypes.length == 1) {
                    String typeName = genericParameterTypes[0].getTypeName();
                    switch (typeName) {
                        case "java.lang.String":
                            met.invoke(ci, "a");
                            break;
                        case "java.lang.Long":
                            met.invoke(ci, 1L);
                    }
                }
            }
        }
        System.out.println(ci);
    }
}
