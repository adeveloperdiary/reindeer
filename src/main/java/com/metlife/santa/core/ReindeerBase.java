package com.metlife.santa.core;

import org.apache.spark.api.java.JavaRDD;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.io.*;
import java.util.Properties;

abstract public class ReindeerBase implements Serializable{

    protected JavaRDD rddInput;
    protected JavaRDD rddOutput;
    protected JavaRDD rddError;
    protected Properties prop;
    protected Object config;

    public ReindeerBase(){
        this.prop=new Properties();
    }

    public void initReindeer(String file) throws IOException{
        this.prop.load(this.getClass().getClassLoader().getResourceAsStream(file));
    }


    public void initReindeer(TypeReference typeRef,String file) throws IOException {

        ObjectMapper obj=new ObjectMapper();
        InputStream in=null;
        try {
            in = this.getClass().getClassLoader().getResourceAsStream(file);
            this.config = obj.readValue(in, typeRef);
        }finally {
            in.close();
        }
    }

    public void chain(ReindeerBase previous){
    }

    abstract public void process();


    public JavaRDD getRddInput() {
        return rddInput;
    }

    public JavaRDD getRddOutput() {
        return rddOutput;
    }

    public JavaRDD getRddError() {
        return rddError;
    }
}





