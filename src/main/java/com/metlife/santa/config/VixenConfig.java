package com.metlife.santa.config;

import java.util.ArrayList;

public class VixenConfig {

    private ArrayList<def> def;

    public ArrayList<com.metlife.santa.config.def> getDef() {
        return def;
    }

    public void setDef(ArrayList<com.metlife.santa.config.def> def) {
        this.def = def;
    }
}

class def{

    private String name;
    private int start;

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    private int length;
    private boolean required;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }


}
