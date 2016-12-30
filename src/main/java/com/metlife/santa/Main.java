package com.metlife.santa;

import com.metlife.santa.core.Dancer;
import com.metlife.santa.core.Dasher;
import com.metlife.santa.core.Vixen;

import java.io.IOException;



public class Main {
    public static void main(String[] args) throws IOException {



        Dasher j=new Dasher();


        j.init("santa.dasher.config.json");
        j.process();

        Dancer d=new Dancer();
        d.init("santa.dancer.properties")
                .chain(j);
        d.process();

        Vixen s=new Vixen();
        s.init("santa.vixen.config.json")
                .chain(d);
        s.process();




        /*SparkConf conf=new SparkConf().setAppName("Main").setMaster("local[*]");

        JavaSparkContext sc=new JavaSparkContext(conf);

        JavaRDD rdd=sc.textFile("hdfs://localhost:9000/stage/data/claim.input.txt");

        rdd.map(new Function<String,Map>() {
            @Override
            public Map call(String v1) throws Exception {

                Copybook copybook = CopybookParser.parse("A", new FileInputStream(new File("/Users/abhisekjana/spark/reindeer/src/main/resources/a.copybook")));
                Record record=copybook.parseData(v1.getBytes());

                return record.toMap();
            }
        }).map(new Function<Map,Map>() {
            @Override
            public Map call(Map v1) throws Exception {
                System.out.println(v1.get("Claim").toString());
                return v1;
            }
        })

                    .collect().forEach(s->{
                System.out.println(s);
            });*/
    }
}
