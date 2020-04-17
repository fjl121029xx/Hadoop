package com.li.hive.jdbc;

import java.sql.*;
import java.text.SimpleDateFormat;

public class HiveJdbcDemo {

    private static String driverName =
            "org.apache.hive.jdbc.HiveDriver";

    public static void runJob() throws SQLException {

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        try {
            Class.forName(driverName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }
//        Connection con = DriverManager.getConnection(
//                "jdbc:hive2://172.20.44.8:10016",
//                "hadoop", "");
        Connection con = DriverManager.getConnection(
                "jdbc:hive2://192.168.101.51:10016",
                "olap", "");

        Statement stmt = con.createStatement();

        System.setProperty("spark.sql.crossJoin.enabled", "true");
//        String sql = "select row_number() over(order by split(key,'_')[0] ) as `key`, split(key,'_')[0] AS `report_date_1586318433859`,cast(value as DECIMAL(15,2)) as `pay_amt_1586318438860` from (select compare(ARRAY(cast(report_date as string)),pay_amt,'ym','0','avg') as m from `db_yqs_b_5700`.`standard_summary_bill_shop_pay_day_8` where 1=1) t LATERAL VIEW explode(t.m) tt as key ,value  order by report_date_1586318433859 ASC";

//        String sql ="create  TEMPORARY  function row_col_stat_test as 'com.hll.udaf.v2.RowColStatistics' using jar 'hdfs:/test/hive-udf-1.0.jar'" ;
        String sql = "" +
                " select split(tt.key,'△')[0] as `col_1_1587091326681`,split(tt.key,'△')[1] as `col_2_1587091317940`,split(tt.key,'△')[2] as `col_3_1587091381721`,cast (split(tt.key,'△')[3] as DECIMAL(15,2)) as `col_6_1587091322830`,cast (split(tt.key,'△')[4] as DECIMAL(15,2)) as `col_6_1587091372661`,cast (split(tt.key,'△')[5] as DECIMAL(15,2)) as `col_6_1587091322830_流水金额_sum`,cast (split(tt.key,'△')[6] as DECIMAL(15,2)) as `col_6_1587091372661_流水金额_sum`,cast (split(tt.key,'△')[7] as DECIMAL(15,2)) as `rowSum`,cast (split(tt.key,'△')[8] as DECIMAL(15,2)) as `measure_total`,split(tt.key,'△')[9] as `key` from ( select row_col_stat_3(Array( cast(col_1 as string) , cast(col_2 as string) ),Array(col_3),Array(col_6,col_6),1,'ymd',Array('col_6-sum','col_6-sum'),Array('sum-1','sum-0','sum-0','sum-1')) as m from db_yqs_p_5700.tbl_p_1016971_1587086884 where 1=1 ) t LATERAL VIEW explode(t.m) tt as key,value order by case col_1_1587091326681 when '总计' then -99999999 else col_1_1587091326681 end ASC,case col_2_1587091317940 when '小计' then -99999999 when '' then 0 else col_2_1587091317940 end ASC,case col_3_1587091381721 when '小计' then -99999999 when '' then 0 else col_3_1587091381721 end ASC" +
                "";

        System.out.println(sdf.format(new Date(System.currentTimeMillis())) + " Running: " + sql);
        long a = System.currentTimeMillis();
        ResultSet res = stmt.executeQuery(sql);

        if (res.next()) {
            System.out.println("-=-=-=-=-");
        }
        long b = System.currentTimeMillis();
        System.out.println((b - a) / 1000);
    }

    public static void main(String[] args)
            throws SQLException {
        for (int i = 0; i < 100; i++) {
            try {
                runJob();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }
}
