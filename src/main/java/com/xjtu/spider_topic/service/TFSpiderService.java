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
import com.xjtu.topic.service.TopicService;
import com.xjtu.utils.Log;
import com.xjtu.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TFSpiderService {
    private static Boolean domainFlag;
    //获取课程的中英文状态
    public static boolean getDomainFlag(){
        return domainFlag;
    }

    @Autowired
    private DomainRepository domainRepository;

    @Autowired
    private DomainService domainService;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private TopicService topicService;

    @Autowired
    private FacetRepository facetRepository;

    // 中文网站爬虫
    public Result TopicFacetTreeSpider(String subjectName, String domainName, Boolean isChineseOrNot) throws Exception {
        Domain domain = domainRepository.findByDomainName(domainName);
        List<Topic> topics = topicRepository.findByDomainName(domainName);
        List<Facet> facets = facetRepository.findByDomainName(domainName);
        if (domain == null) {
            if (isChineseOrNot) {
                domainFlag = true;
            } else {
                domainFlag = false;
            }
            Log.log("==========知识森林里还没有这门课程，开始爬取课程：" + domainName + "==========");
            Result result = domainService.findOrInsetDomainByDomainName(subjectName, domainName);
            if (result.getCode() == 116) {
                return ResultUtil.error(ResultEnum.DOMAIN_INSERT_ERROR.getCode(), ResultEnum.DOMAIN_INSERT_ERROR.getMsg());
            } else if (result.getCode() == 118) {
                return ResultUtil.error(ResultEnum.DOMAIN_INSERT_ERROR_2.getCode(), ResultEnum.DOMAIN_INSERT_ERROR_2.getMsg());
            } else {
                Domain domain_new = domainRepository.findByDomainName(domainName);
                Runnable runnable = new SpiderRunnable(domain_new);
                Thread thread = new Thread(runnable);
                thread.start();
                return ResultUtil.error(ResultEnum.TSPIDER_ERROR.getCode(), ResultEnum.TSPIDER_ERROR.getMsg(), "课程 " + domainName + " 准备开始构建");
            }
        } else if (domain != null && (topics == null || topics.size() == 0) && (facets == null || facets.size() == 0)) {
            return ResultUtil.error(ResultEnum.TSPIDER_ERROR1.getCode(), ResultEnum.TSPIDER_ERROR1.getMsg(), "已经爬取的主题数目为 " + TopicCrawler.getCountTopicCrawled());
        } else if (domain != null && (topics != null && topics.size() != 0) && (facets == null || !FragmentCrawler.getisCompleted())) {
            return ResultUtil.error(ResultEnum.TSPIDER_ERROR2.getCode(), ResultEnum.TSPIDER_ERROR2.getMsg(),
                    "已经爬取的一级分面数目为 " + FragmentCrawler.getCountFacetCrawled()+ " 二、三级分面数目为 " + FragmentCrawler.getCountFacetRelationCrawled());
        } else {
            return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), "==========该课程的知识主题分面树已成功构建==========");
        }
    }


    /**
     * 爬取一门课程：主题、分面、分面关系
     *
     * @param domain 课程
     */
    public static void constructTopicFacetTreeByDomainName(Domain domain) throws Exception {
        // 爬取并存储知识主题
        TopicCrawler.storeTopic(domain);
        // 爬取并存储知识主题对应的分面，形成知识主题分面树（不含碎片信息及知识主题间的认知关系）
        FragmentCrawler.storeFacetTreeByDomainName(domain);
    }

    /**
     * 按照课程名爬取该课程的所有分面与分面关系，用于爬取完主题之后爬取分面
     * @param domainName
     * @return
     * @throws Exception
     */
    public Result FacetTreeSpider(String domainName) throws Exception{
        try {
            Domain domain = domainRepository.findByDomainName(domainName);
            FragmentCrawler.storeFacetTreeByDomainName(domain);
            Log.log("\n\n\n==========该课程的分面与分面关系已经构建完成==========");
            return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), "该课程的分面树与分面关系已成功构建");
        }catch (Exception e){
            e.printStackTrace();
        }
        return ResultUtil.error(ResultEnum.TSPIDER_ERROR3.getCode(), ResultEnum.TSPIDER_ERROR3.getMsg(), "课程 " + domainName + " 分面构建出错");
    }


    /**
     * 针对人工添加的新主题爬取对应的主题分面树并存入数据库
     * 
     */
    public Result SingleTopicFacetTreeSpider(String domainName, String topicName) {
        try {
            Domain domain = domainRepository.findByDomainName(domainName);
            topicService.insertTopicByNameAndDomainName(domainName,topicName);
            Topic topic = topicRepository.findByTopicName(topicName).get(0);
            FragmentCrawler.storeFacetTreeByTopicName(domain,topic);
            Log.log("\n\n\n==========该主题的分面与分面关系已经构建完成==========");
            return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), "该主题下分面与分面关系已构建完毕");
        }catch (Exception e){
            e.printStackTrace();
        }
        return ResultUtil.error(ResultEnum.TSPIDER_ERROR3.getCode(), ResultEnum.TSPIDER_ERROR3.getMsg(), "主题 " + topicName + " 分面构建出错");
    }
}
