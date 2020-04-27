package com.li.bitmap;

import com.li.utils.FileUtil;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class UuidDemo {

    public static void main(String[] args) throws Exception {

        String[] user_id = new String[10000];
        for (int i = 0; i < 10000; i++) {
            UUID uuid = UUID.randomUUID();
            user_id[i] = uuid.toString();
//
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date start = sdf.parse("2020-04-26");
        Calendar ca = Calendar.getInstance();
        ca.setTime(start);

        for (int i = 0; i < 30; i++) {
            for (int j = 0; j < 3000; j++) {
                int index = (int) (Math.random() * 10000);
                String uid = user_id[index];
                String logDate = sdf.format(ca.getTime());
                FileUtil.writeToTxt("user.txt", String.format("%s\t%s\r\n", logDate, uid));
            }
            ca.add(Calendar.DAY_OF_YEAR, 1);
        }


    }
}
