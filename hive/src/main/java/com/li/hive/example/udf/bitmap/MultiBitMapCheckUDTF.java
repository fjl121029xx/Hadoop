package com.li.hive.example.udf.bitmap;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDTF;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.roaringbitmap.RoaringBitmap;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.*;

/**
 * @Author: lijunjie
 * @Date: 2021/11/29 16:25
 */
public class MultiBitMapCheckUDTF extends GenericUDTF {
    private byte firstPosition = 0;

    private String[] obj = new String[2];
    private Map<String, RoaringBitmap> rbMap = new HashMap<>();
    private Map<String, Set<Integer>> setMap = new HashMap<>();


    @Override
    public StructObjectInspector initialize(ObjectInspector[] objectInspectors) throws UDFArgumentException {

        // 输出
        List<String> fieldNames = new ArrayList<String>(2);
        List<ObjectInspector> fieldOIs = new ArrayList<ObjectInspector>(2);

        // 输出列名
        fieldNames.add("id");
        fieldOIs.add(PrimitiveObjectInspectorFactory.javaStringObjectInspector);
        return ObjectInspectorFactory.getStandardStructObjectInspector(fieldNames, fieldOIs);
    }

    @Override
    public void process(Object[] args) throws HiveException {

        int userCode = Integer.parseInt(args[0].toString());
        List<String> filePath = Arrays.asList(args[1].toString().split(":"));

        List<Integer> cid = new ArrayList<>(filePath.size() + 1);
        int isZero = 0;
        for (int k = 0; k < filePath.size(); k++) {
            cid.add(0);
            String dt = filePath.get(k).split(",")[0];
            String packageId = filePath.get(k).split(",")[1];
            String pathString = String.format("/home/hdp_teu_dpd/warehouse/hdp_teu_dpd_fuxi_db/dim_crowd_protocol_analysis/dt=%s/crowd_id=%s/crowid-%s.txt", dt, packageId, packageId);

            if (!rbMap.containsKey(packageId) && !setMap.containsKey(packageId)) {
                try {
                    initBitmap(pathString, packageId);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (firstPosition == 1) {
                if (rbMap.get(packageId).contains(userCode)) {
                    obj[0] = String.valueOf(Integer.parseInt(packageId));
                    forward(obj);
//                    int i = Integer.parseInt(packageId);
//                    cid.set(k, i);
//                    isZero += i;
                }
            }
            if (firstPosition == 0) {
                if (setMap.get(packageId).contains(userCode)) {
                    obj[0] = String.valueOf(Integer.parseInt(packageId));
                    forward(obj);
//                    int i = Integer.parseInt(packageId);
//                    cid.set(k, i);
//                    isZero += i;
                }
            }
        }
        cid.add(isZero);
    }

    @Override
    public void close() throws HiveException {

    }

    private void initBitmap(String pathString, String crowdId) throws IOException {
        FileSystem fs = FileSystem.get(new Configuration());
        Path path = new Path(pathString);
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
            Set<Integer> smallSet = new HashSet<>();
            while (i <= len) {
                int item = buffer.getInt();
                smallSet.add(item);
                i++;
            }
            setMap.put(crowdId, smallSet);
        } else {
            RoaringBitmap roaringBitmap = new RoaringBitmap();
            int length = getVarInt(buffer);
            byte[] bytesRes = new byte[bytes.length];
            buffer.get(bytesRes, 0, bytes.length - varIntSize(length) - 1);
            roaringBitmap.deserialize(new DataInputStream(new ByteArrayInputStream(bytesRes)));
            rbMap.put(crowdId, roaringBitmap);
        }
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
}
