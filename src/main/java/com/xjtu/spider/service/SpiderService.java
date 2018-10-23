package com.xjtu.spider.service;

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
import com.xjtu.spider.controller.SpiderController;
import com.xjtu.spider.spiders.toutiao.ToutiaoProcessor;
import com.xjtu.subject.domain.Subject;
import com.xjtu.subject.repository.SubjectRepository;
import com.xjtu.topic.domain.Topic;
import com.xjtu.topic.repository.TopicRepository;
import com.xjtu.utils.ResultUtil;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yangkuan
 * @date 2018/5/23
 */
@Service
public class SpiderService {

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

    public Result crawlAssembles() {

        // 爬取多门课程
        String excelPath = SpiderController.class.getClassLoader().getResource("").getPath() + "domains.xls";
        //String excelPath = "";
        List<Domain> domains = getDomainFromExcel(excelPath);
        for (Domain domain : domains) {
            String domainName = domain.getDomainName();
            logger.info("碎片开始爬取课程：" + domainName);

            /*logger.info("百度知道碎片开始爬取 当前课程：" + domainName);
            BaiduZhidaoProcessor baiduZhidaoProcessor = new BaiduZhidaoProcessor(this);
            baiduZhidaoProcessor.baiduAnswerCrawl(domainName);
            logger.info("百度知道碎片爬取完成");

            logger.info("CSDN碎片开始爬取 当前课程：" + domainName);
            CSDNProcessor csdnProcessor = new CSDNProcessor(this);
            csdnProcessor.CSDNAnswerCrawl(domainName);
            logger.info("CSDN碎片爬取完成");

            logger.info("stack overflow 提问者碎片开始爬取 当前课程：" + domainName);
            StackoverflowAskerProcessor stackoverflowAskerProcessor = new StackoverflowAskerProcessor(this);
            stackoverflowAskerProcessor.StackoverflowCrawl(domainName);
            logger.info("stack overflow 提问者碎片爬取完成");

            logger.info("stack overflow 问题碎片开始爬取 当前课程：" + domainName);
            StackoverflowQuestionProcessor stackoverflowQuestionProcessor = new StackoverflowQuestionProcessor(this);
            stackoverflowQuestionProcessor.StackoverflowCrawl(domainName);
            logger.info("stack overflow 问题碎片爬取完成");

            logger.info("Yahoo 提问者碎片开始爬取 当前课程：" + domainName);
            YahooAskerProcessor yahooAskerProcessor = new YahooAskerProcessor(this);
            yahooAskerProcessor.YahooCrawl(domainName);
            logger.info("Yahoo 提问者碎片爬取完成");

            logger.info("Yahoo 碎片开始爬取 当前课程：" + domainName);
            YahooProcessor yahooProcessor = new YahooProcessor(this);
            yahooProcessor.YahooCrawl(domainName);
            logger.info("Yahoo 碎片爬取完成");

            logger.info("知乎碎片开始爬取 当前课程：" + domainName);
            ZhihuProcessor zhihuProcessor = new ZhihuProcessor(this);
            zhihuProcessor.zhihuAnswerCrawl(domainName);
            logger.info("知乎碎片爬取完成");*/

            logger.info("今日头条碎片开始爬取 当前课程：" + domainName);
            ToutiaoProcessor toutiaoProcessor = new ToutiaoProcessor(this);
            toutiaoProcessor.toutiaoAnswerCrawl(domainName);
            logger.info("今日头条碎片爬取完成");
        }
        return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), "爬取碎片完成");
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
