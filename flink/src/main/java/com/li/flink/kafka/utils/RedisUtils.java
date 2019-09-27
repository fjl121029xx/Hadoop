package com.li.flink.kafka.utils;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisUtils {

    private static JedisPool pool = null;

    private static int dbIndex = 0;

    private static void makePool() {
        if (pool == null) {
            GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
            int maxTotal = 300;
            poolConfig.setMaxTotal(maxTotal);
            int maxIdle = 150;
            poolConfig.setMaxIdle(maxIdle);
            int minIdle = 10;
            poolConfig.setMinIdle(minIdle);
            poolConfig.setTestOnBorrow(true);
            poolConfig.setTestOnReturn(true);
            Long maxWaitMillis = 10000L;
            poolConfig.setMaxWaitMillis(maxWaitMillis);
            poolConfig.setTestWhileIdle(true);
            poolConfig.setTestOnCreate(true);
            String redisHost = "192.168.65.128";
            int redisPort = 6379;
            int redisTimeout = 0;
            pool = new JedisPool(poolConfig, redisHost, redisPort, redisTimeout);

            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    super.run();
                    pool.destroy();
                }
            });
        }
    }

    private static JedisPool getPool() {
        if (pool == null) {
            makePool();
        }
        return pool;
    }

    public static Jedis getJedis() {
        Jedis jedis = null;
        boolean flag = true;
        int j = 0;
        int reTry = 100;
        while (flag && j < reTry) {
            try {
                j++;
                jedis = RedisUtils.getPool().getResource();
                flag = false;
            } catch (Exception e) {
                e.printStackTrace();
                flag = true;
            }
        }
        return jedis;
    }

    public static void main(String[] args) {

        Jedis jedis = RedisUtils.getJedis();
        jedis.set("a", "b2");


    }
}
