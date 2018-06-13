package com.fw.module;

import com.fw.plugin.FWPlugin;


public interface FWModule {


    void initialize(FWPlugin fwPlugin);

 
    void start();


    void stop();


    void destroy();
    

    public boolean isStart();
}
