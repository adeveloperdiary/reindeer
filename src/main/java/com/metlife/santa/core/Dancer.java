package com.metlife.santa.core;

import net.sf.cb2java.copybook.Copybook;
import net.sf.cb2java.copybook.CopybookParser;
import net.sf.cb2java.data.Record;
import org.apache.spark.api.java.function.Function;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;


public class Dancer extends ReindeerBase implements Serializable {

    public ReindeerBase init(String file) throws IOException {

        initReindeer(file);
        return this;
    }

    @Override
    public void process() {
        try {
            String file=this.prop.getProperty("copybook_url");

            this.rddOutput=rddInput.map(new Function<String,Map>() {
                @Override
                public Map call(String v1) throws Exception {

                    Copybook copybook = CopybookParser.parse("A", this.getClass().getClassLoader().getResourceAsStream(file));
                    Record record=copybook.parseData(v1.getBytes());

                    return record.toMap();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void chain(ReindeerBase previous){
        this.rddInput=previous.rddOutput;
    }
}
