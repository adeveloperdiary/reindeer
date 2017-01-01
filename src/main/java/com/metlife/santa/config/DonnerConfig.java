package com.metlife.santa.config;

import java.util.ArrayList;
import java.util.List;

public class DonnerConfig implements java.io.Serializable{

    public List<Mapping> getMapping() {
        return mapping;
    }

    public void setMapping(List<Mapping> mapping) {
        this.mapping = mapping;
    }

    private List<Mapping> mapping=new ArrayList<Mapping>();

}

class Mapping implements java.io.Serializable{

    private String entityName;
    private String sorEntityName;
    private String lookups;

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getSorEntityName() {
        return sorEntityName;
    }

    public void setSorEntityName(String sorEntityName) {
        this.sorEntityName = sorEntityName;
    }

    public String getLookups() {
        return lookups;
    }

    public void setLookups(String lookups) {
        this.lookups = lookups;
    }

    public ArrayList<Attribute> getAttributeMap() {
        return attributeMap;
    }

    public void setAttributeMap(ArrayList<Attribute> attributeMap) {
        this.attributeMap = attributeMap;
    }

    private ArrayList<Attribute> attributeMap=new ArrayList<Attribute>();




}

class Attribute implements java.io.Serializable {

    private String name;
    private String source;

    public String getxForm() {
        return xForm;
    }

    public void setxForm(String xForm) {
        this.xForm = xForm;
    }

    private String xForm;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }



}
