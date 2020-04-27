package com.li.c3p0;

import org.apache.hadoop.util.hash.Hash;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Handler;

public class DBUtil {

    public static final String url = "jdbc:mysql://192.168.27.128/july";
    public static final String name = "com.mysql.jdbc.Driver";
    public static final String user = "root";
    public static final String password = "111111";

    public Connection conn = null;
    public PreparedStatement pst = null;

    private static final int CAPACITY = 90000;//数据容量

    // 定义一个byte数组缓存所有的数据
    private static byte[] dataBytes = new byte[1 << 29];

    public DBUtil(String sql) {
        try {
            Class.forName(name);//指定连接类型
            conn = DriverManager.getConnection(url, user, password);//获取连接
            pst = conn.prepareStatement(sql);//准备执行语句
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            this.conn.close();
            this.pst.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {

        String sql = "select * from mytab";
        DBUtil db1 = new DBUtil(sql);
        Map<String, Integer> record = new HashMap<>();
        int k = 0;
        byte[] bits = new byte[getIndex(90000) + 1];
        try {
            ResultSet ret = db1.pst.executeQuery();
            while (ret.next()) {
                String uid = ret.getString(1);
                String ufname = ret.getString(2);

                int h;
                int n = 90000;
                int hash = (h = ufname.hashCode()) ^ (h >>> 16);
                int i = (n - 1) & hash;
                System.out.println(uid + "\t" + ufname + "\t" + hash + "\t" + k);
                add(bits, i);
            }
            ret.close();
            db1.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        int index = 1;
        System.out.println(record.size());
//        for (byte bit : bits) {
//            System.out.println("-------" + index++ + "-------");
//            showByte(bit);
//        }
        System.out.println("");
    }

    public static void showByte(byte b) {
        byte[] array = new byte[8];
        for (int i = 7; i >= 0; i--) {
            array[i] = (byte) (b & 1);
            b = (byte) (b >> 1);
        }

        for (byte b1 : array) {
            System.out.print(b1);
            System.out.print(" ");
        }

        System.out.println();
    }

    public static int getIndex(int num) {
        return num >> 3;
    }

    private static byte[] splitBigData(int num) {

        long bitIndex = num + (1l << 31);         //获取num数据对应bit数组（虚拟）的索引
        int index = (int) (bitIndex / 8);         //bit数组（虚拟）在byte数组中的索引
        int innerIndex = (int) (bitIndex % 8);    //bitIndex 在byte[]数组索引index 中的具体位置

        System.out.println("byte[" + index + "] 中的索引：" + innerIndex);

        dataBytes[index] = (byte) (dataBytes[index] | (1 << innerIndex));
        return dataBytes;
    }

    public static void add(byte[] bits, int num) {
        bits[getIndex(num)] |= 1 << getPosition(num);
    }

    public static int getPosition(int num) {
        return num & 0x07;
    }

    //    private static   int getBit(int[] arr, int n) {
//        return (arr[n / BITS_PRE_WORD] & (1 << (n % BITS_PRE_WORD))) != 0 ? 1 : 0;
//    }
    private static void output(byte[] bytes) {
        int count = 0;
        for (int i = 0; i < bytes.length; i++) {
            for (int j = 0; j < 8; j++) {
                if (!(((bytes[i]) & (1 << j)) == 0)) {
                    count++;
                    int number = (int) ((((long) i * 8 + j) - (1l << 31)));
                    System.out.println("取出的第  " + count + "\t个数: " + number);
                }
            }
        }
    }
}
