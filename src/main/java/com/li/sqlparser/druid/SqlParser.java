package com.li.sqlparser.druid;

import java.util.*;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlInsertStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat.*;
import com.alibaba.druid.stat.*;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.Test;

/*
 * 解析sql
 * https://www.cnblogs.com/digdeep/p/5071204.html
 * */
public class SqlParser {

    public static void main(String[] args) {

        /*String sql = "insert into gauss.app_bill_shop_channel_day partition(pt) " +
                " select concat(from_unixtime(unix_timestamp(cast(20210205 as string),'yyyyMMdd'),'yyyy-MM-dd'),' 00:00:00') as report_date,\n" +
                "       a.group_id,\n" +
                "\t   b.group_name,\n" +
                "\t   a.brand_id,\n" +
                "\t   b.brand_name,\n" +
                "\t   a.shop_id,\n" +
                "\t   b.shop_name,\n" +
                "\t   a.city_id,\n" +
                "\t   b.city_name,\n" +
                "\t   a.province_id,\n" +
                "\t   b.province_name,\n" +
                "\t   a.channel_name,\n" +
                "\t   case when a.order_subtype=0 then '堂食'\n" +
                "            when a.order_subtype=20 then '外卖'\n" +
                "            when a.order_subtype=21 then '自提'\n" +
                "            else '其他'\n" +
                "        end as bill_subtype,\n" +
                "       sum(a.bill_person_cnt) as all_bill_cust_num,\n" +
                "       sum(a.bill_cnt) as all_bill_num,\n" +
                "       sum(a.bill_amt) as all_bill_amt,\n" +
                "       sum(a.bill_real_amt) as all_bill_actual_amt,\n" +
                "       sum(a.bill_discount_amt) as all_bill_discount_amt,\n" +
                "       sum(a.bill_food_real_cnt) as all_bill_sale_food_num,\n" +
                "       sum(case when substr(a.pt,1,4)=substr(20210206,1,4) then a.bill_person_cnt else 0 end) as year_bill_cust_num,\n" +
                "       sum(case when substr(a.pt,1,4)=substr(20210206,1,4) then a.bill_cnt else 0 end) as year_bill_num,\n" +
                "       sum(case when substr(a.pt,1,4)=substr(20210206,1,4) then a.bill_amt else 0 end) as year_bill_amt,\n" +
                "       sum(case when substr(a.pt,1,4)=substr(20210206,1,4) then a.bill_real_amt else 0 end) as year_bill_actual_amt,\n" +
                "       sum(case when substr(a.pt,1,4)=substr(20210206,1,4) then a.bill_discount_amt else 0 end) as year_bill_discount_amt,\n" +
                "       sum(case when substr(a.pt,1,4)=substr(20210206,1,4) then a.bill_food_real_cnt else 0 end) as year_bill_sale_food_num,\n" +
                "       sum(case when substr(a.pt,1,6)=substr(20210206,1,6) then a.bill_person_cnt else 0 end) as month_bill_cust_num,\n" +
                "       sum(case when substr(a.pt,1,6)=substr(20210206,1,6) then a.bill_cnt else 0 end) as month_bill_num,\n" +
                "       sum(case when substr(a.pt,1,6)=substr(20210206,1,6) then a.bill_amt else 0 end) as month_bill_amt,\n" +
                "       sum(case when substr(a.pt,1,6)=substr(20210206,1,6) then a.bill_real_amt else 0 end) as month_bill_actual_amt,\n" +
                "       sum(case when substr(a.pt,1,6)=substr(20210206,1,6) then a.bill_discount_amt else 0 end) as month_bill_discount_amt,\n" +
                "       sum(case when substr(a.pt,1,6)=substr(20210206,1,6) then a.bill_food_real_cnt else 0 end) as month_bill_sale_food_num,\n" +
                "       20210206 as pt\n" +
                "from gauss.aggr_bill_shop_channel_paynew_day a\n" +
                "inner join gauss.dim_extend_shop_info b\n" +
                "on a.shop_id=b.shop_id and b.pt=20210206 and b.is_test_shop<>1\n" +
                "where a.pt>='20180101' and a.pt<=20210205\n" +
                "group by concat(from_unixtime(unix_timestamp(cast(20210205 as string),'yyyyMMdd'),'yyyy-MM-dd'),' 00:00:00'),\n" +
                "         a.group_id,\n" +
                "\t\t b.group_name,\n" +
                "\t\t a.brand_id,\n" +
                "\t\t b.brand_name,\n" +
                "\t\t a.shop_id,\n" +
                "\t\t b.shop_name,\n" +
                "\t\t a.city_id,\n" +
                "\t\t b.city_name,\n" +
                "\t\t a.province_id,\n" +
                "\t\t b.province_name,\n" +
                "\t\t a.channel_name,\n" +
                "\t\t case when a.order_subtype=0 then '堂食'\n" +
                "            when a.order_subtype=20 then '外卖'\n" +
                "            when a.order_subtype=21 then '自提'\n" +
                "            else '其他'end";*/
        String sql = "select count(*) from bd.bas_bill_food a left join bd.bas_bill_pay b on a.group_id = b.group_id";
        DbType dbType = JdbcConstants.HIVE;

        //格式化输出
        String result = SQLUtils.format(sql, dbType);
        System.out.println(result); // 缺省大写格式
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);

        //解析出的独立语句的个数
        System.out.println("size is:" + stmtList.size());
        for (int i = 0; i < stmtList.size(); i++) {

            SQLStatement stmt = stmtList.get(i);

            PGSchemaStatVisitor visitor = new PGSchemaStatVisitor();
            stmt.accept(visitor);
            Collection<Column> columns = visitor.getColumns();
            for (Column col : columns) {
                System.out.println("[COLUME]" + col.getName() + " - " + col.getFullName());
            }


            Set<Column> groupby_col = visitor.getGroupByColumns();
            //

            for (Iterator iterator = groupby_col.iterator(); iterator.hasNext(); ) {
                Column column = (Column) iterator.next();
                System.out.println("[GROUP]" + column.toString());
            }
            //获取表名称
            System.out.println("table names:");
            Map<Name, TableStat> tabmap = visitor.getTables();
            for (Iterator iterator = tabmap.keySet().iterator(); iterator.hasNext(); ) {
                Name name = (Name) iterator.next();
                System.out.println(name.toString() + " - " + tabmap.get(name).toString());
            }
            //System.out.println("Tables : " + visitor.getCurrentTable());
            //获取操作方法名称,依赖于表名称
            System.out.println("Manipulation : " + visitor.getTables());
            //获取字段名称
            System.out.println("fields : " + columns);
        }

    }

    @Test
    public void getT() throws Exception {
        String sql = "select count(*)    from bd.bas_bill_food a left join bd.bas_bill_pay b on a.group_id = b.group_id ";
        DbType dbType = JdbcConstants.HIVE;

        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        for (SQLStatement stmt : stmtList) {

            PGSchemaStatVisitor visitor = new PGSchemaStatVisitor();
            stmt.accept(visitor);
            Map<Name, TableStat> tabmap = visitor.getTables();
            SQLSelectItem parent = (SQLSelectItem) visitor.getAggregateFunctions().get(0).getParent();

            System.out.println(parent.getAlias());
            for (Name name : tabmap.keySet()) {
                System.out.println(name.toString() + " - " + tabmap.get(name).toString());
            }
        }

    }

    @Test
    public void getT2() throws Exception {
        String sql = "select count(*) as c  from bd.bas_bill_food a left join bd.bas_bill_pay b on a.group_id = b.group_id  ";
        String formatSql = sql.replaceAll("\\n|\\t|\\r", " ");
        int aliasIdx = formatSql.toLowerCase().indexOf("as");
        if (aliasIdx < 0) {
            System.out.println("myCount");
        } else {
            System.out.println(formatSql.substring(aliasIdx + 2, formatSql.toLowerCase().indexOf("from")).trim());
        }

    }


}
