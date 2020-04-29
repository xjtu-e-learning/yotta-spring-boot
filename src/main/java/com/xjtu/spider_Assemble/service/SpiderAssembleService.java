package com.xjtu.spider_Assemble.service;

import com.xjtu.assemble.domain.Assemble;
import com.xjtu.assemble.repository.AssembleRepository;
import com.xjtu.assemble.service.AssembleService;
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
import com.xjtu.spider_Assemble.spiders.baiduzhidao.BaiduZhidaoProcessor;
import com.xjtu.spider_Assemble.spiders.csdn.CSDNProcessor;
import com.xjtu.spider_Assemble.spiders.toutiao.ToutiaoProcessor;
import com.xjtu.spider_Assemble.spiders.zhihu.ZhihuProcessor;
import com.xjtu.subject.domain.Subject;
import com.xjtu.subject.repository.SubjectRepository;
import com.xjtu.topic.domain.Topic;
import com.xjtu.topic.repository.TopicRepository;
import com.xjtu.utils.ResultUtil;
import com.xjtu.spider_Assemble.spiders.webmagic.spider.myMonitor;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import us.codecraft.webmagic.Spider;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * 根据课程名自动爬取碎片，
 * @Author: makexin
 */

@Service
public class SpiderAssembleService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static int total_left = 0;
    private static int all_assemble = 0;
    private static Spider last_spider;
    private static String last_domainName = null;

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


    public Result startCrawlAssembles(String domainName)
    {
        //1.获取分面信息
        List<Map<String, Object>> facets = getFacets(domainName);
        if (facets == null || facets.size() == 0) {
            return ResultUtil.success(ResultEnum.Assemble_GENERATE_ERROR.getCode(), ResultEnum.Assemble_GENERATE_ERROR.getMsg(), "碎片构建失败： 无分面信息");
        }

        Domain domain = domainRepository.findByDomainName(domainName);
        if (domain == null) {
            logger.error("碎片构建失败： 课程不存在");
            return ResultUtil.error(ResultEnum.Assemble_GENERATE_ERROR_1.getCode(), ResultEnum.Assemble_GENERATE_ERROR_1.getMsg());
        }
        Long domain_id = domain.getDomainId();
        Integer assemble_number = assembleRepository.countByDomainId(domain_id);

        if (assemble_number == 0)//新课程，需构建碎片
        {
            if (last_domainName == null || total_left == 0)
            {
                last_spider = crawlAssembles(domainName);
                last_domainName = domainName;
                return ResultUtil.success(ResultEnum.Assemble_GENERATE_ERROR_2.getCode(), ResultEnum.Assemble_GENERATE_ERROR_2.getMsg(), "开始构建碎片");
            }
            else
            {
                myMonitor monitor = new myMonitor();
                int last_leftCount = monitor.monitor(last_spider);
                total_left = last_leftCount;
                if (total_left > all_assemble)
                    all_assemble = total_left;
                if(last_leftCount == 0)
                {
                    last_spider = crawlAssembles(domainName);
                    last_domainName = domainName;
                    return ResultUtil.success(ResultEnum.Assemble_GENERATE_ERROR_2.getCode(), ResultEnum.Assemble_GENERATE_ERROR_2.getMsg(), "开始构建碎片");
                }
                else
                {
                    if (last_domainName.equals(domainName))
                        return ResultUtil.success(ResultEnum.Assemble_GENERATE_ERROR_3.getCode(), ResultEnum.Assemble_GENERATE_ERROR_3.getMsg(), "正在构建碎片");
                    else
                        return ResultUtil.success(ResultEnum.Assemble_GENERATE_ERROR_4.getCode(), ResultEnum.Assemble_GENERATE_ERROR_4.getMsg(), "上个构建碎片任务尚未完成");
                }

            }
        }
        else//该课程已有碎片
        {
            if (last_domainName == null)
            {
                return ResultUtil.success(ResultEnum.Assemble_GENERATE_ERROR_5.getCode(), ResultEnum.Assemble_GENERATE_ERROR_5.getMsg(), "该课程已有课程碎片");
            }
            else if (last_domainName.equals(domainName))//查询相同主题碎片爬取状态
            {
                if (total_left == 0)
                {
                    return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), "碎片构建完成");
                }
                else
                {
                    myMonitor monitor = new myMonitor();
                    int last_leftCount = monitor.monitor(last_spider);
                    total_left = last_leftCount;
                    if (total_left > all_assemble)
                        all_assemble = total_left;
                    if(last_leftCount == 0)
                        return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), "碎片构建完成");
                    else
                        return ResultUtil.success(ResultEnum.Assemble_GENERATE_ERROR_3.getCode(), ResultEnum.Assemble_GENERATE_ERROR_3.getMsg(), "正在构建碎片");
                }
            }
            else // last_domainName != domainName 查询不同主题碎片，且碎片数量不为零
            {
                if (total_left == 0)
                {
                    return ResultUtil.success(ResultEnum.Assemble_GENERATE_ERROR_5.getCode(), ResultEnum.Assemble_GENERATE_ERROR_5.getMsg(), "该课程已有课程碎片");
                }
                else
                {
                    return ResultUtil.success(ResultEnum.Assemble_GENERATE_ERROR_4.getCode(), ResultEnum.Assemble_GENERATE_ERROR_4.getMsg(), "上个构建碎片任务尚未完成");
                }
            }
        }
    }

    public Spider crawlAssembles(String domainName) {


        logger.info("爬取碎片开始，课程：" + domainName);

        int max_leftCount;
        Spider max_spider;

        logger.info("百度知道碎片开始爬取 当前课程：" + domainName);
        BaiduZhidaoProcessor baiduZhidaoProcessor = new BaiduZhidaoProcessor(this);
        Spider baiduZhidaoSpider = baiduZhidaoProcessor.baiduAnswerCrawl(domainName);
        myMonitor baiduZhidaoMonitor = new myMonitor();
        baiduZhidaoMonitor.register(baiduZhidaoSpider);
        int baiduZhidao_leftCount = baiduZhidaoMonitor.monitor(baiduZhidaoSpider);
        max_leftCount = baiduZhidao_leftCount;
        max_spider = baiduZhidaoSpider;

        logger.info("CSDN碎片开始爬取 当前课程：" + domainName);
        CSDNProcessor csdnProcessor = new CSDNProcessor(this);
        Spider csdnSpider = csdnProcessor.CSDNAnswerCrawl(domainName);
        myMonitor csdnMonitor = new myMonitor();
        csdnMonitor.register(csdnSpider);
        int csdn_leftCount = csdnMonitor.monitor(csdnSpider);
        if (max_leftCount < csdn_leftCount)
        {
            max_leftCount = csdn_leftCount;
            max_spider = csdnSpider;
        }


//        logger.info("stack overflow 提问者碎片开始爬取 当前课程：" + domainName);
//        StackoverflowAskerProcessor stackoverflowAskerProcessor = new StackoverflowAskerProcessor(this);
//        stackoverflowAskerProcessor.StackoverflowCrawl(domainName);
//        logger.info("stack overflow 提问者碎片爬取完成");
//
//        logger.info("stack overflow 问题碎片开始爬取 当前课程：" + domainName);
//        StackoverflowQuestionProcessor stackoverflowQuestionProcessor = new StackoverflowQuestionProcessor(this);
//        stackoverflowQuestionProcessor.StackoverflowCrawl(domainName);
//        logger.info("stack overflow 问题碎片爬取完成");
//
//        logger.info("Yahoo 提问者碎片开始爬取 当前课程：" + domainName);
//        YahooAskerProcessor yahooAskerProcessor = new YahooAskerProcessor(this);
//        yahooAskerProcessor.YahooCrawl(domainName);
//        logger.info("Yahoo 提问者碎片爬取完成");
//
//        logger.info("Yahoo 碎片开始爬取 当前课程：" + domainName);
//        YahooProcessor yahooProcessor = new YahooProcessor(this);
//        yahooProcessor.YahooCrawl(domainName);
//        logger.info("Yahoo 碎片爬取完成");
//
        logger.info("知乎碎片开始爬取 当前课程：" + domainName);
        ZhihuProcessor zhihuProcessor = new ZhihuProcessor(this);
        Spider zhihuSpider = zhihuProcessor.zhihuAnswerCrawl(domainName);
        myMonitor zhihuMonitor = new myMonitor();
        zhihuMonitor.register(zhihuSpider);
        int zhihu_leftCount = zhihuMonitor.monitor(zhihuSpider);
        if (max_leftCount < zhihu_leftCount)
        {
            max_leftCount = zhihu_leftCount;
            max_spider = zhihuSpider;
        }
//        System.out.println("left page: "+ zhihu_leftCount);

//        logger.info("知乎碎片爬取完成");


        logger.info("今日头条碎片开始爬取 当前课程：" + domainName);
        ToutiaoProcessor toutiaoProcessor = new ToutiaoProcessor(this);
        Spider toutiaoSpider = toutiaoProcessor.toutiaoAnswerCrawl(domainName);
        myMonitor toutiaoMonitor = new myMonitor();
        toutiaoMonitor.register(toutiaoSpider);
        int toutiao_leftCount = toutiaoMonitor.monitor(toutiaoSpider);
        if (max_leftCount < toutiao_leftCount)
        {
            max_leftCount = toutiao_leftCount;
            max_spider = toutiaoSpider;
        }

        total_left = baiduZhidao_leftCount + csdn_leftCount + toutiao_leftCount + zhihu_leftCount;
        all_assemble = total_left;

        return max_spider;
    }

    public Result countAssembles(String domainName)
    {
        Domain domain = domainRepository.findByDomainName(domainName);
        if (domain == null) {
            logger.error("碎片查询失败：对应课程不存在");
            return ResultUtil.error(ResultEnum.Assemble_SEARCH_ERROR.getCode(), ResultEnum.Assemble_SEARCH_ERROR.getMsg(), "碎片查询失败：对应课程不存在");
        }
        Long domain_id = domain.getDomainId();
        Integer assemble_number = assembleRepository.countByDomainId(domain_id);
        Map<String, Integer> assembleMap = new HashMap<>();
        assembleMap.put("exist", assemble_number);
        assembleMap.put("allNumber", all_assemble);
        return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), assembleMap);
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

