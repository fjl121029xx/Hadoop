package com.li.compress;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.CompressionCodecFactory;
import org.apache.hadoop.io.compress.CompressionOutputStream;
import org.apache.hadoop.util.ReflectionUtils;


public class Hello {

    public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
        if (args[0].equals("compress")) {
            compress(args[1], "org.apache.hadoop.io.compress." + args[2]);
        } else if (args[0].equals("decompress"))
            decompres(args[1]);
        else {
            System.err.println("Error!\n usgae: hadoop jar Hello.jar [compress] [filename] [compress type]");
            System.err.println("\t\ror [decompress] [filename] ");
            return;
        }
        System.out.println("down");
    }

    /*
     * filename是希望压缩的原始文件,method是欲使用的压缩方法（如BZip2Codec等）
     */
    public static void compress(String filername, String method) throws ClassNotFoundException, IOException {
        System.out.println("[" + new Date() + "] : enter compress");
        File fileIn = new File(filername);
        InputStream in = new FileInputStream(fileIn);

        Class codecClass = Class.forName(method);
        Configuration conf = new Configuration();

        // 通过名称找到对应的编码/解码器
        CompressionCodec codec = (CompressionCodec)
                ReflectionUtils.newInstance(codecClass, conf);

        // 该压缩方法对应的文件扩展名
        File fileOut = new File(filername + codec.getDefaultExtension());
        fileOut.delete();

        OutputStream out = new FileOutputStream(fileOut);
        CompressionOutputStream cout = codec.createOutputStream(out);

        System.out.println("[" + new Date() + "]: start compressing ");
        IOUtils.copyBytes(in, cout, 1024 * 1024 * 5, false);        // 缓冲区设为5MB
        System.out.println("[" + new Date() + "]: compressing finished ");

        in.close();
        cout.close();
    }

    /*
     * filename是希望解压的文件
     */
    public static void decompres(String filename) throws FileNotFoundException, IOException {
        System.out.println("[" + new Date() + "] : enter compress");

        Configuration conf = new Configuration();
        CompressionCodecFactory factory = new CompressionCodecFactory(conf);
        CompressionCodec codec = factory.getCodec(new Path(filename));
        if (null == codec) {
            System.out.println("Cannot find codec for file " + filename);
            return;
        }

        File fout = new File(filename + ".decoded");
        InputStream cin = codec.createInputStream(new FileInputStream(filename));
        OutputStream out = new FileOutputStream(fout);

        System.out.println("[" + new Date() + "]: start decompressing ");
        IOUtils.copyBytes(cin, out, 1024 * 1024 * 5, false);
        System.out.println("[" + new Date() + "]: decompressing finished ");

        cin.close();
        out.close();
    }
}

