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
import com.xjtu.topic.domain.Topic;
import com.xjtu.topic.repository.TopicRepository;
import com.xjtu.utils.ResultUtil;
import io.swagger.models.auth.In;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
            return ResultUtil.error(ResultEnum.Domain_SEARCH_ERROR.getCode(),ResultEnum.Domain_SEARCH_ERROR.getMsg());
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
}
