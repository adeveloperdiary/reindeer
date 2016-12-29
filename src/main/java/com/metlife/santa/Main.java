package com.metlife.santa;

import com.metlife.santa.core.Dasher;
import com.metlife.santa.core.Vixen;

import java.io.IOException;


public class Main {
    public static void main(String[] args) throws IOException {
        Dasher j=new Dasher();



        j.init("santa.dasher.config.json");
        j.process();


        Vixen s=new Vixen();
        s.init("santa.vixen.config.json")
                .chain(j);
        s.process();

    }
}
