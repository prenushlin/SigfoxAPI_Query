package com.sigfox.support;

//because of the format of the JSON object
public class RinfoBaseDetails {
    private String id;


    public RinfoBaseDetails() {
        this.id = "";
    }

    public RinfoBaseDetails(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
