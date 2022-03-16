package com.sigfox.support;

import java.util.Arrays;

public class Device {


    private String id;
    private BaseStation [] baseStation;

    public Device() {
        baseStation = new BaseStation[3];
        id="";
    }

    public Device(String id, BaseStation[] baseStation) {
        this.id = id;
        this.baseStation = baseStation;
    }
    public Device(String id) {
        this.id = id;
        this.baseStation = null;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setBaseStations(BaseStation[] baseStation) {
        this.baseStation = baseStation;
    }
    public BaseStation[] getBaseStations() {
        return baseStation;
    }

    @Override
    public String toString() {
        String s ="";
        s+= "Device ID: " + id +"\n";
        if (baseStation!=null) {
            for (int i = 0; i < 3; i++) {
            BaseStation bs = baseStation[i];
            s+="Base Station "+(i+1)+"\nID: "+bs.getId()+"\nRSSI: "+bs.getRssi()+"\nLatitude"+bs.getLat()+"\nLongitude: "+bs.getLon()+"\n\n";
            }
        }

        return s;
    }

}
