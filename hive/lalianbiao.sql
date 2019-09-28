CREATE EXTERNAL TABLE lxw1234
(
    cookieid   string,
    createtime string, --页面访问时间
    url        STRING  --被访问页面
) ROW FORMAT DELIMITED
    FIELDS TERMINATED BY ','
    stored as textfile location '/huatu-data/b.txt';



SELECT cookieid,
       createtime,
       url,
       ROW_NUMBER() OVER (PARTITION BY cookieid ORDER BY createtime)                              AS rn,
       LAG(createtime, 1, '1970-01-01 00:00:00') OVER (PARTITION BY cookieid ORDER BY createtime) AS last_1_time,
       LAG(createtime, 2) OVER (PARTITION BY cookieid ORDER BY createtime)                        AS last_2_time
FROM lxw1234;

-- 数据仓库之拉链表(hive实现)
-- https://www.jianshu.com/p/c378c49528c3


SELECT cookieid,
       createtime,
       url,
       ROW_NUMBER() OVER (PARTITION BY cookieid ORDER BY createtime)                               AS rn,
       LEAD(createtime, 1, '1970-01-01 00:00:00') OVER (PARTITION BY cookieid ORDER BY createtime) AS next_1_time,
       LEAD(createtime, 2) OVER (PARTITION BY cookieid ORDER BY createtime)                        AS next_2_time
FROM lxw1234;

-- ##########################################################################################################################
-- # 源表
CREATE TABLE orders
(
    orderid      INT,
    createtime   STRING,
    modifiedtime STRING,
    status       STRING
) stored AS textfile;
--
-- # 增量数据
CREATE TABLE ods_orders_inc
(
    orderid      INT,
    createtime   STRING,
    modifiedtime STRING,
    status       STRING
) PARTITIONED BY (day STRING)
    stored AS textfile;

-- # 拉链表
CREATE TABLE dw_orders_his
(
    orderid       INT,
    createtime    STRING,
    modifiedtime  STRING,
    status        STRING,
    dw_start_date STRING,
    dw_end_date   STRING
) stored AS textfile;


-- ####
-- https://blog.csdn.net/bbaiggey/article/details/75222676


-- # 初始化，先把2016-08-20的数据初始化进去
INSERT overwrite TABLE ods_orders_inc PARTITION (day = '2016-08-20')
SELECT orderid, createtime, modifiedtime, status
FROM orders
WHERE createtime < '2016-08-21'
  and modifiedtime < '2016-08-21';

-- # 刷到dw中
INSERT overwrite TABLE dw_orders_his
SELECT orderid,
       createtime,
       modifiedtime,
       status,
       createtime   AS dw_start_date,
       '9999-12-31' AS dw_end_date
FROM ods_orders_inc
WHERE day = '2016-08-20';

-- # 剩余需要进行增量更新
INSERT overwrite TABLE ods_orders_inc PARTITION (day = '2016-08-21')
SELECT orderid, createtime, modifiedtime, status
FROM orders
WHERE (createtime = '2016-08-21' and modifiedtime = '2016-08-21')
   OR modifiedtime = '2016-08-21';


-- # 先放到增量表中，然后进行关联到一张临时表中，在插入到新表中
CREATE TABLE dw_orders_his_tmp AS
SELECT orderid,
       createtime,
       modifiedtime,
       status,
       dw_start_date,
       dw_end_date
FROM (
         SELECT a.orderid,
                a.createtime,
                a.modifiedtime,
                a.status,
                a.dw_start_date,
                CASE
                    WHEN b.orderid IS NOT NULL AND a.dw_end_date > '2016-08-21' THEN '2016-08-21'
                    ELSE a.dw_end_date END AS dw_end_date
         FROM dw_orders_his a
                  left outer join (SELECT * FROM ods_orders_inc WHERE day = '2016-08-21') b
                                  ON (a.orderid = b.orderid)
         UNION ALL
         SELECT orderid,
                createtime,
                modifiedtime,
                status,
                modifiedtime AS dw_start_date,
                '9999-12-31' AS dw_end_date
         FROM ods_orders_inc
         WHERE day = '2016-08-21'
     ) x
ORDER BY orderid, dw_start_date;

INSERT overwrite TABLE dw_orders_his
SELECT *
FROM dw_orders_his_tmp;



























