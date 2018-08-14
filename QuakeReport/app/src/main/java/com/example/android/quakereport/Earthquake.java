package com.example.android.quakereport;

public class Earthquake {
    private double mag;
    private String location;
    private String date;
    private String url;
    Earthquake(double mag, String location, String date,String url){
        this.mag = mag;
        this.location = location;
        this.date = date;
        this.url = url;
    }
    public double getMag(){
        return mag;
    }
    public String getLocation(){
        return location;
    }
    public String getDate() {
        return date;
    }

    public String getUrl() {
        return url;
    }
}
