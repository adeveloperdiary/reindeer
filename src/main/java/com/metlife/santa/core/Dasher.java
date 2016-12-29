package com.metlife.santa.core;

import com.metlife.santa.config.DasherConfig;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.codehaus.jackson.type.TypeReference;
import java.io.IOException;

public class Dasher extends Reindeer {

    private SparkConf conf;

    private JavaSparkContext sc;

    public void init(String file) throws IOException{
        TypeReference<DasherConfig> typeRef
                = new TypeReference<DasherConfig>() {
        };

        init(typeRef,file);
    }

    public void process(){
        DasherConfig objDasherConfig=(DasherConfig)config;

        this.conf=new SparkConf().setAppName(objDasherConfig.getName()).setMaster(objDasherConfig.getMaster());
        this.sc=new JavaSparkContext(conf);

        this.rddOutput=this.sc.textFile(objDasherConfig.getInput_url());
    }

    public void stop(){
        this.sc.stop();
    }

    public JavaSparkContext getSc() {
        return sc;
    }



}
