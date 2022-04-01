package com.li.hive.example.udf.bitmap;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.ql.exec.UDF;
import org.roaringbitmap.RoaringBitmap;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;


/**
 * @Author: lijunjie
 * @Date: 2021/11/29 16:25
 */
public class BitMapCheckUDF extends UDF {
    private RoaringBitmap roaringBitmap = new RoaringBitmap();
    private boolean flag = true;
    private Set<Integer> smallSet = new HashSet<>();
    private byte firstPosition = 0;

    public boolean evaluate(int userCode, String filePath) throws IOException {

        if (flag) {
            FileSystem fs = FileSystem.get(new Configuration());
            Path path = new Path(filePath);
            FSDataInputStream in = fs.open(path);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String bitmapStr = null;
            if ((bitmapStr = reader.readLine()) != null) {

            }

            byte[] bytes = Base64.getDecoder().decode(bitmapStr);
            ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
            buffer.put(bytes);
            buffer.flip();

            firstPosition = buffer.get();

            if (firstPosition == 0) {
                buffer = buffer.slice().order(ByteOrder.LITTLE_ENDIAN);
                int len = buffer.get();
                int i = 1;
                while (i <= len) {
                    int item = buffer.getInt();
                    smallSet.add(item);
                    i++;
                }
            } else {
                int length = getVarInt(buffer);
                byte[] bytesRes = new byte[bytes.length];
                buffer.get(bytesRes, 0, bytes.length - varIntSize(length) - 1);
                roaringBitmap.deserialize(new DataInputStream(new ByteArrayInputStream(bytesRes)));
            }


            flag = false;
        }

        return firstPosition == 1 ? roaringBitmap.contains(userCode) : smallSet.contains(userCode);
    }

    private int varIntSize(int i) {
        int result = 0;
        do {
            result++;
            i >>>= 7;
        } while (i != 0);
        return result;
    }

    private int getVarInt(ByteBuffer src) {
        int tmp;
        if ((tmp = src.get()) >= 0) {
            return tmp;
        }
        int result = tmp & 0x7f;
        if ((tmp = src.get()) >= 0) {
            result |= tmp << 7;
        } else {
            result |= (tmp & 0x7f) << 7;
            if ((tmp = src.get()) >= 0) {
                result |= tmp << 14;
            } else {
                result |= (tmp & 0x7f) << 14;
                if ((tmp = src.get()) >= 0) {
                    result |= tmp << 21;
                } else {
                    result |= (tmp & 0x7f) << 21;
                    result |= (tmp = src.get()) << 28;
                    while (tmp < 0) {
                        // We get into this loop only in the case of overflow.
                        // By doing this, we can call getVarInt() instead of
                        // getVarLong() when we only need an int.
                        tmp = src.get();
                    }
                }
            }
        }
        return result;
    }

//    public static void main(String[] args) throws IOException {
//
////        File f = new File("E:\\id_encode.csv");
////        BufferedReader reader = new BufferedReader(new FileReader(f));
////        String tempString = null;
////        int line = 1;
////        // 一次读入一行，直到读入null为文件结束
////
////        int trueNum = 0;
////        while ((tempString = reader.readLine()) != null) {
////            // 显示行号
//////            System.out.println("line " + line + ": " + tempString);
////            line++;
////            int i = Integer.parseInt(tempString);
////            boolean evaluate = evaluate(i, "E:\\crowid-925556.txt");
////            System.out.println(line + "," + evaluate);
////            if (evaluate) {
////                trueNum++;
////            }
////        }
////        reader.close();
////        System.out.println(trueNum);
//
//        long l = System.currentTimeMillis();
////        boolean evaluate = evaluate(203, "E:\\new1.txt");
//        boolean evaluate = evaluate(1610928895, "E:\\crowid-925557.txt");
//        long l1 = System.currentTimeMillis();
//        System.out.println(l1 - l);
//
////
//        l = System.currentTimeMillis();
////        evaluate = evaluate(203, "E:\\new2.txt");
//        evaluate = evaluate(1610928895, "E:\\crowid-925556.txt");
//        l1 = System.currentTimeMillis();
//        System.out.println(l1 - l);
//
//    }
}
