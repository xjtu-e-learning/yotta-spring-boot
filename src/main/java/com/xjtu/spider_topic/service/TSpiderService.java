package com.xjtu.spider_topic.service;

import com.xjtu.common.Config;
import com.xjtu.common.domain.Result;
import com.xjtu.common.domain.ResultEnum;
import com.xjtu.domain.domain.Domain;
import com.xjtu.domain.repository.DomainRepository;
import com.xjtu.domain.service.DomainService;
import com.xjtu.spider_topic.spiders.wikicn.FragmentCrawler;
import com.xjtu.spider_topic.spiders.wikicn.MysqlReadWriteDAO;
import com.xjtu.spider_topic.spiders.wikicn.TopicCrawler;
import com.xjtu.utils.Log;
import com.xjtu.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TSpiderService {
    @Autowired
    private DomainRepository domainRepository;

    @Autowired
    private DomainService domainService;


    // 中文网站爬虫
    public Result TSpider(String domainName) throws Exception {
        Domain domain = domainRepository.findByDomainName(domainName);
        if (domain == null) {
            Log.log("domain表格没有这门课程，开始爬取课程：" + domainName);
            Result result = domainService.insertDomainByName(domainName);
            Domain domain1 = domainRepository.findByDomainName(domainName);
            constructKGByDomainName(domain1);
        } else {
            Log.log("domain表格有这门课程，不需要爬取课程：" + domain);
            return ResultUtil.error(ResultEnum.TSPIDER_ERROR.getCode(),ResultEnum.TSPIDER_ERROR.getMsg());
        }
        return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), "该课程的知识主题分面树已成功构建");
    }
    /**
     * 爬取一门课程：主题、分面、分面关系、【主题认知关系(gephi文件)、碎片（中文维基）】
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
