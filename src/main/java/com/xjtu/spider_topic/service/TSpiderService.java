package com.xjtu.spider_topic.service;

import com.xjtu.common.Config;
import com.xjtu.domain.domain.Domain;
import com.xjtu.domain.repository.DomainRepository;
import com.xjtu.spider_topic.spiders.wikicn.FragmentCrawler;
import com.xjtu.spider_topic.spiders.wikicn.MysqlReadWriteDAO;
import com.xjtu.spider_topic.spiders.wikicn.TopicCrawler;
import com.xjtu.topic.repository.TopicRepository;
import com.xjtu.utils.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class TSpiderService {
    @Autowired
    private DomainRepository domainRepository;
    @Autowired
    private TopicRepository topicRepository;

    // 中文网站爬虫
    public void TSpider(String domainName) throws Exception {
        // 如果数据库中表格不存在，先新建数据库表格
        //DatabaseUtils.createTable();
        Domain domain = domainRepository.findByDomainName(domainName);
        boolean hasSpidered = MysqlReadWriteDAO.judgeByClass(Config.DOMAIN_TABLE, domain.getDomainName());
        // 如果domain表已经有这门课程，就不爬取这门课程的数据，没有就爬取
        if (!hasSpidered) {
            Log.log("domain表格没有这门课程，开始爬取课程：" + domain);
            constructKGByDomainName(domain);
        } else {
            Log.log("domain表格有这门课程，不需要爬取课程：" + domain);
        }
    }
    /**
     * 爬取一门课程：主题、分面、分面关系、【主题认知关系(gephi文件)、碎片（中文维基）】
     *
     * @param domain 课程
     */
    public static void constructKGByDomainName(Domain domain) throws Exception {
        String domainName = domain.getDomainName();
        // 以下：存储领域
        TopicCrawler.storeDomain(domain);
        // 存储主题
        TopicCrawler.storeTopic(domain);
        // 存储分面和碎片
        FragmentCrawler.storeKGByDomainName(domainName);
    }

}
