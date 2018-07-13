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
import com.xjtu.relation.repository.RelationRepository;
import com.xjtu.source.domain.Source;
import com.xjtu.source.repository.SourceRepository;
import com.xjtu.subject.domain.Subject;
import com.xjtu.subject.repository.SubjectRepository;
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
import java.math.BigInteger;
import java.util.*;

/**
 * 处理课程统计信息
 *
 * @author yangkuan
 * @date 2018/03/21 14:58
 */
@Service
public class StatisticsService {

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
    DependencyRepository dependencyRepository;

    @Autowired
    RelationRepository relationRepository;

    /**
     * 查询所有课程下的统计数据
     *
     * @return
     */
    public Result findStatisticalInformation() {
        List<Domain> domains = domainRepository.findAll();
        //课程名列表
        List<String> domainNames = new ArrayList<>();
        //主题数列表
        int topicNumberSum = 0;
        List<Integer> topicNumbers = new ArrayList<>();
        topicNumbers.add(topicNumberSum);
        //主题上下位关系数列表
        int relationNumberSum = 0;
        List<Integer> relationNumbers = new ArrayList<>();
        relationNumbers.add(relationNumberSum);
        //分面数列表
        int facetNumberSum = 0;
        List<Integer> facetNumbers = new ArrayList<>();
        facetNumbers.add(facetNumberSum);
        //分面关系数列表
        int facetRelationNumberSum = 0;
        List<Integer> facetRelationNumbers = new ArrayList<>();
        facetRelationNumbers.add(facetRelationNumberSum);
        //碎片数列表
        int assembleNumberSum = 0;
        List<Integer> assembleNumbers = new ArrayList<>();
        assembleNumbers.add(assembleNumberSum);
        //依赖关系数列表
        int dependencyNumberSum = 0;
        List<Integer> dependencyNumbers = new ArrayList<>();
        dependencyNumbers.add(dependencyNumberSum);
        for (Domain domain : domains) {
            Long domainId = domain.getDomainId();
            domainNames.add(domain.getDomainName());
            //
            int topicNumber = topicRepository.findByDomainId(domain.getDomainId()).size();
            topicNumberSum += topicNumber;
            topicNumbers.add(topicNumber);
            //
            int relationNumber = relationRepository.findByDomainId(domainId).size();
            relationNumberSum += relationNumber;
            relationNumbers.add(relationNumber);
            //
            int facetNumber = facetRepository.findAllFacetsByDomainId(domainId).size();
            facetNumberSum += facetNumber;
            facetNumbers.add(facetNumber);
            //
            //分面关系数，等于二级分面数、三级分面数之和
            Integer secondLayerFacetNumber = facetRepository.findFacetsByDomainIdAndFacetLayer(domainId, 2).size();
            Integer thirdLayerFacetNumber = facetRepository.findFacetsByDomainIdAndFacetLayer(domainId, 3).size();
            int facetRelationNumber = secondLayerFacetNumber + thirdLayerFacetNumber;
            facetRelationNumberSum += facetRelationNumber;
            facetRelationNumbers.add(facetRelationNumber);
            //
            int assembleNumber = assembleRepository.findAllAssemblesByDomainId(domainId).size();
            assembleNumberSum += assembleNumber;
            assembleNumbers.add(assembleNumber);
            //
            int dependencyNumber = dependencyRepository.findByDomainId(domainId).size();
            dependencyNumberSum += dependencyNumber;
            dependencyNumbers.add(dependencyNumber);
        }
        Map<String, Object> statistics = new HashMap<>(7);
        statistics.put("domainNames", domainNames);
        relationNumbers.set(0, relationNumberSum);
        statistics.put("relationNumbers", relationNumbers);
        topicNumbers.set(0, topicNumberSum);
        statistics.put("topicNumbers", topicNumbers);
        facetNumbers.set(0, facetNumberSum);
        statistics.put("facetNumbers", facetNumbers);
        facetRelationNumbers.set(0, facetRelationNumberSum);
        statistics.put("facetRelationNumbers", facetRelationNumbers);
        assembleNumbers.set(0, assembleNumberSum);
        statistics.put("assembleNumbers", assembleNumbers);
        dependencyNumbers.set(0, dependencyNumberSum);
        statistics.put("dependencyNumbers", dependencyNumbers);
        return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), statistics);
    }

    /**
     * 根据学科名，查询所有课程下的统计数据
     *
     * @param subjectName
     * @return
     */
    public Result findStatisticalInformationBySubjectName(String subjectName) {
        Subject subject = subjectRepository.findBySubjectName(subjectName);
        List<Domain> domains = domainRepository.findBySubjectId(subject.getSubjectId());
        //课程名列表
        List<String> domainNames = new ArrayList<>();
        //主题数列表
        int topicNumberSum = 0;
        List<Integer> topicNumbers = new ArrayList<>();
        topicNumbers.add(topicNumberSum);
        //主题上下位关系数列表
        int relationNumberSum = 0;
        List<Integer> relationNumbers = new ArrayList<>();
        relationNumbers.add(relationNumberSum);
        //分面数列表
        int facetNumberSum = 0;
        List<Integer> facetNumbers = new ArrayList<>();
        facetNumbers.add(facetNumberSum);
        //分面关系数列表
        int facetRelationNumberSum = 0;
        List<Integer> facetRelationNumbers = new ArrayList<>();
        facetRelationNumbers.add(facetRelationNumberSum);
        //碎片数列表
        int assembleNumberSum = 0;
        List<Integer> assembleNumbers = new ArrayList<>();
        assembleNumbers.add(assembleNumberSum);
        //依赖关系数列表
        int dependencyNumberSum = 0;
        List<Integer> dependencyNumbers = new ArrayList<>();
        dependencyNumbers.add(dependencyNumberSum);
        for (Domain domain : domains) {
            Long domainId = domain.getDomainId();
            domainNames.add(domain.getDomainName());
            //
            int topicNumber = topicRepository.findByDomainId(domain.getDomainId()).size();
            topicNumberSum += topicNumber;
            topicNumbers.add(topicNumber);
            //
            int relationNumber = relationRepository.findByDomainId(domainId).size();
            relationNumberSum += relationNumber;
            relationNumbers.add(relationNumber);
            //
            int facetNumber = facetRepository.findAllFacetsByDomainId(domainId).size();
            facetNumberSum += facetNumber;
            facetNumbers.add(facetNumber);
            //
            //分面关系数，等于二级分面数、三级分面数之和
            Integer secondLayerFacetNumber = facetRepository.findFacetsByDomainIdAndFacetLayer(domainId, 2).size();
            Integer thirdLayerFacetNumber = facetRepository.findFacetsByDomainIdAndFacetLayer(domainId, 3).size();
            int facetRelationNumber = secondLayerFacetNumber + thirdLayerFacetNumber;
            facetRelationNumberSum += facetRelationNumber;
            facetRelationNumbers.add(facetRelationNumber);
            //
            int assembleNumber = assembleRepository.findAllAssemblesByDomainId(domainId).size();
            assembleNumberSum += assembleNumber;
            assembleNumbers.add(assembleNumber);
            //
            int dependencyNumber = dependencyRepository.findByDomainId(domainId).size();
            dependencyNumberSum += dependencyNumber;
            dependencyNumbers.add(dependencyNumber);
        }
        Map<String, Object> statistics = new HashMap<>(7);
        statistics.put("domainNames", domainNames);
        relationNumbers.set(0, relationNumberSum);
        statistics.put("relationNumbers", relationNumbers);
        topicNumbers.set(0, topicNumberSum);
        statistics.put("topicNumbers", topicNumbers);
        facetNumbers.set(0, facetNumberSum);
        statistics.put("facetNumbers", facetNumbers);
        facetRelationNumbers.set(0, facetRelationNumberSum);
        statistics.put("facetRelationNumbers", facetRelationNumbers);
        assembleNumbers.set(0, assembleNumberSum);
        statistics.put("assembleNumbers", assembleNumbers);
        dependencyNumbers.set(0, dependencyNumberSum);
        statistics.put("dependencyNumbers", dependencyNumbers);
        return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), statistics);
    }

    /**
     * 根据课程名，获取该门课程的统计信息
     *
     * @param domainName
     * @return
     */
    public Result findStatisticalInformationByDomainName(String domainName) {
        //获取课程
        Domain domain = domainRepository.findByDomainName(domainName);
        if (domain == null) {
            logger.error("课程查询失败：没有课程信息记录");
            return ResultUtil.error(ResultEnum.DOMAIN_SEARCH_ERROR.getCode(), ResultEnum.DOMAIN_SEARCH_ERROR.getMsg());
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

        for (Topic topic : topics) {
            topicNames.add(topic.getTopicName());
            Long topicId = topic.getTopicId();
            //获取分面
            List<Facet> facets = facetRepository.findByTopicId(topicId);
            facetNumbers.add(facets.size());
            facetNumberSum += facets.size();
            //获取一级分面
            List<Facet> firstLayerFacets = facetRepository.findByTopicIdAndFacetLayer(topicId, 1);
            firstLayerFacetNumbers.add(firstLayerFacets.size());
            firstLayerFacetNumberSum += firstLayerFacets.size();
            //获取二级分面
            List<Facet> secondLayerFacets = facetRepository.findByTopicIdAndFacetLayer(topicId, 2);
            secondLayerFacetNumbers.add(secondLayerFacets.size());
            secondLayerFacetNumberSum += secondLayerFacets.size();
            //获取三级分面
            List<Facet> thirdLayerFacets = facetRepository.findByTopicIdAndFacetLayer(topicId, 3);
            thirdLayerFacetNumbers.add(thirdLayerFacets.size());
            thirdLayerFacetNumberSum += thirdLayerFacets.size();
            //获取以主题为起始点的依赖关系
            List<Dependency> dependencies = dependencyRepository.findByStartTopicId(topicId);
            dependencyNumbers.add(dependencies.size());
            dependencyNumberSum += dependencies.size();

            //获取碎片
            Integer assembleNumber = 0;
            for (Facet facet : facets) {
                List<Assemble> assembles = assembleRepository.findByFacetId(facet.getFacetId());
                assembleNumber += assembles.size();
            }
            assembleNumbers.add(assembleNumber);
            assembleNumberSum += assembleNumber;
        }
        //分别将所有统计数据的综合插入列表头
        facetNumbers.add(0, facetNumberSum);
        firstLayerFacetNumbers.add(0, firstLayerFacetNumberSum);
        secondLayerFacetNumbers.add(0, secondLayerFacetNumberSum);
        thirdLayerFacetNumbers.add(0, thirdLayerFacetNumberSum);
        dependencyNumbers.add(0, dependencyNumberSum);
        assembleNumbers.add(0, assembleNumberSum);
        //构建统计结果
        Map<String, Object> statisticsResult = new HashMap<>(7);
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
     * 根据课程名、主题名，查询该主题下的统计数据
     *
     * @param domainName 课程名
     * @param topicName  主题名
     * @return
     */
    public Result findStatisticalInformationByDomainNameAndTopicName(String domainName, String topicName) {
        //获取课程
        Domain domain = domainRepository.findByDomainName(domainName);
        if (domain == null) {
            logger.error("课程查询失败：没有课程信息记录");
            return ResultUtil.error(ResultEnum.DOMAIN_SEARCH_ERROR.getCode(), ResultEnum.DOMAIN_SEARCH_ERROR.getMsg());
        }
        //获取主题
        Topic topic = topicRepository.findByDomainIdAndTopicName(domain.getDomainId(), topicName);
        if (topic == null) {
            logger.error("主题查询失败：没有指定主题");
            return ResultUtil.error(ResultEnum.TOPIC_SEARCH_ERROR.getCode(), ResultEnum.TOPIC_SEARCH_ERROR.getMsg());
        }
        Long topicId = topic.getTopicId();
        List<String> facetNames = new ArrayList<>();
        List<Map<String, Object>> totals = new ArrayList<>();
        //查询分面总数
        List<Facet> facets = facetRepository.findByTopicId(topicId);
        facetNames.add("分面总数");
        Map<String, Object> totalAboutFacets = new HashMap<>(2);
        totalAboutFacets.put("name", "分面总数");
        totalAboutFacets.put("value", facets.size());
        totals.add(totalAboutFacets);
        //查询一级分面总数
        List<Facet> firstLayerFacets = facetRepository.findByTopicIdAndFacetLayer(topicId, 1);
        facetNames.add("一级分面总数");
        Map<String, Object> totalAboutFirstLayerFacets = new HashMap<>(2);
        totalAboutFirstLayerFacets.put("name", "一级分面总数");
        totalAboutFirstLayerFacets.put("value", firstLayerFacets.size());
        totals.add(totalAboutFirstLayerFacets);
        //查询二级分面总数
        List<Facet> secondLayerFacets = facetRepository.findByTopicIdAndFacetLayer(topicId, 2);
        facetNames.add("二级分面总数");
        Map<String, Object> totalAboutSecondLayerFacets = new HashMap<>(2);
        totalAboutSecondLayerFacets.put("name", "二级分面总数");
        totalAboutSecondLayerFacets.put("value", secondLayerFacets.size());
        totals.add(totalAboutSecondLayerFacets);
        //查询碎片总数
        List<Assemble> assembles = assembleRepository.findAllAssemblesByTopicId(topicId);
        facetNames.add("碎片总数");
        Map<String, Object> totalAboutAssembles = new HashMap<>(2);
        totalAboutAssembles.put("name", "碎片总数");
        totalAboutAssembles.put("value", assembles.size());
        totals.add(totalAboutAssembles);
        //查询认知关系总数
        List<Dependency> dependencies = dependencyRepository.findByStartTopicIdOrEndTopicId(topicId, topicId);
        facetNames.add("认知关系总数");
        Map<String, Object> totalAboutDependencies = new HashMap<>(2);
        totalAboutDependencies.put("name", "认知关系总数");
        totalAboutDependencies.put("value", dependencies.size());
        totals.add(totalAboutDependencies);
        //统计一级分面碎片
        List<Map<String, Object>> details = new ArrayList<>();
        for (Facet firstLayerFacet : firstLayerFacets) {
            Map<String, Object> detail = new HashMap<>(2);
            Integer assembleNumber = 0;
            //查询一级分面下的碎片
            List<Assemble> assemblesInFirstLayerFacet = assembleRepository.findByFacetId(firstLayerFacet.getFacetId());
            assembleNumber += assemblesInFirstLayerFacet.size();
            //查询二级分面下的碎片
            List<Facet> secondLayerFacetsInFirstLayerFacet = facetRepository
                    .findByParentFacetIdAndFacetLayer(firstLayerFacet.getFacetId(), 2);
            for (Facet secondLayerFacetInFirstLayerFacet : secondLayerFacetsInFirstLayerFacet) {
                //查询二级碎片并添加
                assembleNumber += assembleRepository.findByFacetId(secondLayerFacetInFirstLayerFacet.getFacetId()).size();
                //此处未考虑三级分面
            }
            facetNames.add("f1:" + firstLayerFacet.getFacetName());
            detail.put("name", "f1:" + firstLayerFacet.getFacetName());
            detail.put("value", assembleNumber);
            details.add(detail);
        }
        //统计二级分面碎片
        for (Facet secondLayerFacet : secondLayerFacets) {
            Map<String, Object> detail = new HashMap<>(2);
            List<Assemble> assemblesInSecondLayerFacet = assembleRepository.findByFacetId(secondLayerFacet.getFacetId());
            facetNames.add("f2:" + secondLayerFacet.getFacetName());
            detail.put("name", "f2:" + secondLayerFacet.getFacetName());
            detail.put("value", assemblesInSecondLayerFacet.size());
            details.add(detail);
        }
        Map<String, Object> statisticsInformation = new HashMap<>(3);
        statisticsInformation.put("facetNames", facetNames);
        statisticsInformation.put("details", details);
        statisticsInformation.put("totals", totals);
        return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), statisticsInformation);
    }

    /**
     * 根据课程名、主题名列表（以“，”分割的字符串），查询每个数据源下碎片数量分布
     *
     * @param domainName                 课程名
     * @param topicNamesSegmentedByComma 主题名列表
     */
    public Result findAssembleDistributionByDomainNameAndTopicNamesSplitedByComma(String domainName, String topicNamesSegmentedByComma) {
        String[] topicNames = topicNamesSegmentedByComma.split(",");
        //1.查询所有数据源
        List<Source> sources = sourceRepository.findAll();

        //该课程及主题下所有碎片
        Result result = findAssemblesByDomainNameAndTopicNames(domainName, topicNames);
        List<Assemble> assemblesInDomainAndTopics;
        if (result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            assemblesInDomainAndTopics = (ArrayList<Assemble>) result.getData();
        } else {
            return result;
        }
        //2.按数据源统计碎片数量
        List<Map<String, Object>> assembleDistributionGroupBySources = new ArrayList<>();
        List<String> sourceNames = new ArrayList<>();
        for (Source source : sources) {
            //获取数据源名列表
            sourceNames.add(source.getSourceName());

            Map<String, Object> assembleDistributionGroupBySource = new HashMap<>(2);
            assembleDistributionGroupBySource.put("name", source.getSourceName());
            Integer assembleCount = 0;
            for (Assemble assemble : assemblesInDomainAndTopics) {
                if (assemble.getSourceId().equals(source.getSourceId())) {
                    assembleCount++;
                }
            }
            assembleDistributionGroupBySource.put("value", assembleCount);
            assembleDistributionGroupBySources.add(assembleDistributionGroupBySource);
        }
        //拼接结果
        Map<String, Object> assembleDistribution = new HashMap<>(2);
        assembleDistribution.put("assembleDistributionGroupBySources", assembleDistributionGroupBySources);
        assembleDistribution.put("sourceNames", sourceNames);
        return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), assembleDistribution);
    }

    /**
     * 根据课程名、主题名列表（以“，”分割的字符串），查询每个其下的碎片
     *
     * @param domainName                 课程名
     * @param topicNamesSegmentedByComma 主题名列表
     * @return 碎片列表
     */
    public Result findAssemblesByDomainNameAndTopicNamesSplitedByComma(String domainName, String topicNamesSegmentedByComma) {
        String[] topicNames = topicNamesSegmentedByComma.split(",");
        //该课程及主题下所有碎片
        Result result = findAssemblesByDomainNameAndTopicNames(domainName, topicNames);
        List<Assemble> assemblesInDomainAndTopics;
        if (result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            assemblesInDomainAndTopics = (ArrayList<Assemble>) result.getData();
        } else {
            return result;
        }
        return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), assemblesInDomainAndTopics);
    }

    /**
     * 根据课程名、主题名列表（以“，”分割的字符串）、数据源，进行碎片词频统计
     *
     * @param domainName                 课程名
     * @param topicNamesSegmentedByComma 主题名列表
     * @param sourceName                 数据源名
     * @param hasSourceName              是否包含数据源名
     * @return
     */
    public Result findWordFrequencyBySourceNameAndDomainNameAndTopicNames(String domainName
            , String topicNamesSegmentedByComma, String sourceName, boolean hasSourceName) {
        String[] topicNames = topicNamesSegmentedByComma.split(",");
        //根据课程名、主题名列表，查询每个其下的碎片
        //该课程及主题下所有碎片
        Result result = findAssemblesByDomainNameAndTopicNames(domainName, topicNames);
        List<Assemble> assemblesInDomainAndTopics;
        if (result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            assemblesInDomainAndTopics = (ArrayList<Assemble>) result.getData();
        } else {
            return result;
        }
        //如果存在数据源名，就过滤掉那些不是该数据源下的碎片
        if (hasSourceName) {
            assemblesInDomainAndTopics = filterAssemblesBySourceName(assemblesInDomainAndTopics, sourceName);
        }
        // 存储所有碎片的文本
        StringBuffer text = new StringBuffer();
        for (Assemble assemblesInDomainAndTopic : assemblesInDomainAndTopics) {
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
        } catch (IOException e) {
            logger.error("词频查询失败：中文分词失败");
            return ResultUtil.error(ResultEnum.STATISTICS_SEARCH_ERROR.getCode()
                    , ResultEnum.STATISTICS_SEARCH_ERROR.getMsg(), e);
        }
        return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), wordFrequency);
    }

    /**
     * 根据课程名、主题名列表，查询每个其下的碎片
     *
     * @param domainName 课程名
     * @param topicNames 主题名列表
     * @return
     */
    private Result findAssemblesByDomainNameAndTopicNames(String domainName, String[] topicNames) {
        //获取课程id
        Domain domain = domainRepository.findByDomainName(domainName);
        if (domain == null) {
            logger.error("查询失败：没有对应课程");
            return ResultUtil.error(ResultEnum.DOMAIN_SEARCH_ERROR.getCode(), ResultEnum.DOMAIN_SEARCH_ERROR.getMsg());
        }
        Long domainId = domain.getDomainId();
        //该课程及主题下所有碎片
        List<Assemble> assemblesInDomainAndTopics = new ArrayList<>();
        for (String topicName : topicNames) {
            //获取课程对应topic
            Topic topic = topicRepository.findByDomainIdAndTopicName(domainId, topicName);
            if (topic == null) {
                logger.error("查询失败：没有对应主题");
                return ResultUtil.error(ResultEnum.TOPIC_SEARCH_ERROR.getCode(), ResultEnum.TOPIC_SEARCH_ERROR.getMsg());
            }
            //根据主题Id获取分面
            List<Facet> facets = facetRepository.findByTopicId(topic.getTopicId());
            for (Facet facet : facets) {
                //根据分面Id获取碎片
                List<Assemble> assembles = assembleRepository.findByFacetId(facet.getFacetId());
                assemblesInDomainAndTopics.addAll(assembles);
            }
        }
        return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), assemblesInDomainAndTopics);
    }

    /**
     * 根据数据源，过滤碎片
     *
     * @param assembles
     * @param sourceName
     * @return 过滤后的碎片列表
     */
    private List<Assemble> filterAssemblesBySourceName(List<Assemble> assembles, String sourceName) {
        List<Assemble> newAssembles = new ArrayList<>();
        Long sourceId = sourceRepository.findBySourceName(sourceName).getSourceId();
        for (Assemble assemble : assembles) {
            if (assemble.getSourceId().equals(sourceId)) {
                newAssembles.add(assemble);
            }
        }
        return newAssembles;
    }

    /**
     * 统计所有课程信息，包括包含学科名、课程名、课程id、主题数、
     * 一级分面、二级分面和三级分面数、碎片数、依赖（认知关系）数
     *
     * @return
     */
    public Result findDomainDistribution() {
        //查询所有课程
        List<Domain> domains = domainRepository.findAll();
        //存储统计结果
        List<Map<String, Object>> results = new ArrayList<>();
        List<Long> domainIds = new ArrayList<>();
        for (Domain domain : domains) {
            Long domainId = domain.getDomainId();
            domainIds.add(domainId);
            Map<String, Object> result = new HashMap<>(11);
            result.put("domainId", domain.getDomainId());
            result.put("domainName", domain.getDomainName());
            result.put("note", "");
            //根据主题查询分面（一级、二级、三级、总数）
            //获取总分面数
            int facetTotalNumber = 0;
            List<Integer> layer = Arrays.asList(1, 2, 3);
            List<BigInteger> facetNumbers = facetRepository.findFacetNumberByDomainIdAndFacetLayer(domainId, layer);
            for (BigInteger facetNumber : facetNumbers) {
                facetTotalNumber += facetNumber.intValue();
            }
            //一级分面
            int firstLayerFacetNumber = facetNumbers.get(0).intValue();
            //二级分面
            int secondLayerFacetNumber = 0;
            if (facetNumbers.size() >= 2) {
                secondLayerFacetNumber = facetNumbers.get(1).intValue();
            }
            //三级分面
            int thirdLayerFacetNumber = 0;
            if (facetNumbers.size() == 3) {
                thirdLayerFacetNumber = facetNumbers.get(2).intValue();
            }
            result.put("facetNumber", facetTotalNumber);
            result.put("firstLayerFacetNumber", firstLayerFacetNumber);
            result.put("secondLayerFacetNumber", secondLayerFacetNumber);
            result.put("thirdLayerFacetNumber", thirdLayerFacetNumber);
            //碎片数量
            int assembleNumber = assembleRepository.findAssembleNumberByDomainId(domainId);
            result.put("assembleNumber", assembleNumber);
            //查询主题依赖关系
            int dependencyNumber = dependencyRepository.findDependencyNumberByDomainId(domainId);
            result.put("dependencyNumber", dependencyNumber);
            results.add(result);
        }
        List<BigInteger> topicNumbers = topicRepository.findTopicNumbersByDomainId(domainIds);
        for (int i = 0; i < domainIds.size(); i++) {
            results.get(i).put("topicNumber", topicNumbers.get(i));
        }
        return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), results);
    }

    /**
     * 统计学科、课程数量
     *
     * @return
     */
    public Result countSubjectAndDomain() {
        List<Subject> subjects = subjectRepository.findAll();
        List<Domain> domains = domainRepository.findAll();
        Map<String, Integer> information = new HashMap<>(2);
        information.put("subjectNumber", subjects.size());
        information.put("domainNumber", domains.size());
        return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), information);
    }

    /**
     * 根据关键词，查询与关键词相似的课程、主题和分面
     *
     * @param keyword
     * @return
     */
    public Result queryKeyword(String keyword) {
        List<Map<String, Object>> queryResults = new ArrayList<>();
        //根据关键字，查询相关课程
        List<Domain> domains = domainRepository.findByKeyword(keyword);
        for (Domain domain : domains) {
            Map<String, Object> domainQueryResult = new HashMap<>(2);
            domainQueryResult.put("type", "domain");
            domainQueryResult.put("name", domain.getDomainName());
            queryResults.add(domainQueryResult);
        }
        //根据关键字，查询相关主题
        List<Map<String, Object>> topicInformations = topicRepository.findTopicInformationByKeyword(keyword);
        for (Map<String, Object> topicInformation : topicInformations) {
            Map<String, Object> topicQueryResult = new HashMap<>(3);
            topicQueryResult.put("type", "topic");
            topicQueryResult.put("name", topicInformation.get("0"));
            topicQueryResult.put("domainName", topicInformation.get("1"));
            queryResults.add(topicQueryResult);
        }
        //根据关键字，查询相关分面
        List<Map<String, Object>> facetInformations = facetRepository.findFacetInformationByKeyword(keyword);
        for (Map<String, Object> facetInformation : facetInformations) {
            Map<String, Object> facetQueryResult = new HashMap<>(5);
            facetQueryResult.put("type", "facet");
            facetQueryResult.put("name", facetInformation.get("0"));
            facetQueryResult.put("layer", facetInformation.get("1"));
            facetQueryResult.put("topicName", facetInformation.get("2"));
            facetQueryResult.put("domainName", facetInformation.get("3"));
            queryResults.add(facetQueryResult);
        }
        return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), queryResults);
    }
}
