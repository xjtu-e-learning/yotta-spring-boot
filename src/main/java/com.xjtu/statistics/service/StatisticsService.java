package com.xjtu.statistics.service;

import com.xjtu.assemble.domain.Assemble;
import com.xjtu.assemble.repository.AssembleRepository;
import com.xjtu.common.domain.Result;
import com.xjtu.common.domain.ResultEnum;
import com.xjtu.dependency.domain.Dependency;
import com.xjtu.dependency.repository.DependencyRepository;
import com.xjtu.domain.domain.Domain;
import com.xjtu.domain.repository.DomainRepository;
import com.xjtu.facet.domain.Facet;
import com.xjtu.facet.repository.FacetRepository;
import com.xjtu.source.domain.Source;
import com.xjtu.source.repository.SourceRepository;
import com.xjtu.topic.domain.Topic;
import com.xjtu.topic.repository.TopicRepository;
import com.xjtu.utils.ResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;


import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 处理课程统计信息
 * @author yangkuan
 * @date 2018/03/21 14:58
 * */
@Service
public class StatisticsService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    SourceRepository sourceRepository;

    @Autowired
    DomainRepository domainRepository;

    @Autowired
    TopicRepository topicRepository;

    @Autowired
    FacetRepository facetRepository;

    @Autowired
    AssembleRepository assembleRepository;

    @Autowired
    DependencyRepository dependencyRepository;

    /**
     * 根据课程名，获取该门课程的统计信息
     * @param domainName
     * @return
     * */
    public Result findStatisticalInformationByDomainName(String domainName){
        //获取课程
        Domain domain = domainRepository.findByDomainName(domainName);
        if(domain==null){
            logger.error("课程查询失败：没有课程信息记录");
            return ResultUtil.error(ResultEnum.DOMAIN_SEARCH_ERROR.getCode(),ResultEnum.DOMAIN_SEARCH_ERROR.getMsg());
        }
        //获取课程下的主题列表
        List<Topic> topics = topicRepository.findByDomainId(domain.getDomainId());
        //主题名列表
        List<String> topicNames = new ArrayList<>();
        //分面数量列表（每一维与主题对应）
        List<Integer> facetNumbers = new ArrayList<>();
        Integer facetNumberSum = 0;
        //一级分面数量列表
        List<Integer> firstLayerFacetNumbers = new ArrayList<>();
        Integer firstLayerFacetNumberSum = 0;
        //二级分面数量列表
        List<Integer> secondLayerFacetNumbers = new ArrayList<>();
        Integer secondLayerFacetNumberSum = 0;
        //三级分面数量列表
        List<Integer> thirdLayerFacetNumbers = new ArrayList<>();
        Integer thirdLayerFacetNumberSum = 0;
        //主题依赖关系数量列表
        List<Integer> dependencyNumbers = new ArrayList<>();
        Integer dependencyNumberSum = 0;
        //碎片数量列表
        List<Integer> assembleNumbers = new ArrayList<>();
        Integer assembleNumberSum = 0;

        for(Topic topic:topics){
            topicNames.add(topic.getTopicName());
            Long topicId = topic.getTopicId();
            //获取分面
            List<Facet> facets = facetRepository.findByTopicId(topicId);
            facetNumbers.add(facets.size());
            facetNumberSum += facets.size();
            //获取一级分面
            List<Facet> firstLayerFacets = facetRepository.findByFacetLayerAndTopicId(1,topicId);
            firstLayerFacetNumbers.add(firstLayerFacets.size());
            firstLayerFacetNumberSum += firstLayerFacets.size();
            //获取二级分面
            List<Facet> secondLayerFacets = facetRepository.findByFacetLayerAndTopicId(2,topicId);
            secondLayerFacetNumbers.add(secondLayerFacets.size());
            secondLayerFacetNumberSum += secondLayerFacets.size();
            //获取三级分面
            List<Facet> thirdLayerFacets = facetRepository.findByFacetLayerAndTopicId(3,topicId);
            thirdLayerFacetNumbers.add(thirdLayerFacets.size());
            thirdLayerFacetNumberSum += thirdLayerFacets.size();
            //获取以主题为起始点的依赖关系
            List<Dependency> dependencies = dependencyRepository.findByStartTopicId(topicId);
            dependencyNumbers.add(dependencies.size());
            dependencyNumberSum += dependencies.size();

            //获取碎片
            Integer assembleNumber = 0;
            for(Facet facet:facets){
                List<Assemble> assembles = assembleRepository.findByFacetId(facet.getFacetId());
                assembleNumber += assembles.size();
            }
            assembleNumbers.add(assembleNumber);
            assembleNumberSum += assembleNumber;
        }
        //分别将所有统计数据的综合插入列表头
        facetNumbers.add(0,facetNumberSum);
        firstLayerFacetNumbers.add(0,firstLayerFacetNumberSum);
        secondLayerFacetNumbers.add(0,secondLayerFacetNumberSum);
        thirdLayerFacetNumbers.add(0,thirdLayerFacetNumberSum);
        dependencyNumbers.add(0,dependencyNumberSum);
        assembleNumbers.add(0,assembleNumberSum);
        //构建统计结果
        Map<String,Object> statisticsResult = new HashMap<>(7);
        statisticsResult.put("topicNames", topicNames);
        statisticsResult.put("facetNumbers", facetNumbers);
        statisticsResult.put("firstLayerFacetNumbers", firstLayerFacetNumbers);
        statisticsResult.put("secondLayerFacetNumbers", secondLayerFacetNumbers);
        statisticsResult.put("thirdLayerFacetNumbers", thirdLayerFacetNumbers);
        statisticsResult.put("dependencyNumbers", dependencyNumbers);
        statisticsResult.put("assembleNumbers", assembleNumbers);

        return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), statisticsResult);
    }

    /**
     * 根据课程名、主题名列表（以“，”分割的字符串），查询每个数据源下碎片数量分布
     * @param domainName 课程名
     * @param topicNamesSegmentedByComma 主题名列表
     * */
    public Result findAssembleDistributionByDomainNameAndTopicNamesSplitedByComma(String domainName, String topicNamesSegmentedByComma){
        String[] topicNames = topicNamesSegmentedByComma.split(",");
        //1.查询所有数据源
        List<Source> sources = sourceRepository.findAll();

        //该课程及主题下所有碎片
        Result result = findAssemblesByDomainNameAndTopicNames(domainName,topicNames);
        List<Assemble> assemblesInDomainAndTopics;
        if(result.getCode().equals(ResultEnum.SUCCESS.getCode())){
            assemblesInDomainAndTopics = (ArrayList<Assemble>) result.getData();
        }
        else {
            return result;
        }
        //2.按数据源统计碎片数量
        List<Map<String,Object>> assembleDistributionGroupBySources = new ArrayList<>();
        List<String> sourceNames = new ArrayList<>();
        for(Source source:sources){
            //获取数据源名列表
            sourceNames.add(source.getSourceName());

            Map<String,Object> assembleDistributionGroupBySource = new HashMap<>(2);
            assembleDistributionGroupBySource.put("name",source.getSourceName());
            Integer assembleCount = 0;
            for(Assemble assemble:assemblesInDomainAndTopics){
                if(assemble.getSourceId().equals(source.getSourceId())){
                    assembleCount ++;
                }
            }
            assembleDistributionGroupBySource.put("value",assembleCount);
            assembleDistributionGroupBySources.add(assembleDistributionGroupBySource);
        }
        //拼接结果
        Map<String,Object> assembleDistribution = new HashMap<>(2);
        assembleDistribution.put("assembleDistributionGroupBySources",assembleDistributionGroupBySources);
        assembleDistribution.put("sourceNames",sourceNames);
        logger.info("查询碎片统计信息成功");
        return ResultUtil.success(ResultEnum.SUCCESS.getCode(),ResultEnum.SUCCESS.getMsg(),assembleDistribution);
    }

    /**
     * 根据课程名、主题名列表（以“，”分割的字符串），查询每个其下的碎片
     * @param domainName 课程名
     * @param topicNamesSegmentedByComma 主题名列表
     * @return 碎片列表
     */
    public Result findAssemblesByDomainNameAndTopicNamesSplitedByComma(String domainName, String topicNamesSegmentedByComma){
        String[] topicNames = topicNamesSegmentedByComma.split(",");
        //该课程及主题下所有碎片
        Result result = findAssemblesByDomainNameAndTopicNames(domainName,topicNames);
        List<Assemble> assemblesInDomainAndTopics;
        if(result.getCode().equals(ResultEnum.SUCCESS.getCode())){
            assemblesInDomainAndTopics = (ArrayList<Assemble>) result.getData();
        }
        else {
            return result;
        }
        logger.info("查询碎片信息成功");
        return ResultUtil.success(ResultEnum.SUCCESS.getCode(),ResultEnum.SUCCESS.getMsg(),assemblesInDomainAndTopics);
    }
    /**
     * 根据课程名、主题名列表（以“，”分割的字符串）、数据源，进行碎片词频统计
     * @param domainName 课程名
     * @param topicNamesSegmentedByComma 主题名列表
     * @param sourceName 数据源名
     * @param hasSourceName 是否包含数据源名
     * @return
     */
    public Result findWordFrequencyBySourceNameAndDomainNameAndTopicNames(String domainName
            , String topicNamesSegmentedByComma, String sourceName, boolean hasSourceName){
        String[] topicNames = topicNamesSegmentedByComma.split(",");
        //根据课程名、主题名列表，查询每个其下的碎片
        //该课程及主题下所有碎片
        Result result = findAssemblesByDomainNameAndTopicNames(domainName,topicNames);
        List<Assemble> assemblesInDomainAndTopics;
        if(result.getCode().equals(ResultEnum.SUCCESS.getCode())){
            assemblesInDomainAndTopics = (ArrayList<Assemble>) result.getData();
        }
        else {
            return result;
        }
        //如果存在数据源名，就过滤掉那些不是该数据源下的碎片
        if(hasSourceName){
            assemblesInDomainAndTopics = filterAssemblesBySourceName(assemblesInDomainAndTopics, sourceName);
        }
        // 存储所有碎片的文本
        StringBuffer text = new StringBuffer();
        for(Assemble assemblesInDomainAndTopic:assemblesInDomainAndTopics){
            text.append(assemblesInDomainAndTopic.getAssembleText());
        }
        //使用Lucene Ik Analyzer 中文分词
        StringReader reader = new StringReader(text.toString());
        Map<String, Integer> wordFrequency = new HashMap<>();
        // 当为true时，分词器进行最大词长切分
        IKSegmenter ik = new IKSegmenter(reader, true);
        Lexeme lexeme;
        try {
            while ((lexeme = ik.next()) != null) {
                String word = lexeme.getLexemeText();
                if (!wordFrequency.containsKey(word)) {
                    wordFrequency.put(word, 1);
                } else {
                    wordFrequency.put(word, wordFrequency.get(word) + 1);
                }
            }
        }
        catch (IOException e){
            logger.error("词频查询失败：中文分词失败");
            return ResultUtil.error(ResultEnum.STATISTICS_SEARCH_ERROR.getCode()
                    , ResultEnum.STATISTICS_SEARCH_ERROR.getMsg(), e);
        }
        return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), wordFrequency);
    }

    /**
     * 根据课程名、主题名列表，查询每个其下的碎片
     * @param domainName 课程名
     * @param topicNames 主题名列表
     * @return
     */
    private Result findAssemblesByDomainNameAndTopicNames(String domainName, String[] topicNames){
        //获取课程id
        Domain domain = domainRepository.findByDomainName(domainName);
        if(domain==null){
            logger.error("查询失败：没有对应课程");
            return ResultUtil.error(ResultEnum.DOMAIN_SEARCH_ERROR.getCode(),ResultEnum.DOMAIN_SEARCH_ERROR.getMsg());
        }
        Long domainId = domain.getDomainId();
        //该课程及主题下所有碎片
        List<Assemble> assemblesInDomainAndTopics = new ArrayList<>();
        for(String topicName:topicNames){
            //获取课程对应topic
            Topic topic = topicRepository.findByTopicNameAndDomainId(topicName,domainId);
            if(topic==null){
                logger.error("查询失败：没有对应主题");
                return ResultUtil.error(ResultEnum.TOPIC_SEARCH_ERROR.getCode(),ResultEnum.TOPIC_SEARCH_ERROR.getMsg());
            }
            //根据主题Id获取分面
            List<Facet> facets = facetRepository.findByTopicId(topic.getTopicId());
            for(Facet facet:facets){
                //根据分面Id获取碎片
                List<Assemble> assembles = assembleRepository.findByFacetId(facet.getFacetId());
                assemblesInDomainAndTopics.addAll(assembles);
            }
        }
        return ResultUtil.success(ResultEnum.SUCCESS.getCode(),ResultEnum.SUCCESS.getMsg(),assemblesInDomainAndTopics);
    }
    /**
     * 根据数据源，过滤碎片
     * @param assembles
     * @param sourceName
     * @return 过滤后的碎片列表
     */
    private List<Assemble> filterAssemblesBySourceName(List<Assemble> assembles, String sourceName){
        List<Assemble> newAssembles = new ArrayList<>();
        Long sourceId = sourceRepository.findBySourceName(sourceName).getSourceId();
        for(Assemble assemble:assembles){
            if(assemble.getSourceId().equals(sourceId)){
                newAssembles.add(assemble);
            }
        }
        return newAssembles;
    }

}
