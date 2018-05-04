package com.li.flume;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Date;

public class FlumeTest {
    /**
     * flume入门 log4j 输出日志到flume
     * https://blog.csdn.net/poorcoder_/article/details/74939381
     *
     * flume监控spoolDir日志到HDFS整个流程
     * https://www.2cto.com/kf/201612/555835.html
     *
     * Flume环境搭建_五种案例
     * https://www.cnblogs.com/haozhengfei/p/2192231596ceb2ac4c22294dbd25a1ca.html
     *
     * https://www.cnblogs.com/dreammyle/p/6595693.html
     *
     * http://flume.apache.org/FlumeUserGuide.html
     */
    protected static final Log logger = LogFactory.getLog(FlumeTest.class);

    public static void main(String[] args) throws InterruptedException {

        while (true) {
            // 每隔两秒log输出一下当前系统时间戳
            logger.info(System.currentTimeMillis());
            //System.out.println(new Date().getTime());
            Thread.sleep(2000);
            try {
                throw new Exception("exception msg");
            } catch (Exception e) {
                logger.error("error:" + e.getMessage());
            }
        }
    }
}
