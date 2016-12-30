package com.metlife.santa.core;

import com.metlife.santa.config.DasherConfig;
import net.sf.cb2java.copybook.Copybook;
import net.sf.cb2java.copybook.CopybookParser;
import net.sf.cb2java.data.Record;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkFiles;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.codehaus.jackson.type.TypeReference;

import java.io.*;
import java.util.Map;

public class Dasher extends ReindeerBase implements Serializable{

    private SparkConf conf;

    private JavaSparkContext sc;

    public void init(String file) throws IOException{
        TypeReference<DasherConfig> typeRef
                = new TypeReference<DasherConfig>() {
        };

        initReindeer(typeRef,file);
    }

    public void process(){
        DasherConfig objDasherConfig=(DasherConfig)config;

        this.conf=new SparkConf().setAppName(objDasherConfig.getName()).setMaster(objDasherConfig.getMaster());
        this.sc=new JavaSparkContext(conf);

        this.rddOutput=this.sc.textFile(objDasherConfig.getInput_url());

    }


    public JavaSparkContext getSc() {
        return sc;
    }



}
