package com.xjtu.spider_dynamic_output.spiders.csdn;

import com.xjtu.domain.domain.Domain;
import com.xjtu.facet.domain.Facet;
import com.xjtu.spider_dynamic_output.spiders.FacetAssembleCrawler;
import com.xjtu.topic.domain.Topic;

public class CsdnThread extends Thread{
    public Domain domain;
    public Topic topic;
    public Facet facet;
    public Boolean incremental;


    public CsdnThread(Domain domain, Topic topic, Facet facet,Boolean incremental){
        this.domain=domain;
        this.topic=topic;
        this.facet=facet;
        this.incremental=incremental;
    }

    @Override
    public void run(){
        try {
            CsdnCrawler.csdnSpiderAssemble(domain,topic,facet,incremental);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}