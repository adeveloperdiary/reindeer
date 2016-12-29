package com.metlife.santa.core;

import com.metlife.santa.config.DasherConfig;
import org.codehaus.jackson.type.TypeReference;
import java.io.IOException;

public class Vixen extends Reindeer {

    public Reindeer init(String file) throws IOException {

        TypeReference<DasherConfig> typeRef
                = new TypeReference<DasherConfig>() {};

        init(typeRef,file);
        return this;
    }

    public void process(){
        this.rddInput.collect().forEach(o->{
            System.out.println(o);
        });
    }

    public void chain(Reindeer previous){
        this.rddInput=previous.rddOutput;
    }

}
