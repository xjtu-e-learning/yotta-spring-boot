package com.xjtu.spider_dynamic_output.spiders;

import com.xjtu.domain.domain.Domain;
import com.xjtu.spider_dynamic_output.spiders.wikicn.AssembleCrawler;
import com.xjtu.spider_dynamic_output.spiders.wikicn.FacetCrawler;
import com.xjtu.topic.domain.Topic;

/**
 * 分面+碎片爬虫
 * @author ljj
 * */
public class FacetAssembleCrawler {
    public static void storeFacetAssemble(Domain domain, Topic topic) throws Exception {
        FacetCrawler.storeFacetByTopicName(domain,topic);
        FacetCrawler.setIsCompleted(false);
        AssembleCrawler.storeAssembleByTopicName(domain, topic);
    }
}
