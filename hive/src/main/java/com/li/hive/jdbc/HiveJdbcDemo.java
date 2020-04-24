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
        Connection con = DriverManager.getConnection(
                "jdbc:hive2://172.20.44.8:10016",
                "hadoop", "");
//        Connection con = DriverManager.getConnection(
//                "jdbc:hive2://192.168.101.51:10016",
//                "olap", "");

        Statement stmt = con.createStatement();

        System.setProperty("spark.sql.crossJoin.enabled", "true");

        String sql = "  " +
                "SELECT " +
                "  report_date AS report_date," +
                "  data_source AS data_source," +
                "  __group_id__ AS __group_id__," +
                "  group_name AS group_name," +
                "  __brand_id__ AS __brand_id__," +
                "  brand_name AS brand_name," +
                "  __shop_id__ AS __shop_id__," +
                "  shop_name AS shop_name," +
                "  order_key AS order_key," +
                "  order_no AS order_no," +
                "  device_orderno AS device_orderno," +
                "  locked_flag AS locked_flag," +
                "  timename_start AS timename_start," +
                "  timename_checkout AS timename_checkout," +
                "  area_name AS area_name," +
                "  table_name AS table_name," +
                "  channel_key AS channel_key," +
                "  channel_name AS channel_name," +
                "  channel_orderno AS channel_orderno," +
                "  channel_orderkey AS channel_orderkey," +
                "  channel_userkey AS channel_userkey," +
                "  channel_userimage AS channel_userimage," +
                "  channel_ordertime AS channel_ordertime," +
                "  order_subtype AS order_subtype," +
                "  net_ordertype_code AS net_ordertype_code," +
                "  person AS person," +
                "  createby AS createby," +
                "  waiterby AS waiterby," +
                "  start_time AS start_time," +
                "  checkout_time AS checkout_time," +
                "  checkoutby AS checkoutby," +
                "  order_status AS order_status," +
                "  is_testorder AS is_testorder," +
                "  order_md5 AS order_md5," +
                "  food_count AS food_count," +
                "  food_amount AS food_amount," +
                "  sendfood_amount AS sendfood_amount," +
                "  sendcoupon_amount AS sendcoupon_amount," +
                "  sendcoupon_remark AS sendcoupon_remark," +
                "  card_no AS card_no," +
                "  card_key AS card_key," +
                "  card_transid AS card_transid," +
                "  customer_name AS customer_name," +
                "  card_transafter_remark AS card_transafter_remark," +
                "  discountby AS discountby," +
                "  discount_waykey AS discount_waykey," +
                "  discount_wayname AS discount_wayname," +
                "  discount_rate AS discount_rate," +
                "  discount_range AS discount_range," +
                "  is_vip_price AS is_vip_price," +
                "  money_wipezero_type AS money_wipezero_type," +
                "  promotion_amount AS promotion_amount," +
                "  promotion_desc AS promotion_desc," +
                "  paid_amount AS paid_amount," +
                "  invoiceby AS invoiceby," +
                "  invoice_title AS invoice_title," +
                "  invoice_taxpayerident_code AS invoice_taxpayerident_code," +
                "  invoice_amount AS invoice_amount," +
                "  invoice_tax_rate AS invoice_tax_rate," +
                "  invoice_tax_amount AS invoice_tax_amount," +
                "  user_name AS user_name," +
                "  user_sex AS user_sex," +
                "  user_mobile AS user_mobile," +
                "  user_address AS user_address," +
                "  modify_order_log AS modify_order_log," +
                "  yjz_count AS yjz_count," +
                "  fjz_count AS fjz_count," +
                "  fjz_end_time AS fjz_end_time," +
                "  alert_flaglst AS alert_flaglst," +
                "  food_alert AS food_alert," +
                "  food_name_detail AS food_name_detail," +
                "  pay_alert AS pay_alert," +
                "  order_remark AS order_remark," +
                "  device_key AS device_key," +
                "  device_code AS device_code," +
                "  device_name AS device_name," +
                "  server_mac AS server_mac," +
                "  reviewby AS reviewby," +
                "  review_time AS review_time," +
                "  shift_time AS shift_time," +
                "  shift_name AS shift_name," +
                "  action_time AS action_time," +
                "  create_time AS create_time," +
                "  upload_time AS upload_time," +
                "  device_group_name AS device_group_name," +
                "  primary_device_type AS primary_device_type," +
                "  primary_device_version AS primary_device_version," +
                "  item_id AS item_id," +
                "  is_created_by_login_user AS is_created_by_login_user," +
                "  channel_order_keytp AS channel_order_keytp," +
                "  channel_userid AS channel_userid," +
                "  device_groupid AS device_groupid," +
                "  dw_pos_version AS dw_pos_version," +
                "  unique_id AS unique_id," +
                "  analysis_time AS analysis_time," +
                "  pt AS pt," +
                "  default.HOUR_DIFF (  start_time  ,    checkout_time  ) AS virtual_740 " +
                "FROM" +
                "  `db_yqs_b_505`.tbl_pos_bill_master t0 " +
                "WHERE (" +
                "    shop_name IN (" +
                "      '测试-李源'," +
                "      '测试-李彦龙123'," +
                "      '测试-唐唐'," +
                "      '测试-南唐一'" +
                "    )" +
                "  ) " +
                "LIMIT 1000";

        System.out.println(sdf.format(new Date(System.currentTimeMillis())) + " Running: \r\n" + sql);
        long a = System.currentTimeMillis();
        ResultSet res = stmt.executeQuery(sql);

        while (res.next()) {

            System.out.println(res.getObject(1));
//            System.out.println(res.getObject(1));
        }
        long b = System.currentTimeMillis();
        System.out.println((b - a) / 1000);
    }

    public static void main(String[] args)
            throws SQLException {
        for (int i = 0; i < 1; i++) {
            try {
                runJob();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }
}
