package com.xjtu.spider_topic.common;

import com.xjtu.domain.domain.Domain;
import com.xjtu.utils.Log;

import static com.xjtu.spider_topic.service.TFSpiderService.constructTopicFacetTreeByDomainName;

public class SpiderRunnable implements Runnable {
    private Domain domain;

    public SpiderRunnable(Domain domain) {
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

