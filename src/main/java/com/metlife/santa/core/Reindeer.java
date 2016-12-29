package com.metlife.santa.core;

import org.apache.spark.api.java.JavaRDD;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.io.InputStream;

abstract public class Reindeer {

    protected JavaRDD rddInput;
    protected JavaRDD rddOutput;
    protected JavaRDD rddError;

    protected Object config;

    public JavaRDD getRddInput() {
        return rddInput;
    }

    public JavaRDD getRddOutput() {
        return rddOutput;
    }

    public JavaRDD getRddError() {
        return rddError;
    }

    public void init(TypeReference typeRef,String file) throws IOException {

        ObjectMapper obj=new ObjectMapper();
        InputStream in=null;
        try {
            in = this.getClass().getClassLoader().getResourceAsStream(file);
            this.config = obj.readValue(in, typeRef);
        }finally {
            in.close();
        }
    }

    public void chain(Reindeer previous){
    }

    abstract public void process();

    public void stop(){}
}





