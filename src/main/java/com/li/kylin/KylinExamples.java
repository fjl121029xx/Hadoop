package com.li.kylin;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.li.utils.HttpUtil;

import java.text.SimpleDateFormat;

public class KylinExamples {


    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static void animateSql() throws Exception {

        StringBuilder condition = new StringBuilder();


        String sqlTemplate = " select sum(playtime) as playtime,count(distinct uname) as activeuser,netclassid,title from videoplay2 where  playday >='19700101' and playday<='20190122' and netclassid = 67424 group by netclassid,title ";


        String body = "{" +
                "\"sql\":\"" + sqlTemplate + "\"," +
                "\"offset\":0," +
                "\"limit\":50000," +
                "\"acceptPartial\":false," +
                "\"project\":\"" + "user_study" + "\"" +
                "}";

        String results = HttpUtil.query(
                body);

        JSONArray ja = JSONObject.parseObject(results).getJSONArray("results");
        for (int i = 0; i < ja.size(); i++) {

            JSONArray jsonArray = ja.getJSONArray(i);


            System.out.println(jsonArray.toJSONString());
        }


    }

    public static void main(String[] args) throws Exception {

        animateSql();
    }


}
