package com.example.earthqk.models;

public class EarthQuakes {
    private String place;
    private String type;

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDetailLink() {
        return detailLink;
    }

    public void setDetailLink(String detailLink) {
        this.detailLink = detailLink;
    }

    public double getMeg() {
        return meg;
    }

    public void setMeg(double meg) {
        this.meg = meg;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public EarthQuakes(){}
    public EarthQuakes(String place, String type, String detailLink, double meg, double lon, double lat, long time) {
        this.place = place;
        this.type = type;
        this.detailLink = detailLink;
        this.meg = meg;
        this.lon = lon;
        this.lat = lat;
        this.time = time;
    }

    private String detailLink;
    private double meg,lon,lat;
    private long time;

}
