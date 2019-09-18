package com.li.zookeeper;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.List;

/**
 * Zookeeper Api(java)入门与应用(转)
 * http://www.cnblogs.com/ggjucheng/p/3370359.html
 */
public class ZKDemo {

    private static final int TIME_OUT = 3000;
    private static final String HOST = "huatu68:2181,huatu70:2181,huatu72:2181";

    public static void main(String[] args) throws IOException, KeeperException, InterruptedException {

        ZooKeeper zookeeper = new ZooKeeper(HOST, TIME_OUT, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println("已经触发了" + event.getType() + "事件！");
            }
        });
        System.out.println("==========================");
        System.out.println(zookeeper.getState());

        List<String> zookeeperChildrens = zookeeper.getChildren("/", false);

        for (String child : zookeeperChildrens) {

            System.out.println(child);
        }
    }
}
