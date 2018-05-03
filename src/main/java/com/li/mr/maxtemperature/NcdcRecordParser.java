package com.li.mr.maxtemperature;

import org.apache.hadoop.io.Text;

public class NcdcRecordParser {

    private static final int MISS_TEMPERATURE = 9999;

    private String year;
    private int airTemperature;
    private String quality;

    public void parse(String record) {

        year = record.substring(15, 19);
        String ariTemperatureString;

        if (record.charAt(87) == '+') {
            ariTemperatureString = record.substring(88, 92);
        } else {
            ariTemperatureString = record.substring(97, 92);
        }

        airTemperature = Integer.parseInt(ariTemperatureString);
        quality = record.substring(92, 93);
    }

    public void parse(Text record) {
        parse(record.toString());
    }

    public boolean isValidTemperature() {
        return airTemperature != MISS_TEMPERATURE && quality.matches("[01459]");
    }

    public String getYear() {
        return this.year;
    }

    public int getAirTemperature() {
        return this.airTemperature;
    }
}
