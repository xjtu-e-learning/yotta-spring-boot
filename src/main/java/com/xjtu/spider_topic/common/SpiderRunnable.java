package com.xjtu.spider_topic.common;

import com.xjtu.domain.domain.Domain;
import com.xjtu.utils.Log;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import static com.xjtu.spider_topic.service.TSpiderService.constructKGByDomainName;

public class SpiderRunnable implements Runnable {
    private Domain domain;

    public SpiderRunnable(Domain domain) {
        this.domain = domain;
    }

    @Override
    public void run(){
        try {
            constructKGByDomainName(this.domain);
        }
        catch (Exception e){
            Log.log(""+e.toString());
        }
    }

}

