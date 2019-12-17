package com.zehaogao.weatherapp;

import java.io.Serializable;
import java.util.*;
import java.text.*;

public class Weather implements Serializable {

    private String date;
    private String condition;
    private int minTemp;
    private int maxTemp;

    public Weather(Long unixTime, String timezone, String condition, Double minTemp, Double maxTemp) {
        this.date = transferToDate(unixTime, timezone);
        this.condition = condition;
        this.minTemp = MyUtility.round(minTemp);
        this.maxTemp = MyUtility.round(maxTemp);
    }

    public String getDate() {
        return date;
    }

    public String getCondition() {
        return condition;
    }

    public int getMaxTemp() {
        return maxTemp;
    }

    public int getMinTemp() {
        return minTemp;
    }

    private String transferToDate(Long unixTime, String timezone) {

        long unix_seconds = unixTime;
        String timeZone = (timezone.equals("") ? "America/Los_Angeles" : timezone );

        Date date = new Date(unix_seconds * 1000L);

        SimpleDateFormat jdf = new SimpleDateFormat("MM/dd/yyyy");
        jdf.setTimeZone(TimeZone.getTimeZone(timeZone));

        String normalDate = jdf.format(date);
        return normalDate;
    }

}


