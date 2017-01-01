package com.metlife.santa.config;


public class DasherConfig implements java.io.Serializable{

    private String name;
    private String master;
    private String input_url;

    public String getInput_url() {
        return input_url;
    }

    public void setInput_url(String input_url) {
        this.input_url = input_url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMaster() {
        return master;
    }

    public void setMaster(String master) {
        this.master = master;
    }



}


