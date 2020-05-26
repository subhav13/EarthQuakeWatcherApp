package com.example.earthqk.utils;

import java.util.Random;

public class Constants {
    public static final String url = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/2.5_week.geojson";
    public static final int LIMIT = 20;
    public static int randomInt(int max,int min){
        return new Random().nextInt(max-min)+min;
    }
}
