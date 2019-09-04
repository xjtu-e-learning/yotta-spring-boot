package com.xjtu.spider_topic.service;

import com.xjtu.common.domain.Result;
import com.xjtu.common.domain.ResultEnum;
import com.xjtu.domain.domain.Domain;
import com.xjtu.domain.repository.DomainRepository;
import com.xjtu.domain.service.DomainService;
import com.xjtu.facet.domain.Facet;
import com.xjtu.facet.repository.FacetRepository;
import com.xjtu.spider_topic.common.SpiderRunnable;
import com.xjtu.spider_topic.spiders.wikicn.FragmentCrawler;
import com.xjtu.spider_topic.spiders.wikicn.TopicCrawler;
import com.xjtu.topic.domain.Topic;
import com.xjtu.topic.repository.TopicRepository;
import com.xjtu.utils.Log;
import com.xjtu.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TSpiderService {
    @Autowired
    private DomainRepository domainRepository;

    @Autowired
    private DomainService domainService;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private FacetRepository facetRepository;

    // 中文网站爬虫
    public Result TSpider(String domainName) throws Exception {
        Domain domain = domainRepository.findByDomainName(domainName);
        List<Topic> topics = topicRepository.findByDomainName(domainName);
        List<Facet> facets = facetRepository.findByDomainName(domainName);
        if (domain == null) {
            Log.log("==========知识森林里还没有这门课程，开始爬取课程：" + domainName+"==========");
            Result result = domainService.insertDomainByName(domainName);
            Domain domain_new = domainRepository.findByDomainName(domainName);
            Runnable runnable=new SpiderRunnable(domain_new);
            Thread thread=new Thread(runnable);
            thread.start();
            return ResultUtil.error(ResultEnum.TSPIDER_ERROR.getCode(),ResultEnum.TSPIDER_ERROR.getMsg());
        } else if(domain != null && (topics == null||topics.size()==0) && (facets == null||facets.size()==0))
        {
            return ResultUtil.error(ResultEnum.TSPIDER_ERROR1.getCode(),ResultEnum.TSPIDER_ERROR1.getMsg());
        } else if(domain != null && (topics != null && topics.size()!=0) && (facets == null||facets.size()==0))
        {
            return ResultUtil.error(ResultEnum.TSPIDER_ERROR2.getCode(),ResultEnum.TSPIDER_ERROR2.getMsg());
        }
        else {
            return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), "==========该课程的知识主题分面树已成功构建==========");
        }
    }
    /**
     * 爬取一门课程：主题、分面、分面关系
     *
     * @param domain 课程
     */
    public static void constructKGByDomainName(Domain domain) throws Exception {
        // 爬取并存储知识主题
        TopicCrawler.storeTopic(domain);
        // 爬取并存储知识主题对应的分面，形成知识主题分面树（不含碎片信息及知识主题间的认知关系）
        FragmentCrawler.storeKGByDomainName(domain);
    }

}
