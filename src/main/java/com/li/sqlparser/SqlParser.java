package com.li.sqlparser;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat.*;
import com.alibaba.druid.stat.*;
import com.alibaba.druid.util.JdbcConstants;

public class SqlParser {

    public static void main(String[] args) {

        String sql = "insert overwrite table aa partition (pt) " +
                "select card_id, " +
                "       group_id, " +
                "       save_count, " +
                "       consume_cnt, " +
                "       save_money_amount, " +
                "       save_return_money_amt, " +
                "       custome_consume_amt, " +
                "       custome_addpoint_num, " +
                "       custome_reducepotin_num, " +
                "       trans_shop_id, " +
                "       pt " +
                "from (select *, row_number() over (partition by group_id,card_id,trans_shop_id,pt order by v desc) rnum " +
                "      from ( " +
                "               select card_id, " +
                "                      group_id, " +
                "                      trans_shop_id, " +
                "                      count(if(trans_type = 20, trans_id, null))            as save_count, " +
                "                      count(if(trans_type = 30, trans_id, null))            as consume_cnt, " +
                "                      sum(if(trans_type = 20, save_money_amount, 0))        as save_money_amount, " +
                "                      sum(if(trans_type = 20, save_return_money_amount, 0)) as save_return_money_amt, " +
                "                      sum(if(trans_type = 30, consumption_amount, 0))       as custome_consume_amt, " +
                "                      sum(nvl(return_point_amount, 0))                      as custome_addpoint_num, " +
                "                      sum(nvl(deduction_point_amount, 0))                   as custome_reducepotin_num, " +
                "                      substr(trans_time, 1, 8)                              as pt, " +
                "                      1                                                     as v " +
                "               from hedw.fact_customer1_transdetail_day " +
                "               where pt =12 and action<=1 and substr(trans_time, 1, 8)=12 " +
                "               group by group_id, card_id, substr(trans_time, 1, 8), trans_shop_id " +
                "               union all " +
                "               select " +
                "                   card_id, group_id, trans_shop_id, " +
                "                   count(if (trans_type=20, trans_id, null)) as save_count, " +
                "                   count(if (trans_type=30, trans_id, null)) as consume_cnt, " +
                "                   sum(if (trans_type=20, save_money_amount, 0)) as save_money_amount, " +
                "                   sum(if (trans_type=20, save_return_money_amount, 0)) as save_return_money_amt, " +
                "                   sum(if (trans_type=30, consumption_amount, 0)) as custome_consume_amt, " +
                "                   sum(nvl(return_point_amount, 0)) as custome_addpoint_num, " +
                "                   sum(nvl(deduction_point_amount, 0)) as custome_reducepotin_num, " +
                "                   substr(trans_time, 1, 8) as pt, " +
                "                   2 as v " +
                "               from hedw.fact_customer2_transdetail_day " +
                "               where pt=12 and action<=1 and substr(trans_time, 1, 8)=12 " +
                "               group by group_id, card_id, substr(trans_time, 1, 8), trans_shop_id " +
                "           ) a " +
                "     ) b " +
                "where rnum = 1";
        DbType dbType = JdbcConstants.POSTGRESQL;

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
//            Map<String, String> aliasmap = visitor.getAliasMap();
//            for (Iterator iterator = aliasmap.keySet().iterator(); iterator.hasNext(); ) {
//                String key = iterator.next().toString();
//                System.out.println("[ALIAS]" + key + " - " + aliasmap.get(key));
//            }
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
            System.out.println("fields : " + visitor.getColumns());
        }

    }
}
