package com.zehaogao.weatherapp;

import java.util.List;

public class MyUtility {

    public static int round(Double d) {
        double dAbs = Math.abs(d);
        int i = (int) dAbs;
        double result = dAbs - (double) i;
        if (result < 0.5) {
            return d < 0 ? -i : i;
        }
        else {
            return d < 0 ? -(i + 1) : (i + 1);
        }
    }

    public static String getStatus(String str) {
        if (str.equals("partly-cloudy-night")) return "cloudy night";
        else if (str.equals("partly-cloudy-day")) return "cloudy day";
        else return str.replace('-', ' ');
    }

    public static void printList(List<String> list) {
        System.out.println("===============================");
        for (String str : list) {
            System.out.println(str);
        }
        System.out.println("===============================");
        System.out.println();
    }
}
