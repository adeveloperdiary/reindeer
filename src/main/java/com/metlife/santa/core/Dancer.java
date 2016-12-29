package com.metlife.santa.core;

import com.metlife.santa.config.DancerConfig;
import net.sf.cb2java.copybook.Copybook;
import net.sf.cb2java.copybook.CopybookParser;
import net.sf.cb2java.data.Record;
import org.apache.spark.api.java.function.Function;
import org.codehaus.jackson.type.TypeReference;
import java.io.IOException;
import java.util.Map;


public class Dancer extends ReindeerBase{

    public ReindeerBase init(String file) throws IOException {

        TypeReference<DancerConfig> typeRef
                = new TypeReference<DancerConfig>() {};

        init(typeRef,file);
        return this;
    }

    @Override
    public void process() {

        DancerConfig objDancerConfig=(DancerConfig)config;

        Copybook copybook = CopybookParser.parse("A", this.getClass().getClassLoader().getResourceAsStream(objDancerConfig.getCopybook_url()));

        this.rddOutput= this.rddInput.map(new Function<String,Map>() {
            @Override
            public Map call(String line) throws Exception {
                //TODO - Serialize Error
                Record record=copybook.parseData(line.getBytes());
                return record.toMap();

            }
        });


    }

    public void chain(ReindeerBase previous){
        this.rddInput=previous.rddOutput;
    }
}
