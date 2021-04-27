package com.xjtu.spider_dynamic_output.service;

import com.xjtu.assemble.domain.Assemble;
import com.xjtu.assemble.domain.AssembleContainType;
import com.xjtu.assemble.repository.AssembleRepository;
import com.xjtu.common.domain.Result;
import com.xjtu.common.domain.ResultEnum;
import com.xjtu.dependency.repository.DependencyRepository;
import com.xjtu.domain.domain.Domain;
import com.xjtu.domain.repository.DomainRepository;
import com.xjtu.domain.service.DomainService;
import com.xjtu.facet.domain.Facet;
import com.xjtu.facet.domain.FacetContainAssemble;
import com.xjtu.facet.repository.FacetRepository;
import com.xjtu.relation.repository.RelationRepository;
import com.xjtu.source.repository.SourceRepository;
import com.xjtu.spider_dynamic_output.spiders.FacetAssembleCrawler;
import com.xjtu.spider_dynamic_output.spiders.csdn.CsdnThread;
import com.xjtu.spider_dynamic_output.spiders.wikicn.AssembleCrawler;
import com.xjtu.spider_dynamic_output.spiders.wikicn.FacetCrawler;
import com.xjtu.spider_dynamic_output.spiders.wikicn.TopicOnlyCrawler;
import com.xjtu.spider_topic.service.TFSpiderService;
import com.xjtu.topic.dao.TopicDAO;
import com.xjtu.topic.domain.Topic;
import com.xjtu.topic.domain.TopicContainFacet;
import com.xjtu.topic.repository.TopicRepository;
import com.xjtu.utils.*;
import org.jetbrains.annotations.TestOnly;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.*;

import static java.lang.Thread.State.RUNNABLE;
import static java.lang.Thread.State.TERMINATED;

@Service
public class SpiderDynamicOutputService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private DomainRepository domainRepository;

    @Autowired
    private FacetRepository facetRepository;

    @Autowired
    private AssembleRepository assembleRepository;

    @Autowired
    private DomainService domainService;

    @Autowired
    private RelationRepository relationRepository;

    @Autowired
    private SourceRepository sourceRepository;

    @Autowired
    private DependencyRepository dependencyRepository;

    @Autowired
    private TopicDAO topicDAO;

    @Value("${server.port}")
    private Integer port;



    private static Boolean domainFlag;

    //获取课程的中英文状态
    public static boolean getDomainFlag() {
        return domainFlag;
    }

    HashMap<String,Thread> threadMap=new HashMap<>();//保存正常爬虫状态
    HashMap<String,Thread> inThreadMap=new HashMap<>();//保存增量爬虫状态




    /**
     * 指定课程名和主题名，爬取主题并包含其完整的下的分面、碎片数据
     *
     * @param domainName
     * @param topicName
     * @return
     */
    public Result startFacetAssembleSpider(String domainName, String topicName){
        Domain domain = domainRepository.findByDomainName(domainName);
        if (domain == null) {
            logger.error("课程查询失败：没有指定课程");
            return ResultUtil.error(ResultEnum.TOPIC_SEARCH_ERROR_2.getCode(), ResultEnum.TOPIC_SEARCH_ERROR_2.getMsg());
        }
        Topic topic = topicRepository.findByDomainIdAndTopicName(domain.getDomainId(), topicName);
        if (topic == null) {
            logger.error("主题查询失败：没有指定主题");
            return ResultUtil.error(ResultEnum.TOPIC_SEARCH_ERROR.getCode(), ResultEnum.TOPIC_SEARCH_ERROR.getMsg());

        }
        if(threadMap.containsKey(topicName)==true){
            return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(),"主题："+topicName+"  爬虫已启动,无需重复启动");
        }
        Thread thread=new facetAssembleSpiderThread(domain,topic);
        thread.start();
        threadMap.put(topicName,thread);
        return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(),"主题："+topicName+"  爬虫已启动");

    }

    public Result getFacetAssemble(String domainName, String topicName){
        Domain domain = domainRepository.findByDomainName(domainName);
        if (domain == null) {
            logger.error("课程查询失败：没有指定课程");
            return ResultUtil.error(ResultEnum.TOPIC_SEARCH_ERROR_2.getCode(), ResultEnum.TOPIC_SEARCH_ERROR_2.getMsg());
        }
        Topic topic = topicRepository.findByDomainIdAndTopicName(domain.getDomainId(), topicName);
        if (topic == null) {
            logger.error("主题查询失败：没有指定主题");
            return ResultUtil.error(ResultEnum.TOPIC_SEARCH_ERROR.getCode(), ResultEnum.TOPIC_SEARCH_ERROR.getMsg());

        }

        TopicContainFacet topicContainFacet = getTopicContainFacet(topic);

        if(threadMap.containsKey(topicName)==false){
            return ResultUtil.error(ResultEnum.OUTPUTSPIDER_ERROR_1.getCode(), ResultEnum.OUTPUTSPIDER_ERROR_1.getMsg(), topicContainFacet);
        }
        System.out.println("hashmap中的线程状态:"+threadMap.get(topicName).getState());

        if(threadMap.get(topicName).getState()==TERMINATED){
            threadMap.remove(topicName);
            return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), topicContainFacet);
        }else {
            return ResultUtil.error(ResultEnum.OUTPUTSPIDER_ERROR_2.getCode(), ResultEnum.OUTPUTSPIDER_ERROR_2.getMsg(), topicContainFacet);
        }



    }

    public Result startIncrementFacetAssembleSpider(String domainName, String topicName){
        Domain domain = domainRepository.findByDomainName(domainName);
        if (domain == null) {
            logger.error("课程查询失败：没有指定课程");
            return ResultUtil.error(ResultEnum.TOPIC_SEARCH_ERROR_2.getCode(), ResultEnum.TOPIC_SEARCH_ERROR_2.getMsg());
        }
        Topic topic = topicRepository.findByDomainIdAndTopicName(domain.getDomainId(), topicName);
        if (topic == null) {
            logger.error("主题查询失败：没有指定主题");
            return ResultUtil.error(ResultEnum.TOPIC_SEARCH_ERROR.getCode(), ResultEnum.TOPIC_SEARCH_ERROR.getMsg());

        }
        if(inThreadMap.containsKey(topicName)==true){
            return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(),"主题："+topicName+"  增量爬虫已启动,无需重复启动");
        }
        Thread thread=new incrementFacetAssembleSpiderThread(domain,topic);
        thread.start();
        inThreadMap.put(topicName,thread);
        return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(),"主题："+topicName+"  增量爬虫已启动");

    }

    public Result getIncrementFacetAssemble(String domainName, String topicName){
        Domain domain = domainRepository.findByDomainName(domainName);
        if (domain == null) {
            logger.error("课程查询失败：没有指定课程");
            return ResultUtil.error(ResultEnum.TOPIC_SEARCH_ERROR_2.getCode(), ResultEnum.TOPIC_SEARCH_ERROR_2.getMsg());
        }
        Topic topic = topicRepository.findByDomainIdAndTopicName(domain.getDomainId(), topicName);
        if (topic == null) {
            logger.error("主题查询失败：没有指定主题");
            return ResultUtil.error(ResultEnum.TOPIC_SEARCH_ERROR.getCode(), ResultEnum.TOPIC_SEARCH_ERROR.getMsg());

        }

        TopicContainFacet topicContainFacet = getTopicContainFacet(topic);

        if(inThreadMap.containsKey(topicName)==false){
            return ResultUtil.error(ResultEnum.OUTPUTSPIDER_ERROR_1.getCode(), ResultEnum.OUTPUTSPIDER_ERROR_1.getMsg(), topicContainFacet);
        }
        System.out.println("hashmap中的线程状态: "+inThreadMap.get(topicName).getState());

        if(inThreadMap.get(topicName).getState()==TERMINATED){
            inThreadMap.remove(topicName);
            return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), topicContainFacet);
        }else {
            return ResultUtil.error(ResultEnum.OUTPUTSPIDER_ERROR_2.getCode(), ResultEnum.OUTPUTSPIDER_ERROR_2.getMsg(), topicContainFacet);
        }



    }




    /**
     * 指定学科名和课程名，爬取该课程下的主题并动态返回
     *
     * @param domainName
     * @param subjectName
     * @return
     */

    public Result TopicSpider(String subjectName, String domainName) throws Exception {
        Boolean isChinese = true;
        Domain domain = domainRepository.findByDomainName(domainName);
        List<Topic> topiclist = topicRepository.findByDomainName(domainName);
        if (isChinese) {
            TFSpiderService.setDomainFlag(isChinese);
            domainFlag = true;
        } else {
            TFSpiderService.setDomainFlag(isChinese);
            domainFlag = false;
        }
        if (domain == null) {
            Log.log("==========知识森林里还没有这门课程，开始爬取课程：" + domainName + "==========");
            Result resultDomainInset = domainService.findOrInsetDomainByDomainName(subjectName, domainName);
            if (resultDomainInset.getCode() == 116) {
                return ResultUtil.error(ResultEnum.DOMAIN_INSERT_ERROR.getCode(), ResultEnum.DOMAIN_INSERT_ERROR.getMsg());
            } else if (resultDomainInset.getCode() == 118) {
                return ResultUtil.error(ResultEnum.DOMAIN_INSERT_ERROR_2.getCode(), ResultEnum.DOMAIN_INSERT_ERROR_2.getMsg());
            }
        }
        if (topiclist.isEmpty()) {
            Domain domain_new = domainRepository.findByDomainName(domainName);
            TopicOnlyCrawler.storeTopic(domain_new);
        }
        Domain domain_new1 = domainRepository.findByDomainName(domainName);
        Long domainId = domain_new1.getDomainId();
        List<Topic> topics = topicRepository.findByDomainId(domainId);
        Map<Long, Integer> assembleCounts = topicDAO.countAssemblesByDomainIdGroupByTopicId(domainId);
        Map<Long, Integer> inDegreeCounts = topicDAO.countInDegreeByTopicId(domainId);
        Map<Long, Integer> outDegreeCounts = topicDAO.countOutDegreeByTopicId(domainId);
        List<Map<String, Object>> results = new ArrayList<>();
        for (Topic topic : topics) {
            Map<String, Object> result = new HashMap<>();
            result.put("topicId", topic.getTopicId());
            result.put("topicName", topic.getTopicName());
            result.put("topicUrl", topic.getTopicUrl());
            result.put("topicLayer", topic.getTopicLayer());
            result.put("domainId", topic.getDomainId());
            result.put("assembleNumber", assembleCounts.get(topic.getTopicId()));
            result.put("inDegreeNumber", inDegreeCounts.get(topic.getTopicId()) == null ? 0 : inDegreeCounts.get(topic.getTopicId()));
            result.put("outDegreeNumber", outDegreeCounts.get(topic.getTopicId()) == null ? 0 : outDegreeCounts.get(topic.getTopicId()));
            results.add(result);
        }
        return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), results);
    }


    /**
     *分面碎片爬取线程
     * */

    public class facetAssembleSpiderThread extends Thread{
        public Domain domain;
        public Topic topic;


        public facetAssembleSpiderThread(Domain domain,Topic topic){
            this.domain=domain;
            this.topic=topic;
        }

        @Override
        public void run(){
            try {
                FacetCrawler.storeFacetByTopicName(domain,topic);
                AssembleCrawler.storeAssembleByTopicName(domain, topic,false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *分面碎片爬取线程
     * */

    public class incrementFacetAssembleSpiderThread extends Thread{
        public Domain domain;
        public Topic topic;


        public incrementFacetAssembleSpiderThread(Domain domain,Topic topic){
            this.domain=domain;
            this.topic=topic;
        }

        @Override
        public void run(){
            try {
                FacetCrawler.storeFacetByTopicName(domain,topic);
                AssembleCrawler.storeAssembleByTopicName(domain, topic,true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class FacetAssembleSpiderTask implements Callable<Integer> {//分面碎片爬取线程
        public Domain domain;
        public Topic topic;
        public Integer isCompleted = 0;


        public FacetAssembleSpiderTask(Domain domain, Topic topic) {
            this.domain = domain;
            this.topic = topic;
        }

        @Override
        public Integer call() throws Exception {
            FacetAssembleCrawler.storeFacetAssemble(this.domain, this.topic);
            isCompleted = 1;
            return isCompleted;
        }

    }

    /**
     *更新分面碎片线程
     * */
    public class FacetAssembleIncrementalSpiderTask implements Callable<Integer> {
        public Domain domain;
        public Topic topic;
        public Integer isCompleted = 0;


        public FacetAssembleIncrementalSpiderTask(Domain domain, Topic topic) {
            this.domain = domain;
            this.topic = topic;
        }

        @Override
        public Integer call() throws Exception {
            FacetAssembleCrawler.storeFacetAssemble(this.domain, this.topic);
            AssembleCrawler.storeAssembleIncrementalByTopicName(this.domain, this.topic);
            isCompleted = 1;
            return isCompleted;
        }

    }

    /**
     *增量爬取碎片线程
     * */
    public class AssembleIncrementalSpiderTask implements Callable<Integer> {//增量爬取碎片线程
        public Domain domain;
        public Topic topic;
        public Integer isCompleted = 0;


        public AssembleIncrementalSpiderTask(Domain domain, Topic topic) {
            this.domain = domain;
            this.topic = topic;
        }

        @Override
        public Integer call() throws Exception {
            AssembleCrawler.storeAssembleIncrementalByTopicName(this.domain, this.topic);
            isCompleted = 1;
            return isCompleted;
        }

    }

    /**
     * 根据主题名获得该主题的分面树
     * */
    public TopicContainFacet getTopicContainFacet(Topic topic) {
        Boolean hasFragment = true;
        List<Facet> firstLayerFacets = facetRepository.findByTopicIdAndFacetLayer(topic.getTopicId(), 1);
        List<Facet> secondLayerFacets = facetRepository.findByTopicIdAndFacetLayer(topic.getTopicId(), 2);
        List<Assemble> assembles = assembleRepository.findAllAssemblesByTopicId(topic.getTopicId());
        //初始化Topic
        TopicContainFacet topicContainFacet = new TopicContainFacet();
        topicContainFacet.setTopic(topic);
        topicContainFacet.setChildrenNumber(firstLayerFacets.size());

        //firstLayerFacets一级分面列表，将二级分面挂到对应一级分面下
        List<Facet> firstLayerFacetContainAssembles = new ArrayList<>();
        for (Facet firstLayerFacet : firstLayerFacets) {
            if (firstLayerFacet.getFacetName().equals("匿名分面")) {
                continue;
            }
            FacetContainAssemble firstLayerFacetContainAssemble = new FacetContainAssemble();
            firstLayerFacetContainAssemble.setFacet(firstLayerFacet);
            firstLayerFacetContainAssemble.setType("branch");
            //设置一级分面的子节点（二级分面）
            List<Object> secondLayerFacetContainAssembles = new ArrayList<>();
            for (Facet secondLayerFacet : secondLayerFacets) {
                //一级分面下的二级分面
                if (secondLayerFacet.getParentFacetId().equals(firstLayerFacet.getFacetId())) {
                    FacetContainAssemble secondLayerFacetContainAssemble = new FacetContainAssemble();
                    secondLayerFacetContainAssemble.setFacet(secondLayerFacet);
                    List<Object> assembleContainTypes = new ArrayList<>();
                    for (Assemble assemble : assembles) {
                        //二级分面下的碎片
                        if (assemble.getFacetId().equals(secondLayerFacet.getFacetId())) {
                            AssembleContainType assembleContainType = new AssembleContainType();
                            if ("emptyAssembleContent".equals(hasFragment)) {
                                assemble.setAssembleContent("");
                            }
                            assembleContainType.setAssemble(assemble);
                            String ip = HttpUtil.getIp();
                            assembleContainType.setUrl(ip + ":" + port + "/assemble/getAssembleContentById?assembleId=" + assemble.getAssembleId());
                            assembleContainTypes.add(assembleContainType);
                        }
                    }
                    secondLayerFacetContainAssemble.setChildren(assembleContainTypes);
                    secondLayerFacetContainAssemble.setChildrenNumber(assembleContainTypes.size());
                    secondLayerFacetContainAssembles.add(secondLayerFacetContainAssemble);
                }
            }
            //一级分面有二级分面
            if (secondLayerFacetContainAssembles.size() > 0) {
                firstLayerFacetContainAssemble.setChildren(secondLayerFacetContainAssembles);
                firstLayerFacetContainAssemble.setChildrenNumber(secondLayerFacetContainAssembles.size());
                firstLayerFacetContainAssemble.setContainChildrenFacet(true);
            }
            //一级分面没有二级分面
            else {
                firstLayerFacetContainAssemble.setContainChildrenFacet(false);
                List<Object> assembleContainTypes = new ArrayList<>();
                for (Assemble assemble : assembles) {
                    //一级分面下的碎片
                    if (assemble.getFacetId().equals(firstLayerFacet.getFacetId())) {
                        AssembleContainType assembleContainType = new AssembleContainType();
                        if ("emptyAssembleContent".equals(hasFragment)) {
                            assemble.setAssembleContent("");
                        }
                        assembleContainType.setAssemble(assemble);
                        String ip = HttpUtil.getIp();
                        assembleContainType.setUrl(ip + ":" + port + "/assemble/getAssembleContentById?assembleId=" + assemble.getAssembleId());

                        assembleContainTypes.add(assembleContainType);
                    }
                }
                firstLayerFacetContainAssemble.setChildren(assembleContainTypes);
                firstLayerFacetContainAssemble.setChildrenNumber(assembleContainTypes.size());
            }
            firstLayerFacetContainAssembles.add(firstLayerFacetContainAssemble);
        }
        topicContainFacet.setChildren(firstLayerFacetContainAssembles);
        topicContainFacet.setChildrenNumber(firstLayerFacetContainAssembles.size());
        return topicContainFacet;
    }


    /**
     * 爬取某个课程下所有分面的碎片
     * @param domainId 课程id
     * @return
     */
    public Result crawlAssemblesByDomainId(Long domainId) {
        Domain domain = domainRepository.findByDomainId(domainId);
        List<Topic> topics = topicRepository.findByDomainId(domainId);

        for (Topic topic : topics) {
            List<Facet> facets = facetRepository.findByTopicId(topic.getTopicId());
            for (Facet facet : facets) {
                crawlCSDNAssemble(domain, topic, facet, false); // 目前先不启用增量
            }
        }

        return ResultUtil.success(ResultEnum.Assemble_GENERATE_ERROR_3.getCode(),
                ResultEnum.Assemble_GENERATE_ERROR_3.getMsg(),
                null);
    }

    /**
     * 根据以下参数去 CSDN 上爬取碎片
     * @param domain 课程
     * @param topic 主题
     * @param facet 分面
     * @param incremental 是否增量爬取
     * @return
     */
    public void crawlCSDNAssemble(Domain domain,Topic topic, Facet facet, Boolean incremental) {
        Thread csdnCrawler = new CsdnThread(domain, topic, facet, incremental);
        csdnCrawler.start();
    }

}

