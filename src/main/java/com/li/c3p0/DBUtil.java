package com.li.c3p0;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

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

        String sql = "select * from mytab  ";
        DBUtil db1 = new DBUtil(sql);
        Map<String, Integer> record = new HashMap<>();
        int k = 0;
        byte[][] bits = new byte[30][getIndex(10000) + 1];
//        byte[] bits = new byte[getIndex(90000) + 1];
        try {
            ResultSet ret = db1.pst.executeQuery();
            while (ret.next()) {
                String uid = ret.getString(1);
                String ufname = ret.getString(2);
                int hash = ret.getInt(3);
                int pt_index = ret.getInt(4);
                int index = ret.getInt(5);

//                System.out.println(uid + "\t" + ufname + "\t" + hash + "\t" + pt_index + "\t" + index);
                add(bits, pt_index, index);
            }
            ret.close();
            db1.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        int index = 0;
        System.out.println(record.size());
        for (byte[] bit : bits) {
            System.out.println("-------" + index++ + "-------");
            for (byte b : bit) {
                showByte(b);
            }
            System.out.println();
            System.out.println("-------[-]-------");
        }
        System.out.println("");

        // 新增
//        byte[] a = bits[0];
//        for (int i = 0; i < a.length; i++) {
//            byte b = a[i];
//            showByte(b);
//        }
//        System.out.println();
//        byte[] b = bits[1];
//        for (int i = 0; i < b.length; i++) {
//            byte c = b[i];
//            showByte(c);
//        }
        byte[] bit = bits[0];
        byte[] a = formatByte(bit);
        byte[] bit1 = bits[1];
        byte[] b = formatByte(bit1);

        System.out.println();
        printByte(a);
        System.out.println();
        printByte(b);
        System.out.println();

        int newAdd = 0;
        int save = 0;
        int[] sad = new int[a.length];
        for (int i = 0; i < a.length; i++) {
            byte s = a[i];
            byte e = b[i];
            int tmp = s ^ e;
            if (s == 1 && tmp == 0) {
                save++;
            } else if (s == 0 && tmp == 1) {
                newAdd++;
            }
            sad[i] = tmp;
            System.out.print(" ");
        }
        System.out.println();
        for (int i : sad) {
            System.out.print(i);
            System.out.print(" ");
        }
        System.out.println();
        System.out.println("save " + save);
        System.out.println("2020-04-27 newAdd " + newAdd);


        byte[] newAdd28 = new byte[a.length];
        for (int i = 0; i < a.length; i++) {
            byte s = a[i];
            byte e = b[i];
            byte tmp = (byte) (s | e);
            newAdd28[i] = tmp;
        }

        System.out.println();
        for (int i : newAdd28) {
            System.out.print(i);
            System.out.print(" ");
        }
        System.out.println();
        byte[] c = formatByte(bits[2]);
        for (int i : c) {
            System.out.print(i);
            System.out.print(" ");
        }

        newAdd = 0;
        save = 0;
        sad = new int[a.length];
        for (int i = 0; i < newAdd28.length; i++) {
            byte s = newAdd28[i];
            byte e = c[i];
            int tmp = s ^ e;
            if (s == 1 && tmp == 0) {
                save++;
            } else if (s == 0 && tmp == 1) {
                newAdd++;
            }
            sad[i] = tmp;
            System.out.print(" ");
        }
        System.out.println();
        for (int i : sad) {
            System.out.print(i);
            System.out.print(" ");
        }
        System.out.println();
        System.out.println("save " + save);
        System.out.println("2020-04-28 newAdd " + newAdd);


//
//        System.out.println();
//        System.out.println("x新增-------------------------");
//        int[] array = new int[8 * bit.length];
//        for (int i = 0; i < bit.length; i++) {
//            array[i] = bit[i] ^ bit1[i];
//        }
//        for (int b1 : array) {
//            System.out.print(b1);
//            System.out.print(" ");
//        }
    }

    public static byte[] formatByte(byte[] b) {
        byte[] array = new byte[8 * b.length];
        for (int j = 0; j < b.length; j++) {
            for (int i = (7 + j * 8); i >= (0 + j * 8); i--) {
                array[i] = (byte) (b[j] & 1);
                b[j] = (byte) (b[j] >> 1);
            }
        }
        return array;
    }

    public static void printByte(byte[] array) {
        for (byte b1 : array) {
            System.out.print(b1);
            System.out.print(" ");
        }
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
    }

    public static int getIndex(int num) {
        return num >> 3;
    }

    /**
     * 标记指定数字（num）在bitmap中的值，标记其已经出现过
     * 将1左移position后，那个位置自然就是1，然后和以前的数据做|，这样，那个位置就替换成1了
     *
     * @param bits
     * @param num
     */
    public static void add(byte[][] bits, int index, int num) {
        bits[index][getIndex(num)] |= 1 << getPosition(num);
    }

    //    num % 8
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
