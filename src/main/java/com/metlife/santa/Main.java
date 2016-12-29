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
        d.init("santa.dancer.config.json")
                .chain(j);
        d.process();

        Vixen s=new Vixen();
        s.init("santa.vixen.config.json")
                .chain(d);
        s.process();

        j.stop();

    }
}
