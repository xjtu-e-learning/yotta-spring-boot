package com.xjtu.spider_new.common;

import com.xjtu.domain.domain.Domain;
import com.xjtu.utils.Log;

import static com.xjtu.spider_new.service.NewSpiderService.constructTopicFacetTreeByDomainName;


public class NewSpiderRunnable implements Runnable {
    private Domain domain;

    public NewSpiderRunnable(Domain domain) {
        this.domain = domain;
    }

    @Override
    public void run(){
        try {
            constructTopicFacetTreeByDomainName(this.domain);
        }
        catch (Exception e){
            Log.log(""+e.toString());
        }
    }

}

