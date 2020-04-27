package com.li.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class FileUtil {


    public static void writeToTxt(String filename, String line) {
        FileOutputStream outSTr = null;
        BufferedOutputStream Buff = null;
        try {
            outSTr = new FileOutputStream(new File(filename),true);
            Buff = new BufferedOutputStream(outSTr);
            Buff.write(line.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                Buff.flush();
                Buff.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }
}
