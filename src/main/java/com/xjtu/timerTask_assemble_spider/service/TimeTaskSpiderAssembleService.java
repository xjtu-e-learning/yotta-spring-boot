package com.xjtu.timerTask_assemble_spider.service;

import com.xjtu.assemble.domain.Assemble;
import com.xjtu.assemble.repository.AssembleRepository;
import com.xjtu.common.domain.Result;
import com.xjtu.common.domain.ResultEnum;
import com.xjtu.domain.domain.Domain;
import com.xjtu.domain.repository.DomainRepository;
import com.xjtu.facet.domain.Facet;
import com.xjtu.facet.repository.FacetRepository;
import com.xjtu.question.domain.Question;
import com.xjtu.question.repository.QuestionRepository;
import com.xjtu.source.domain.Source;
import com.xjtu.source.repository.SourceRepository;
import com.xjtu.subject.domain.Subject;
import com.xjtu.subject.repository.SubjectRepository;
import com.xjtu.timerTask_assemble_spider.spiders.baiduzhidao.BaiduZhidaoProcessor;
import com.xjtu.timerTask_assemble_spider.spiders.csdn.CSDNProcessor;
import com.xjtu.timerTask_assemble_spider.spiders.toutiao.ToutiaoProcessor;
import com.xjtu.timerTask_assemble_spider.spiders.zhihu.ZhihuProcessor;
import com.xjtu.topic.domain.Topic;
import com.xjtu.topic.repository.TopicRepository;
import com.xjtu.utils.ResultUtil;
import java.util.concurrent.TimeUnit;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import us.codecraft.webmagic.Spider;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: makexin
 * @Date: 2019/9/1919:33
 */

@Service
public class TimeTaskSpiderAssembleService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    SourceRepository sourceRepository;

    @Autowired
    SubjectRepository subjectRepository;

    @Autowired
    DomainRepository domainRepository;

    @Autowired
    TopicRepository topicRepository;

    @Autowired
    FacetRepository facetRepository;

    @Autowired
    AssembleRepository assembleRepository;

    @Autowired
    QuestionRepository questionRepository;


    @Scheduled(cron = "0 0 1 9 * ?")//每个月9号1点启动
    public void crawlAssembles() {

        for (Long subjectId = 1L; subjectId<=15; subjectId++)
        {
            List<Domain> domains = domainRepository.findBySubjectId(subjectId);
            if (domains == null)
                return;
            for (Domain domain : domains)
            {
                String domainName = domain.getDomainName();
                startSpider(domainName);
                try {
                    java.util.concurrent.TimeUnit.HOURS.sleep(2L);
                    //每开始爬取一门课程碎片，休眠2小时。避免同时开启太多。
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public void startSpider(String domainName)
    {
        logger.info("爬取碎片开始，课程：" + domainName);

        logger.info("百度知道碎片开始爬取 当前课程：" + domainName);
        BaiduZhidaoProcessor baiduZhidaoProcessor = new BaiduZhidaoProcessor(this);
        baiduZhidaoProcessor.baiduAnswerCrawl(domainName, assembleRepository);

        logger.info("CSDN碎片开始爬取 当前课程：" + domainName);
        CSDNProcessor csdnProcessor = new CSDNProcessor(this);
        csdnProcessor.CSDNAnswerCrawl(domainName, assembleRepository);


        logger.info("知乎碎片开始爬取 当前课程：" + domainName);
        ZhihuProcessor zhihuProcessor = new ZhihuProcessor(this);
        zhihuProcessor.zhihuAnswerCrawl(domainName, assembleRepository);


        logger.info("今日头条碎片开始爬取 当前课程：" + domainName);
        ToutiaoProcessor toutiaoProcessor = new ToutiaoProcessor(this);
        toutiaoProcessor.toutiaoAnswerCrawl(domainName, assembleRepository);
    }


    /**
     * 读取本地excel文件，获取课程和对应的学科信息
     *
     * @param excelPath 课程excel文件路径
     * @return 课程信息集合
     */
    public List<Domain> getDomainFromExcel(String excelPath) {
        List<Domain> domains = new ArrayList<>();
        try {
            Workbook wb = Workbook.getWorkbook(new File(excelPath));
            Sheet st = wb.getSheet(0);
            int rows = st.getRows();
            logger.error("rows:" + rows);
            for (int i = 1; i < rows; i++) {
                String subjectName = st.getCell(0, i).getContents();
                String domainName = st.getCell(1, i).getContents();
                logger.error(subjectName + " " + domainName);
                Domain domain = new Domain();
                domain.setDomainName(domainName);
                Subject subject = subjectRepository.findBySubjectName(subjectName);
                logger.error(subject.toString());
                domain.setSubjectId(subject.getSubjectId());
                domains.add(domain);
            }
        } catch (BiffException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return domains;
    }

    //返回数据包含课程名，主题名，分面名
    public List<Map<String, Object>> getFacets(String domainName) {
        Domain domain = domainRepository.findByDomainName(domainName);
        List<Topic> topics = topicRepository.findByDomainId(domain.getDomainId());
        List<Map<String, Object>> facetContainTopicAndDomains = new ArrayList<>();
        for (Topic topic : topics) {
            List<Facet> facets = facetRepository.findByTopicId(topic.getTopicId());
            for (Facet facet : facets) {
                Map<String, Object> facetContainTopicAndDomain = new HashMap<>(3);
                facetContainTopicAndDomain.put("facetName", facet.getFacetName());
                facetContainTopicAndDomain.put("topicName", topic.getTopicName());
                facetContainTopicAndDomain.put("domainName", domain.getDomainName());
                facetContainTopicAndDomains.add(facetContainTopicAndDomain);
            }
        }
        return facetContainTopicAndDomains;
    }

    /**
     * 查询对应分面的分面Id和数据源Id
     *
     * @param facetMap
     * @return
     */
    public Map<String, Object> getFacet(Map<String, Object> facetMap) {
        String sourceName = (String) facetMap.get("sourceName");
        Source source = sourceRepository.findBySourceName(sourceName);
        if (source == null) {
            logger.error("数据源不存在：" + sourceName);
        }
        Long sourceId = source.getSourceId();
        String domainName = (String) facetMap.get("domainName");
        String topicName = (String) facetMap.get("topicName");
        String facetName = (String) facetMap.get("facetName");
        Domain domain = domainRepository.findByDomainName(domainName);
        Topic topic = topicRepository.findByDomainIdAndTopicName(domain.getDomainId(), topicName);
        Facet facet = facetRepository.findByTopicIdAndFacetName(topic.getTopicId(), facetName);
        facetMap = new HashMap<>(5);
        facetMap.put("domainName", domainName);
        facetMap.put("topicName", topicName);
        facetMap.put("facetName", facetName);
        facetMap.put("domainId", domain.getDomainId());
        facetMap.put("sourceId", sourceId);
        facetMap.put("facetId", facet.getFacetId());
        return facetMap;
    }

    /**
     * 根据数据源和课程名，查询问题碎片
     *
     * @param sourceName
     * @param domainName
     * @return
     */
    public List<Question> getQuestions(String sourceName, String domainName) {
        List<Question> questionAssembles = questionRepository.findBySourceNameAndDomainName(sourceName, domainName);
        return questionAssembles;
    }

    public Long findMaxAssembleId() {
        return assembleRepository.findMaxId();
    }

    public void updateQuestionByQuestionId(String askerName
            , String askerReputation
            , String askerAnswerCount
            , String askerQuestionCount
            , String askerViewCount
            , Long questionId) {
        questionRepository.updateByQuestionId(askerName, askerReputation
                , askerAnswerCount, askerQuestionCount
                , askerViewCount, questionId);
    }

    public void saveAssembles(List<Assemble> assembles) {
        assembleRepository.save(assembles);
    }

    public void insertQuestion(Question question) {
        questionRepository.save(question);
    }
}

