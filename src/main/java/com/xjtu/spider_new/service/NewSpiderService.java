package com.xjtu.spider_new.service;

import com.xjtu.assemble.domain.Assemble;
import com.xjtu.assemble.repository.AssembleRepository;
import com.xjtu.common.domain.Result;
import com.xjtu.common.domain.ResultEnum;
import com.xjtu.domain.domain.Domain;
import com.xjtu.domain.repository.DomainRepository;
import com.xjtu.domain.service.DomainService;
import com.xjtu.facet.domain.Facet;
import com.xjtu.facet.repository.FacetRepository;
import com.xjtu.spider_new.common.NewSpiderRunnable;
import com.xjtu.spider_new.common.ProgressResult;
import com.xjtu.spider_new.spiders.wikicn.AssembleCrawler;
import com.xjtu.spider_new.spiders.wikicn.FragmentCrawler;
import com.xjtu.spider_new.spiders.wikicn.TopicCrawler;
import com.xjtu.topic.domain.Topic;
import com.xjtu.topic.repository.TopicRepository;
import com.xjtu.topic.service.TopicService;
import com.xjtu.utils.Log;
import com.xjtu.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 2021使用的爬虫
 *
 * @author 洪振杰
 * @date 2021年3月
 */
@Service
public class NewSpiderService {

    private static Boolean isChinese;

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

    @Autowired
    private AssembleRepository assembleRepository;

    /**
     * 主题-分面-碎片爬虫方法
     *
     * @param subjectName 学科名
     * @param domainName 课程名
     * @param isChineseOrNot 是否为中文课程
     * @return
     */
    public Result TopicFacetTreeSpider(String subjectName, String domainName, Boolean isChineseOrNot) throws Exception {

        Domain domain = domainRepository.findByDomainName(domainName);
        List<Topic> topics = topicRepository.findByDomainName(domainName);
        List<Facet> facets = facetRepository.findByDomainName(domainName);
        List<Assemble> assembles = assembleRepository.findByDomainName(domainName);
        if (domain == null) {
            if (isChineseOrNot) {
                isChinese = true;
            } else {
                isChinese = false;
            }
            Log.log("==========知识森林里还没有这门课程，开始爬取课程：" + domainName + "==========");
            Result result = domainService.findOrInsetDomainByDomainName(subjectName, domainName);
            if (result.getCode() == 116) {
                // 课程信息插入失败：课程名不存在或者为空
                return ResultUtil.error(ResultEnum.DOMAIN_INSERT_ERROR.getCode(), ResultEnum.DOMAIN_INSERT_ERROR.getMsg());
            } else if (result.getCode() == 118) {
                // 课程信息插入失败：数据库插入语句失败
                return ResultUtil.error(ResultEnum.DOMAIN_INSERT_ERROR_2.getCode(), ResultEnum.DOMAIN_INSERT_ERROR_2.getMsg());
            } else {
                Domain domain_new = domainRepository.findByDomainName(domainName);
                Runnable runnable = new NewSpiderRunnable(domain_new);
                Thread thread = new Thread(runnable);
                thread.start();
                return ResultUtil.error(ResultEnum.TSPIDER_ERROR.getCode(), ResultEnum.TSPIDER_ERROR.getMsg(), "课程 " + domainName + " 准备开始构建");
            }
        } else if (domain != null && (topics == null || topics.size() == 0) && (facets == null || facets.size() == 0)) {
            return ResultUtil.error(ResultEnum.TSPIDER_ERROR1.getCode(), ResultEnum.TSPIDER_ERROR1.getMsg(),
//                    "已经爬取的主题数目为 " + TopicCrawler.getCountTopicCrawled()
                    new ProgressResult(TopicCrawler.getCountTopicCrawled(), 0));
        } else if (domain != null && (topics != null && topics.size() != 0) && (facets == null || !FragmentCrawler.getisCompleted())) {
            return ResultUtil.error(ResultEnum.TSPIDER_ERROR2.getCode(), ResultEnum.TSPIDER_ERROR2.getMsg(),
//                    "已经爬取的主题数目为 " + topicRepository.findByDomainName(domainName).size() +
//                    " 已经爬取的一级分面数目为 " + FragmentCrawler.getCountFacetCrawled() + " 二、三级分面数目为 " + FragmentCrawler.getCountFacetRelationCrawled()
                    new ProgressResult(topicRepository.findByDomainName(domainName).size(),
                    FragmentCrawler.getCountFacetCrawled() + FragmentCrawler.getCountFacetRelationCrawled(),
                            FragmentCrawler.getProgress(domain.getDomainId()) / 2));
        } else if ((domain != null)
                && (topics != null && topics.size() != 0)
                && (facets != null && facets.size() != 0)
                && (assembles == null || !AssembleCrawler.isCompleted())) {
            return ResultUtil.error(ResultEnum.TSPIDER_ERROR5.getCode(), ResultEnum.TSPIDER_ERROR5.getMsg(),
//                    "已经爬取的主题数目为 " + topicRepository.findByDomainName(domainName).size() +
//                    " 已经爬取的分面数目为 " + facetRepository.findByDomainName(domainName).size() +
//                    "已经爬取的碎片数目为 " + AssembleCrawler.getAssembleCount() + ", 进度为" + AssembleCrawler.getProgress()
                    new ProgressResult(topicRepository.findByDomainName(domainName).size(),
                    facetRepository.findByDomainName(domainName).size(),
                    AssembleCrawler.getAssembleCount(),
                    AssembleCrawler.getProgress(domain.getDomainId()) / 2 + 0.5));
        } else {
            return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(),
                    "==========该课程的知识主题分面树已成功构建，且碎片爬取完毕==========");
        }


    }

    /**
     * 爬取一门课程：主题、分面、分面关系、碎片
     *
     * @param domain 课程
     */
    public static void constructTopicFacetTreeByDomainName(Domain domain) throws Exception {
        // 爬取并存储知识主题
        TopicCrawler.storeTopic(domain);
        // 爬取并存储知识主题对应的分面，形成知识主题分面树（不含碎片信息及知识主题间的认知关系）
        FragmentCrawler.storeFacetTreeByDomainName(domain);
        // 爬取上面爬到的所有分面下的所有碎片
        AssembleCrawler.storeAssembleByDomainName(domain);

    }

    public static Boolean getIsChinese() {
        return isChinese;
    }

    public static void setIsChinese(Boolean isChinese) {
        NewSpiderService.isChinese = isChinese;
    }
}
