package com.sigfox.support;

import com.google.gson.annotations.SerializedName;

import javax.lang.model.type.NullType;
import java.lang.reflect.Type;

public class BaseStation {

    private String id;
    private double lat;
    private double lon;
    private double rssi;
    private double circle_radius;
    private enum strength{
        TAPV2,TAPV3,TAPMV3


    };


    @SerializedName(value = "baseStation", alternate = "//")
    private RinfoBaseDetails baseDetails;

    //constructors


    public BaseStation()
    {
        id="";
        lat=0.0;
        lon=0.0;
        rssi=0.0;
        baseDetails = new RinfoBaseDetails();
        circle_radius = 0;

    }

    //csv base stations
    public BaseStation(String pId, double pLat, double pLon)
    {
        id=pId;
        lat=pLat;
        lon=pLon;

    }

    //API basestations
    public BaseStation(RinfoBaseDetails ri, double pRssi)
    {
        baseDetails = ri;
        id=baseDetails.getId();
        rssi=pRssi;

    }

    //Accessors and Mutators

    public String getId() {
        if (id==""){return baseDetails.getId();}
        else return id;
    }

    public double getCircle_radius() {

        double temp = ((Math.abs(rssi)/10)-9); //  |-107|/10 = 10.7-9 = 1.7
        circle_radius = Math.pow(temp,2)-temp+2; //  n^2-n+2
        return circle_radius;
    }

    public void setCircle_radius(int circle_radius) {
        this.circle_radius = circle_radius;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getRssi() {
        return rssi;
    }

    public void setRssi(double rssi) {
        this.rssi = rssi;
    }

    public String toString()
    {
        String s = "";
        s+="ID\t\tLatitude\tLongitude\n";
        s+=getId()+"\t"+getLat()+"\t"+getLon()+"\n";
        return s;
    }

    //for gpsvisualizer txt
    public String toStringVis()
    {
        String s = "";
        s+=getId()+","+getRssi()+","+getLat()+","+getLon()+","+getCircle_radius()+"\n";
        return s;
    }
}
