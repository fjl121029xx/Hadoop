package com.li.hive.jdbc;

import java.sql.*;

public class HiveJdbcDemo {

    private static String driverName =
            "org.apache.hive.jdbc.HiveDriver";

    public static void main(String[] args)
            throws SQLException {
        try {
            Class.forName(driverName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }

        Connection con = DriverManager.getConnection(
                "jdbc:hive2://172.20.44.8:10016",
                "hadoop", "");
        Statement stmt = con.createStatement();
//        String tableName = "wyphao";
//        stmt.execute("drop table if exists " + tableName);
//        stmt.execute("create table " + tableName +
//                " (key int, value string)");
//        System.out.println("Create table success!");
        // show tables

        String sql = "select split(tt.key,'△')[0] as `report_date_1585638350572`,split(tt.key,'△')[1] as `brand_name_1585638389514`,split(tt.key,'△')[2] as `foodsubject_name_1585638394160`,split(tt.key,'△')[3] as `foodcategory_name_1585638397420`,cast (split(tt.key,'△')[4] as DECIMAL(15,2)) as `food_send_number_1585639491665`,cast (split(tt.key,'△')[5] as DECIMAL(15,2)) as `food_price_amount_1585639746938`,cast (split(tt.key,'△')[6] as DECIMAL(15,2)) as `food_send_number_1585639491665_赠菜数量_sum`,cast (split(tt.key,'△')[7] as DECIMAL(15,2)) as `food_price_amount_1585639746938_流水金额_sum`,cast (split(tt.key,'△')[8] as DECIMAL(15,2)) as `rowSum`,cast (split(tt.key,'△')[9] as DECIMAL(15,2)) as `measure_total`,split(tt.key,'△')[10] as `key` from ( select row_col_stat_1(Array(report_date,brand_name,foodsubject_name),Array(foodcategory_name),Array(food_send_number,food_price_amount),1,'ymd',Array('food_send_number-sum','food_price_amount-sum'),array('sum','sum','sum','sum')) as m from db_yqs_b_505.tbl_pos_bill_food where 1=1 ) t LATERAL VIEW explode(t.m) tt as key,value  order by  case report_date_1585638350572 when '总计' then -99999999 else report_date_1585638350572 end ASC,case brand_name_1585638389514 when '小计' then -99999999 when '' then 0 else brand_name_1585638389514 end ASC,case foodsubject_name_1585638394160 when '小计' then -99999999 when '' then 0 else foodsubject_name_1585638394160 end ASC,case foodcategory_name_1585638397420 when '小计' then -99999999 when '' then 0 else foodcategory_name_1585638397420 end ASC\n " ;

//        String sql = "select  " +
//                "split(tt.k,'△')[0] as `report_date_1585638350572`," +
//                "split(tt.k,'△')[1] as `brand_name_1585638389514`," +
//                "split(tt.k,'△')[2] as `foodsubject_name_1585638394160`," +
//                "split(tt.k,'△')[3] as `foodcategory_name_1585638397420`," +
//                "cast (split(tt.k,'△')[4] as DECIMAL(15,2)) as `food_send_number_1585639491665`," +
//                "cast (split(tt.k,'△')[5] as DECIMAL(15,2)) as `food_price_amount_1585639746938`," +
//                "cast (split(tt.k,'△')[6] as DECIMAL(15,2)) as `food_send_number_1585639491665_赠菜数量_sum`," +
//                "cast (split(tt.k,'△')[7] as DECIMAL(15,2)) as `food_price_amount_1585639746938_流水金额_sum`," +
//                "cast (split(tt.k,'△')[8] as DECIMAL(15,2)) as `rowSum`," +
//                "cast (split(tt.k,'△')[9] as DECIMAL(15,2)) as `measure_total`," +
//                "split(tt.k,'△')[10] as `key` " +
//                "from ( select row_col_stat_1(Array(report_date,brand_name,foodsubject_name),Array(foodcategory_name),Array(food_send_number,food_price_amount),1,'ymd',Array(\'food_send_number-sum\',\'food_price_amount-sum\'),array(\'sum\',\'sum\',\'sum\',\'sum\')) as m from db_yqs_b_505.tbl_pos_bill_food where 1=1 ) t LATERAL VIEW explode(t.m) tt as k,c  " ;

        System.out.println("Running: " + sql);
        ResultSet res = stmt.executeQuery(sql);
        if (res.next()) {
            System.out.println(res.getObject(1));
        }


    }
}
