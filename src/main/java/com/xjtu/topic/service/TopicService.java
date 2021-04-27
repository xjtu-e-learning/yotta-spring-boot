package com.xjtu.topic.service;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import com.xjtu.relation.domain.Relation;
import com.xjtu.relation.repository.RelationRepository;
import com.xjtu.spider.service.SpiderService;
import com.xjtu.spider_new.service.NewSpiderService;
import com.xjtu.topic.dao.TopicDAO;
import com.xjtu.topic.domain.Topic;
import com.xjtu.topic.domain.TopicContainAssembleText;
import com.xjtu.topic.domain.TopicContainFacet;
import com.xjtu.topic.extraction.TopicExtraction;
import com.xjtu.topic.extraction.TopicSelect;
import com.xjtu.topic.repository.TopicRepository;
import com.xjtu.utils.HttpUtil;
import com.xjtu.utils.ResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * 处理topic主题数据
 *
 * @author yangkuan
 * @date 2018/03/09 15:15
 */

@Service
public class TopicService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private NewSpiderService spiderService;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private DomainRepository domainRepository;

    @Autowired
    private FacetRepository facetRepository;

    @Autowired
    private AssembleRepository assembleRepository;

    @Autowired
    private RelationRepository relationRepository;

    @Autowired
    private DependencyRepository dependencyRepository;

    @Autowired
    private DomainService domainService;

    @Autowired
    private TopicDAO topicDAO;

    @Value("${gexfpath}")
    private String gexfPath;

    @Value("${server.port}")
    private Integer port;


    /**
     * 插入主题信息
     *
     * @param topic 需要插入的主题
     * @return 插入结果
     */
    public Result insertTopic(Topic topic) {

        String topicName = topic.getTopicName();
        Long domainId = topic.getDomainId();

        //插入主题的主题名必须存在且不能为空
        if (topicName == null || topicName.equals("") || topicName.length() == 0) {
            logger.error("主题信息插入失败：主题名不存在或者为空");
            return ResultUtil.error(ResultEnum.TOPIC_INSERT_ERROR.getCode(), ResultEnum.TOPIC_INSERT_ERROR.getMsg());
        }
        //保证插入主题不存在数据库中
        else if (topicRepository.findByDomainIdAndTopicName(domainId, topicName) == null) {
            Topic topicInsert = topicRepository.save(topic);
            if (topicInsert == null) {
                logger.error("主题信息插入失败：数据库插入语句失败");
                return ResultUtil.error(ResultEnum.TOPIC_INSERT_ERROR_2.getCode(), ResultEnum.TOPIC_INSERT_ERROR_2.getMsg());
            } else {
                logger.debug("插入主题信息成功");
                return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), "插入主题:" + topicName + "成功");
            }
        }
        //主题信息已经在数据库中
        logger.error("主题信息插入失败：插入主题已经存在");
        return ResultUtil.error(ResultEnum.TOPIC_INSERT_ERROR_1.getCode(), ResultEnum.TOPIC_INSERT_ERROR_1.getMsg());
    }

    /**
     * 插入主题信息
     *
     * @param domainName 课程名
     * @param topicName  主题名
     * @return 插入结果
     */
    public Result insertTopicByNameAndDomainName(String domainName, String topicName) {
        Domain domain = domainRepository.findByDomainName(domainName);
        if (domain == null) {
            logger.error("主题信息插入失败：没有对应的课程");
            return ResultUtil.error(ResultEnum.TOPIC_INSERT_ERROR_3.getCode(), ResultEnum.TOPIC_INSERT_ERROR_3.getMsg());
        }
        Topic topic = new Topic();
        topic.setDomainId(domain.getDomainId());
        topic.setTopicName(topicName);
        return insertTopic(topic);
    }

    /**
     * 删除主题信息：根据主题Id
     *
     * @param topicId 主题Id
     * @return 删除结果
     */
    public Result deleteTopic(Long topicId) {
        try {
            topicRepository.delete(topicId);
            return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), "主题删除成功");
        } catch (Exception err) {
            logger.error("主题删除失败");
            return ResultUtil.error(ResultEnum.TOPIC_DELETE_ERROR.getCode(), ResultEnum.TOPIC_DELETE_ERROR.getMsg());
        }
    }


    /**
     * 辅助之后两个分别按照主题名课程名、按照主题ID删除主题的函数，负责具体删除功能。
     */
    public void deleteTopicContent(Long topicId) {
        //删除主题表中该主题
        topicRepository.delete(topicId);
        //删除分面表中主题下的分面
        facetRepository.deleteByTopicId(topicId);
        //删除碎片
        assembleRepository.deleteByTopicId(topicId);

        //先删除子主题对应上下位关系
        relationRepository.deleteByChildTopicId(topicId);
        //递归删除父主题对应上下位关系
        //先找到父主题对应的关系
        List<Relation> relations = relationRepository.findByParentTopicId(topicId);
        //删除父主题对应的关系
        relationRepository.deleteByParentTopicId(topicId);
        while ((relations.size() != 0) && (relations != null)) {
            List<Relation> childRelations = new ArrayList<>();
            for (Relation relation : relations) {
                //循环找到对应的下位主题
                childRelations.addAll(relationRepository.findByParentTopicId(relation.getChildTopicId()));
                //删除父主题对应的关系
                relationRepository.deleteByParentTopicId(relation.getChildTopicId());
            }
            relations = childRelations;
        }
        //删除依赖关系
        dependencyRepository.deleteByStartTopicIdOrEndTopicId(topicId, topicId);
    }

    /**
     * 删除某门课程的主题信息：根据课程名和主题名进行删除(注：删除过程的事务一致性问题未解决)
     *
     * @param topicName  主题名
     * @param domainName 课程名
     * @return 删除结果
     */
    public Result deleteTopicByNameAndDomainName(String topicName, String domainName) {
        try {
            Domain domain = domainRepository.findByDomainName(domainName);
            if (domain == null) {
                logger.error("主题删除失败：没有主题对应的课程");
                return ResultUtil.error(ResultEnum.TOPIC_DELETE_ERROR_1.getCode(), ResultEnum.TOPIC_DELETE_ERROR_1.getMsg());
            }

            Topic topic = topicRepository.findByDomainIdAndTopicName(domain.getDomainId(), topicName);
            if (topic == null) {
                logger.error("主题名删除失败：主题数据不存在");
                return ResultUtil.error(ResultEnum.TOPIC_DELETE_ERROR_2.getCode(), ResultEnum.TOPIC_DELETE_ERROR_2.getMsg());
            }
            Long topicId = topic.getTopicId();
            //具体负责删除的代码部分
            deleteTopicContent(topicId);
            //删除对应的课程gexf文件
            File gexfFile = new File(gexfPath + "\\" + domainName + ".gexf");
            gexfFile.delete();
            return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), "主题删除成功");
        } catch (Exception exception) {
            logger.error("错误：" + exception);
            logger.error("主题名删除失败：删除语句执行失败");
            return ResultUtil.error(ResultEnum.TOPIC_DELETE_ERROR.getCode(), ResultEnum.TOPIC_DELETE_ERROR.getMsg());
        }
    }

    /**
     * 根据主题ID删除该主题的所有信息（已废弃，请勿使用）
     *
     * @param topicId
     * @return
     */
    public Result deleteTopicByTopicId(Long topicId) {
        try {
            Topic topic = topicRepository.findByTopicId(topicId);
            if (topic == null) {
                logger.error("主题删除失败：主题数据不存在");
                return ResultUtil.error(ResultEnum.TOPIC_DELETE_ERROR_2.getCode(), ResultEnum.TOPIC_DELETE_ERROR_2.getMsg());
            }
            //具体负责删除的代码部分
            deleteTopicContent(topicId);
            return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), "主题删除成功");
        } catch (Exception exception) {
            logger.error("错误：" + exception);
            logger.error("主题名删除失败：删除语句执行失败");
            return ResultUtil.error(ResultEnum.TOPIC_DELETE_ERROR.getCode(), ResultEnum.TOPIC_DELETE_ERROR.getMsg());
        }
    }


    /**
     * 根据课程和主题名字，递归地依次删除该主题下的主题依赖关系、碎片、分面，请谨慎操作！
     *
     * @param domainName
     * @return
     */
    public Result deleteTopicCompleteByDomainNameAndTopicName(String domainName, String topicName) {

        Domain domain = domainRepository.findByDomainName(domainName);
        if (domain == null) {
            logger.error("主题删除失败：指定课程不存在");
            return ResultUtil.error(ResultEnum.TOPIC_DELETE_ERROR_1.getCode(), ResultEnum.TOPIC_DELETE_ERROR_1.getMsg());
        }
        Topic topic = topicRepository.findByDomainIdAndTopicName(domain.getDomainId(), topicName);
        if (topic == null) {
            logger.error("主题删除失败：此课程下不存在该主题");
            return ResultUtil.error(ResultEnum.TOPIC_DELETE_ERROR_2.getCode(), ResultEnum.TOPIC_DELETE_ERROR_2.getMsg());
        }
        try{
            Long topicId = topic.getTopicId();
            logger.info("开始删除 " + domain.getDomainName() + " 课程下 " + topicName + " 主题的依赖关系");
            dependencyRepository.deleteByStartTopicIdOrEndTopicId(topicId, topicId);
            logger.info("删除 " + domain.getDomainName() + " 课程下 " + topicName + " 主题的依赖关系完成");
            logger.info("开始删除 " + topicName + " 主题的碎片");
            assembleRepository.deleteByTopicId(topicId);
            logger.info("删除 " + topicName + " 主题的碎片完成");
            logger.info("开始删除 " + topicName + " 主题的分面");
            facetRepository.deleteByTopicId(topicId);
            logger.info("删除 " + topicName + " 主题的分面完成");
            logger.info("开始删除该主题信息");
            topicRepository.deleteByDomainIdAndTopicId(domain.getDomainId(), topicId);
            logger.info("删除该主题信息完成！" + topicName + " 主题下的所有主题依赖关系-分面-碎片已完全删除。");
            return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), "主题 " + topicName + " 下的所有主题依赖关系-分面-碎片已完全删除。");
        } catch (Exception excepetion) {
            logger.error("错误：" + excepetion);
            logger.error("主题完整删除失败：删除语句执行失败");
            return ResultUtil.error(ResultEnum.TOPIC_DELETE_ERROR.getCode(), ResultEnum.TOPIC_DELETE_ERROR.getMsg());
        }
    }


    /**
     * 根据课程ID，递归地依次删除该课程下的主题依赖关系、碎片、分面、主题，请谨慎操作！
     *
     * @param domainId
     * @return
     * @author Qi Jingchao
     */
    public Result deleteCompleteTopicByDomainId(Long domainId) {
        try {
            Domain domain = domainRepository.findByDomainId(domainId);
            if (domain != null) {
                logger.error("主题完整删除失败：指定课程ID不存在");
                return ResultUtil.error(ResultEnum.TOPIC_DELETE_ERROR_3.getCode(), ResultEnum.TOPIC_DELETE_ERROR_3.getMsg());
            }
            logger.info("开始删除 " + domain.getDomainName() + " 课程的主题依赖关系");
            dependencyRepository.deleteDependenciesByDomainId(domain.getDomainId());
            logger.info("删除 " + domain.getDomainName() + " 课程的主题依赖关系完成");
            logger.info("开始删除 " + domain.getDomainName() + " 课程的碎片");
            assembleRepository.deleteByDomainId(domain.getDomainId());
            logger.info("删除 " + domain.getDomainName() + " 课程碎片完成");
            logger.info("开始删除 " + domain.getDomainName() + " 课程的分面");
            List<Facet> facetList = facetRepository.findByDomainName(domain.getDomainName());
            for (int i = 0; i < facetList.size(); i++) {
                facetRepository.deleteByFacetId(facetList.get(i).getFacetId());
            }
            logger.info("删除 " + domain.getDomainName() + " 课程分面完成");
            logger.info("开始删除 " + domain.getDomainName() + " 课程的主题");
            topicRepository.deleteByDomainId(domain.getDomainId());
            logger.info("删除 " + domain.getDomainName() + " 课程主题完成，" + domain.getDomainName() + " 下的所有主题-分面-碎片已完全删除。");
            return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), "课程 " + domain.getDomainName() + " 下的所有主题-分面-碎片已完全删除。");
        } catch (Exception excepetion) {
            logger.error("错误：" + excepetion);
            logger.error("主题完整删除失败：删除语句执行失败");
            return ResultUtil.error(ResultEnum.TOPIC_DELETE_ERROR.getCode(), ResultEnum.TOPIC_DELETE_ERROR.getMsg());
        }
    }


    /**
     * 根据课程名，递归地依次删除该课程下的主题依赖关系、碎片、分面、主题，请谨慎操作！
     *
     * @param domainName
     * @return
     * @author Qi Jingchao
     */
    public Result deleteCompleteTopicByDomainName(String domainName) {
        try {
            Domain domain = domainRepository.findByDomainName(domainName);
            if (domain != null) {
                logger.error("主题完整删除失败：指定课程ID不存在");
                return ResultUtil.error(ResultEnum.TOPIC_DELETE_ERROR_3.getCode(), ResultEnum.TOPIC_DELETE_ERROR_3.getMsg());
            }
            logger.info("开始删除 " + domain.getDomainName() + " 课程的主题依赖关系");
            dependencyRepository.deleteDependenciesByDomainId(domain.getDomainId());
            logger.info("删除 " + domain.getDomainName() + " 课程的主题依赖关系完成");
            logger.info("开始删除 " + domain.getDomainName() + " 课程的碎片");
            assembleRepository.deleteByDomainId(domain.getDomainId());
            logger.info("删除 " + domain.getDomainName() + " 课程碎片完成");
            logger.info("开始删除 " + domain.getDomainName() + " 课程的分面");
            List<Facet> facetList = facetRepository.findByDomainName(domain.getDomainName());
            for (int i = 0; i < facetList.size(); i++) {
                facetRepository.deleteByFacetId(facetList.get(i).getFacetId());
            }
            logger.info("删除 " + domain.getDomainName() + " 课程分面完成");
            logger.info("开始删除 " + domain.getDomainName() + " 课程的主题");
            topicRepository.deleteByDomainId(domain.getDomainId());
            logger.info("删除 " + domain.getDomainName() + " 课程主题完成，" + domain.getDomainName() + " 下的所有主题-分面-碎片已完全删除。");
            return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), "课程 " + domain.getDomainName() + " 下的所有主题-分面-碎片已完全删除。");
        } catch (Exception excepetion) {
            logger.error("错误：" + excepetion);
            logger.error("主题完整删除失败：删除语句执行失败");
            return ResultUtil.error(ResultEnum.TOPIC_DELETE_ERROR.getCode(), ResultEnum.TOPIC_DELETE_ERROR.getMsg());
        }
    }


    /**
     * 依次递归删除所有课程已不存在的主题依赖、碎片、分面、主题（未完成）
     *
     * @return
     * @author Qi Jingchao
     */
    public Result deleteNonDomainTopicAndFacetAndAssembleAndDependency() {
        List<BigInteger> rawDomainIdList = assembleRepository.findDistinctDomainId();
        List<Long> fullDomainIdList = new ArrayList<>();
        for (int i = 0; i < rawDomainIdList.size(); i++) {
            Long temp = Long.valueOf(String.valueOf(rawDomainIdList.get(i)));
            fullDomainIdList.add(temp);
        }
//        List<Long> fullDomainIdList = assembleRepository.findDistinctDomainId();
        logger.info(fullDomainIdList.toString());
        List<Long> domainIdList = new ArrayList<>();
        for (int i = 0; i < fullDomainIdList.size(); i++) {
            if (fullDomainIdList.get(i) == null) {
                continue;
            }
//            Long currentDomain = Long.valueOf(fullDomainIdList.get(i));
//            logger.info(currentDomain.toString());
//            Domain domain = domainRepository.findByDomainId(Long.parseLong(fullDomainIdList.get(i).toString()));
            Domain domain = domainRepository.findByDomainId(fullDomainIdList.get(i));
            if (domain == null) {
                domainIdList.add(fullDomainIdList.get(i));
            }
        }
        logger.info("发现野生 domainId 如下：" + domainIdList.toString());
        for (int i = 0; i < domainIdList.size(); i++) {
            if (domainIdList.get(i) == null) {
                continue;
            } else {
                try {
                    Long domainId = domainIdList.get(i);
                    logger.info("开始删除课程ID：" + domainIdList.get(i) + "的主题依赖关系");
                    dependencyRepository.deleteDependenciesByDomainId(domainId);
                    logger.info("删除课程ID：" + domainIdList.get(i) + "的主题依赖关系完成");
                    logger.info("开始删除课程ID：" + domainIdList.get(i) + " 的碎片");
                    assembleRepository.deleteByDomainId(domainId);
                    logger.info("删除课程ID：" + domainIdList.get(i) + "的碎片完成");
                    logger.info("开始删除课程ID：" + domainIdList.get(i) + "的分面");
                    List<Facet> facetList = facetRepository.findByDomainId(domainId);
                    for (int j = 0; j < facetList.size(); j++) {
                        facetRepository.deleteByFacetId(facetList.get(j).getFacetId());
                    }
                    logger.info("删除课程ID：" + domainIdList.get(i) + "的分面完成");
                    logger.info("开始删除课程ID：" + domainIdList.get(i) + "的主题");
                    topicRepository.deleteByDomainId(domainId);
                    logger.info("删除课程ID：" + domainIdList.get(i) + "的主题完成");
                    logger.info("野生课程ID：" + domainIdList.get(i) + "的依赖、分面、主题已全部删除。");
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println(domainIdList);
        return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), domainIdList);
    }

    /**
     * 更新主题：根据主题名
     *
     * @param oldTopicName 旧主题名
     * @param newTopicName 新主题名
     * @param domainName   新课程名
     * @return 更新结果
     */
    public Result updateTopicByName(String oldTopicName, String newTopicName, String domainName) {
        if (newTopicName == null || newTopicName.equals("") || newTopicName.length() == 0) {
            logger.error("主题名更新失败：新主题名不存在或为空");
            return ResultUtil.error(ResultEnum.TOPIC_UPDATE_ERROR_1.getCode(), ResultEnum.TOPIC_UPDATE_ERROR_1.getMsg());
        }
        try {
            Domain domain = domainRepository.findByDomainName(domainName);
            if (domain == null) {
                logger.error("主题名更新失败：课程不存在");
                return ResultUtil.error(ResultEnum.TOPIC_UPDATE_ERROR_2.getCode(), ResultEnum.TOPIC_UPDATE_ERROR_2.getMsg());
            }
            List<Topic> topics = topicRepository.findByTopicName(oldTopicName);
            if (topics.size() < 1) {
                logger.error("主题名更新失败：原主题不存在");
                return ResultUtil.error(ResultEnum.TOPIC_UPDATE_ERROR_3.getCode(), ResultEnum.TOPIC_UPDATE_ERROR_3.getMsg());
            }
            topicRepository.updateByTopicName(oldTopicName, newTopicName, domain.getDomainId());
            return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), "主题名更新成功");
        } catch (Exception err) {
            logger.error("主题名更新失败：更新语句执行失败");
            return ResultUtil.error(ResultEnum.TOPIC_UPDATE_ERROR.getCode(), ResultEnum.TOPIC_UPDATE_ERROR.getMsg());
        }
    }

    /**
     * 查询主题：根据课程名
     *
     * @param domainName 新课程名
     * @return 查询结果
     */
    public Result findTopicsByDomainName(String domainName) {
        Domain domain = domainRepository.findByDomainName(domainName);
        if (domain == null) {
            logger.error("主题查询失败：没有指定课程");
            return ResultUtil.error(ResultEnum.TOPIC_SEARCH_ERROR_2.getCode(), ResultEnum.TOPIC_SEARCH_ERROR_2.getMsg());
        }
        Long domainId = domain.getDomainId();
        return findTopicsByDomainId(domainId);
    }


    /**
     * 查询主题：根据课程名
     * 根据主题筛选算法获取处理后的知识主题
     *
     * @param domainName
     * @return
     */
    public Result findSelectedTopicsByDomainName(String domainName) {
        Domain domain = domainRepository.findByDomainName(domainName);
        if (domain == null) {
            logger.error("主题查询失败：没有指定课程");
            return ResultUtil.error(ResultEnum.TOPIC_SEARCH_ERROR_2.getCode(), ResultEnum.TOPIC_SEARCH_ERROR_2.getMsg());
        }

        List<Topic> topicRaw = topicRepository.findByDomainName(domainName);
        List<Topic> results = null;

        if (topicRaw.size() <= 0) {
            logger.error("主题抽取失败：该课程主题数目为0");
            return ResultUtil.success(ResultEnum.TOPIC_SEARCH_ERROR_1.getCode(), ResultEnum.TOPIC_SEARCH_ERROR_1.getMsg() + "该课程主题数目为0", results);
        }

        TopicSelect topicSelect = new TopicSelect();
        results = topicSelect.filterAlgorithm(domain.getDomainId(), topicRaw);
        Map<Long, Integer> assembleCounts = topicDAO.countAssemblesByDomainIdGroupByTopicId(domain.getDomainId());

        // 获得topicContainAssembleText List，即每个主题对应的碎片文本，获得主题内容信息
        List<TopicContainAssembleText> topicContainAssembleTexts = new ArrayList<>();
        for (int i = 0; i < results.size(); i++) {
            Topic tmpTopic = results.get(i);
            TopicContainAssembleText tmpText = new TopicContainAssembleText(tmpTopic);
            tmpText.setTopicId(tmpTopic.getTopicId());

            //查询主题碎片信息
            List<Assemble> assembleList = assembleRepository.findAllAssemblesByTopicId(tmpTopic.getTopicId());
            if (assembleList.size() <= 0) {
                System.out.println("该主题没有碎片知识 - TopicId:" + tmpTopic.getTopicId());
                continue;
            }
            String text = "";
            for (int j = 0; j < assembleList.size(); j++) {
                text = text + assembleList.get(j).getAssembleText() + " ";
            }
            tmpText.setText(text);
            topicContainAssembleTexts.add(tmpText);
        }

        TopicExtraction topicExtraction = new TopicExtraction();
        // 对主题进行算法处理
        results = topicExtraction.extractAlgorithm(results, topicContainAssembleTexts, assembleCounts);

        if (results.size() <= 0) {
            logger.error("主题抽取失败：经算法抽取该课程主题数目为0");
            return ResultUtil.success(ResultEnum.TOPIC_SEARCH_ERROR_1.getCode(), ResultEnum.TOPIC_SEARCH_ERROR_1.getMsg() + "该课程主题数目为0", results);
        }

        logger.info(domainName+" 课程主题抽取成功");
        return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg() + "抽取到主题个数为" + results.size(), results);
    }

    /**
     * 查询主题：根据课程Id
     * 根据主题筛选算法获取处理后的知识主题
     *
     * @param domainId
     * @return
     */
    public Result findSelectedTopicsByDomainId(Long domainId) {
        Domain domain = domainRepository.findByDomainId(domainId);
        if (domain == null) {
            logger.error("主题查询失败：没有指定课程");
            return ResultUtil.error(ResultEnum.TOPIC_SEARCH_ERROR_2.getCode(), ResultEnum.TOPIC_SEARCH_ERROR_2.getMsg());
        }

        List<Topic> topicRaw = topicRepository.findByDomainId(domainId);
        List<Topic> results = null;
        if (topicRaw.size() <= 0) {
            logger.error("主题抽取失败：该课程主题数目为0");
            return ResultUtil.success(ResultEnum.TOPIC_SEARCH_ERROR_1.getCode(), ResultEnum.TOPIC_SEARCH_ERROR_1.getMsg() + "该课程主题数目为0", results);
        }

        TopicSelect topicSelect = new TopicSelect();
        results = topicSelect.filterAlgorithm(domain.getDomainId(), topicRaw);
        Map<Long, Integer> assembleCounts = topicDAO.countAssemblesByDomainIdGroupByTopicId(domainId);

        // 获得topicContainAssembleText List，即每个主题对应的碎片文本，获得主题内容信息
        List<TopicContainAssembleText> topicContainAssembleTexts = new ArrayList<>();
        for (int i = 0; i < results.size(); i++) {
            Topic tmpTopic = results.get(i);
            TopicContainAssembleText tmpText = new TopicContainAssembleText(tmpTopic);
            tmpText.setTopicId(tmpTopic.getTopicId());

            //查询主题碎片信息
            List<Assemble> assembleList = assembleRepository.findAllAssemblesByTopicId(tmpTopic.getTopicId());
            if (assembleList.size() <= 0) {
                System.out.println("该主题没有碎片知识 - TopicId:" + tmpTopic.getTopicId());
                continue;
            }
            String text = "";
            for (int j = 0; j < assembleList.size(); j++) {
                text = text + assembleList.get(j).getAssembleText() + " ";
            }
            tmpText.setText(text);
            topicContainAssembleTexts.add(tmpText);
        }

        TopicExtraction topicExtraction = new TopicExtraction();
        // 对主题进行算法处理
        results = topicExtraction.extractAlgorithm(results, topicContainAssembleTexts, assembleCounts);

        if (results.size() <= 0) {
            logger.error("主题抽取失败：经算法抽取该课程主题数目为0");
            return ResultUtil.success(ResultEnum.TOPIC_SEARCH_ERROR_1.getCode(), ResultEnum.TOPIC_SEARCH_ERROR_1.getMsg() + "该课程主题数目为0", results);
        }

        logger.info("课程主题抽取成功");
        return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg() + "抽取到主题个数为" + results.size(), results);
    }

    /**
     * 查询主题：根据课程id
     *
     * @param domainId
     * @return
     */
    public Result findTopicsByDomainId(Long domainId) {
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
        try {
            return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), results);
        } catch (Exception error) {
            logger.error("主题查询失败：" + error);
            return ResultUtil.error(ResultEnum.TOPIC_SEARCH_ERROR_1.getCode(), ResultEnum.TOPIC_SEARCH_ERROR_1.getMsg());
        }
    }

    /**
     * 指定课程名和主题名，获取主题并包含其完整的下的分面、碎片数据
     *
     * @param domainName
     * @param topicName
     * @param hasFragment
     * @return
     */
    public Result findCompleteTopicByNameAndDomainName(String domainName, String topicName, String hasFragment) {
        Domain domain = domainRepository.findByDomainName(domainName);
        if (domain == null) {
            logger.error("主题查询失败：没有指定课程");
            return ResultUtil.error(ResultEnum.TOPIC_SEARCH_ERROR_2.getCode(), ResultEnum.TOPIC_SEARCH_ERROR_2.getMsg());
        }
        Topic topic = topicRepository.findByDomainIdAndTopicName(domain.getDomainId(), topicName);
        if (topic == null) {
            logger.error("主题查询失败：没有指定主题");
            return ResultUtil.error(ResultEnum.TOPIC_SEARCH_ERROR.getCode(), ResultEnum.TOPIC_SEARCH_ERROR.getMsg());

        }
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
        return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), topicContainFacet);
    }

    /**
     * 指定课程名和主题名，获取主题并包含其完整的下的分面、不包含碎片数据
     *
     * @param domainName 课程名
     * @param topicName  主题名
     * @return
     */
    public Result findCompleteTopicByNameAndDomainNameWithoutAssemble(String domainName, String topicName) {
        Domain domain = domainRepository.findByDomainName(domainName);
        if (domain == null) {
            logger.error("主题查询失败：没有指定课程");
            return ResultUtil.error(ResultEnum.TOPIC_SEARCH_ERROR_2.getCode(), ResultEnum.TOPIC_SEARCH_ERROR_2.getMsg());
        }
        Topic topic = topicRepository.findByDomainIdAndTopicName(domain.getDomainId(), topicName);
        if (topic == null) {
            logger.error("主题查询失败：没有指定主题");
            return ResultUtil.error(ResultEnum.TOPIC_SEARCH_ERROR.getCode(), ResultEnum.TOPIC_SEARCH_ERROR.getMsg());
        }
        List<Facet> facets = facetRepository.findByTopicIdAndFacetLayer(topic.getTopicId(), 1);
        //初始化Topic
        TopicContainFacet topicContainFacet = new TopicContainFacet();
        topicContainFacet.setTopic(topic);

        //firstLayerFacets一级分面列表，将二级分面挂到对应一级分面下
        List<Facet> firstLayerFacets = new ArrayList<>();
        for (Facet facet : facets) {
            if (facet.getFacetName().equals("匿名分面")) {
                continue;
            }
            //设置一级分面
            FacetContainAssemble firstLayerFacet = new FacetContainAssemble();
            firstLayerFacet.setFacet(facet);

            //如果存在二级分面，设置一级分面下的二级分面
            List<Facet> secondLayerFacets = facetRepository.findByParentFacetId(facet.getFacetId());
            if (secondLayerFacets.size() > 0) {
                firstLayerFacet.setContainChildrenFacet(true);
                List<Object> secondLayerFacetContainAssembles = new ArrayList<>();
                for (Facet secondLayerFacet : secondLayerFacets) {
                    FacetContainAssemble secondLayerFacetContainAssemble = new FacetContainAssemble();
                    secondLayerFacetContainAssemble.setFacet(secondLayerFacet);
                    secondLayerFacetContainAssembles.add(secondLayerFacetContainAssemble);
                }
                firstLayerFacet.setChildren(secondLayerFacetContainAssembles);
                firstLayerFacet.setChildrenNumber(secondLayerFacets.size());
            }
            firstLayerFacets.add(firstLayerFacet);
        }
        topicContainFacet.setChildren(firstLayerFacets);
        topicContainFacet.setChildrenNumber(firstLayerFacets.size());
        return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), topicContainFacet);
    }

    /**
     * 获得指定课程第一个主题的所有信息,用于构建分面树
     *
     * @param domainName
     * @return
     */
    public Result findFirstTopicByDomainName(String domainName) {
        Domain domain = domainRepository.findByDomainName(domainName);
        if (domain == null) {
            logger.error("主题查询失败：没有指定课程");
            return ResultUtil.error(ResultEnum.TOPIC_SEARCH_ERROR_2.getCode(), ResultEnum.TOPIC_SEARCH_ERROR_2.getMsg());
        }
        Long domainId = domain.getDomainId();
        Topic topic = topicRepository.findFirstByDomainId(domainId);
        return findCompleteTopicByNameAndDomainName(domainName, topic.getTopicName(), "true");
    }

    /**
     * 查询指定课程、主题下的，主题信息、以及分面统计信息
     *
     * @param domainName
     * @param topicName
     * @return
     */
    public Result findTopicInformationByDomainNameAndTopicName(String domainName, String topicName) {
        Domain domain = domainRepository.findByDomainName(domainName);
        if (domain == null) {
            logger.error("主题查询失败：没有指定课程");
            return ResultUtil.error(ResultEnum.TOPIC_SEARCH_ERROR_2.getCode(), ResultEnum.TOPIC_SEARCH_ERROR_2.getMsg());
        }
        //查询主题
        Topic topic = topicRepository.findByDomainIdAndTopicName(domain.getDomainId(), topicName);
        if (topic == null) {
            logger.error("主题查询失败：没有指定主题");
            return ResultUtil.error(ResultEnum.TOPIC_SEARCH_ERROR.getCode(), ResultEnum.TOPIC_SEARCH_ERROR.getMsg());
        }
        Long topicId = topic.getTopicId();
        //查询一级分面
        Integer firstLayerFacetNumber = facetRepository.countByTopicIdAndFacetLayer(topicId, 1);
        //查询二级分面
        Integer secondLayerFacetNumber = facetRepository.countByTopicIdAndFacetLayer(topicId, 2);
        //查询三级分面
        Integer thirdLayerFacetNumber = facetRepository.countByTopicIdAndFacetLayer(topicId, 3);
        //查询碎片
        Integer assembleNumber = assembleRepository.countByTopicId(topicId);
        Map<String, Object> topicInformation = new HashMap<>(10);
        topicInformation.put("topicId", topic.getTopicId());
        topicInformation.put("topicName", topicName);
        topicInformation.put("topicUrl", topic.getTopicUrl());
        topicInformation.put("topicLayer", topic.getTopicLayer());
        topicInformation.put("domainId", domain.getDomainId());
        topicInformation.put("domainName", domainName);
        topicInformation.put("firstLayerFacetNumber", firstLayerFacetNumber);
        topicInformation.put("secondLayerFacetNumber", secondLayerFacetNumber);
        topicInformation.put("thirdLayerFacetNumber", thirdLayerFacetNumber);
        topicInformation.put("facetNumber", firstLayerFacetNumber
                + secondLayerFacetNumber
                + thirdLayerFacetNumber);
        topicInformation.put("assembleNumber", assembleNumber);
        return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), topicInformation);
    }

    /**
     * 根据主题名，获得主题的所有信息，用于构建分面树
     *
     * @param topicName
     * @param hasFragment
     * @return
     */
    public Result getCompleteTopicByTopicName(String topicName, String hasFragment) {
        List<Topic> topicList = topicRepository.findByTopicName(topicName);
        if (topicList.size() == 0) {
            logger.error("主题查询失败：没有指定主题");
            return ResultUtil.error(ResultEnum.TOPIC_SEARCH_ERROR.getCode(), ResultEnum.TOPIC_SEARCH_ERROR.getMsg());
        }

        Topic topic = topicList.get(0);
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

        return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), topicContainFacet);
    }

    public List<Topic> getTopicList(Long domainId) {
        return topicRepository.findByDomainId(domainId);
    }

    public static void main(String[] args) {
        TopicService topicService = new TopicService();
        topicService.deleteTopicByNameAndDomainName("诺基亚操作系统", "Java");
    }

    public Result findTopicNameBeforeAndAfterFilter(String domainName) {
        List<String> originalTopicNameList=new ArrayList<>();
        List<String> topicNameListAfterFilter=new ArrayList<>();


        Map<String,Object> topicNameMap=new LinkedHashMap<>();


        Domain domain = domainRepository.findByDomainName(domainName);
        if (domain == null) {
            logger.error("主题查询失败：没有指定课程");
            return ResultUtil.error(ResultEnum.TOPIC_SEARCH_ERROR_2.getCode(), ResultEnum.TOPIC_SEARCH_ERROR_2.getMsg());
        }

        Long domainId = domain.getDomainId();
        List<Topic> topicList = topicRepository.findByDomainId(domainId);

        if(topicList==null || topicList.size()==0){
            return ResultUtil.error(ResultEnum.TOPIC_SEARCH_ERROR_1.getCode(), ResultEnum.TOPIC_SEARCH_ERROR_1.getMsg());
        }
        for(Topic topic:topicList){
            String topicName = topic.getTopicName();
            originalTopicNameList.add(topicName);
        }

        Result result = findSelectedTopicsByDomainName(domainName);
        List<Topic> topicListAfterFilter = (List<Topic>)result.getData();

        for(Topic topic :topicListAfterFilter){
            topicNameListAfterFilter.add(topic.getTopicName());
        }

        originalTopicNameList.removeAll(topicNameListAfterFilter);
        topicNameMap.put("domainName:",domainName);
        topicNameMap.put("totalNum:",topicList.size());
        topicNameMap.put("filterNum:",originalTopicNameList.size());
        topicNameMap.put("Filter:",originalTopicNameList);
        topicNameMap.put("remainNum:",topicNameListAfterFilter.size());
        topicNameMap.put("Remain:",topicNameListAfterFilter);

        String fileName = "C:\\Users\\马昆明\\Desktop\\topic\\"+domainName+".json";
        ObjectMapper mapper = new ObjectMapper();
        String s="";

        s=s+"domainName: "+domainName+"\n";
        s=s+"totalNum: "+topicList.size()+"\n";
        s=s+"filterNum: "+originalTopicNameList.size()+"\n";
        s=s+"Filter: {\n";
        for(String name :originalTopicNameList){
            s=s+"\t"+name+", \n";
        }
        s=s+"}\n";
        s=s+"remainNum: "+topicNameListAfterFilter.size()+"\n";
        s=s+"Remain: {\n";
        for(String name :topicNameListAfterFilter){
            s=s+"\t"+name+", \n";
        }
        s=s+"}\n";
        Path path = Paths.get(fileName);
        // 使用newBufferedWriter创建文件并写文件
        // 这里使用了try-with-resources方法来关闭流，不用手动关闭
        try (BufferedWriter writer =
                     Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            writer.write(s);
        } catch (IOException e) {
            e.printStackTrace();
        }


        return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), topicNameMap);
    }

    public Result filterTopicsByDomainName(String domainName) {
        Domain domain = domainRepository.findByDomainName(domainName);
        if (domain == null) {
            logger.error("主题查询失败：没有指定课程");
            return ResultUtil.error(ResultEnum.TOPIC_SEARCH_ERROR_2.getCode(), ResultEnum.TOPIC_SEARCH_ERROR_2.getMsg());
        }

        List<Topic> topicRaw = topicRepository.findByDomainName(domainName);
        List<Topic> results = null;

        if (topicRaw.size() <= 0) {
            logger.error("主题抽取失败：该课程主题数目为0");
            return ResultUtil.success(ResultEnum.TOPIC_SEARCH_ERROR_1.getCode(), ResultEnum.TOPIC_SEARCH_ERROR_1.getMsg() + "该课程主题数目为0", results);
        }

        List<Topic> originalTopicNameList=new ArrayList<>();
        for(Topic topic:topicRaw){
            originalTopicNameList.add(topic);
        }

        Result result = findSelectedTopicsByDomainName(domainName);
        List<Topic> topicListAfterFilter = (List<Topic>)result.getData();


        // 剩下的就是被过滤掉的，也是需要删除的
        originalTopicNameList.removeAll(topicListAfterFilter);
        for(Topic topic :originalTopicNameList){
            Result result1 = deleteTopicCompleteByDomainNameAndTopicId(domainName, topic.getTopicId());
            if (!result1.getCode().equals(ResultEnum.SUCCESS.getCode())) {
                return ResultUtil.error(ResultEnum.TOPIC_DELETE_ERROR.getCode(), ResultEnum.TOPIC_DELETE_ERROR.getMsg());
            }
        }
        Map<String, Object> topicNameMap=new LinkedHashMap<>();
        topicNameMap.put("domainName:",domainName);
        topicNameMap.put("totalNum:",topicRaw.size());
        topicNameMap.put("filterNum:",originalTopicNameList.size());
        topicNameMap.put("Filter:",originalTopicNameList);
        topicNameMap.put("remainNum:",topicListAfterFilter.size());
        topicNameMap.put("Remain:",topicListAfterFilter);
        return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), topicNameMap);
    }

    public Result getTopicNumByDomainName(String domainName) {
        Domain domain = domainRepository.findByDomainName(domainName);
        if(domain==null){
            logger.error("主题查询失败：没有指定课程");
            return ResultUtil.error(ResultEnum.TOPIC_SEARCH_ERROR_2.getCode(), ResultEnum.TOPIC_SEARCH_ERROR_2.getMsg());
        }
        Integer topicNum = topicRepository.countByDomainId(domain.getDomainId());
        return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), topicNum);
    }

    public Result getTopicListByTopicNumLargerThanNum(Integer num) {
        List<Domain> domains = domainRepository.findAll();
        if(domains==null || domains.size()==0){
            logger.error("课程查询失败：课程列表为空");
            return ResultUtil.error(ResultEnum.DOMAIN_SEARCH_ERROR_4.getCode(), ResultEnum.DOMAIN_SEARCH_ERROR_4.getMsg());
        }
        List<Domain> domainList=new ArrayList<>();
        for(Domain domain:domains){
            Integer topicNum = topicRepository.countByDomainId(domain.getDomainId());
            if(topicNum>num){
                domainList.add(domain);
            }
        }
        return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), domainList);

    }

    public Result deleteNeedlessTopicByDomainName(String domainName, Integer num) {
        Domain domain = domainRepository.findByDomainName(domainName);
        if(domain==null){
            logger.error("课程查询失败：课程列表为空");
            return ResultUtil.error(ResultEnum.DOMAIN_SEARCH_ERROR.getCode(), ResultEnum.DOMAIN_SEARCH_ERROR.getMsg());
        }
        List<Topic> topicList = topicRepository.findByDomainId(domain.getDomainId());
        List<Topic> deleteTopicList=new ArrayList<>();
        for (int i = 0; i < topicList.size(); i++) {
            if(i>=num){
                Topic topic = topicList.get(i);
                Result result = deleteTopicCompleteByDomainNameAndTopicId(domainName, topic.getTopicId());
                if (result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
                    deleteTopicList.add(topic);
                }
            }
        }

        return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), deleteTopicList);
    }

    public Result deleteDuplicatedTopicCompleteByDomainName(String domainName) {
        Domain domain = domainRepository.findByDomainName(domainName);
        if(domain==null){
            logger.error("课程查询失败：课程列表为空");
            return ResultUtil.error(ResultEnum.DOMAIN_SEARCH_ERROR.getCode(), ResultEnum.DOMAIN_SEARCH_ERROR.getMsg());
        }
        List<Topic> topicList = topicRepository.findByDomainId(domain.getDomainId());
        Collections.sort(topicList,Comparator.comparing(Topic::getTopicName));
        List<String> deleteTopicList=new ArrayList<>();
        for (int i = 0; i < topicList.size()-1; i++) {
            Topic topic = topicList.get(i);
            Topic nextTopic = topicList.get(i+1);
            if(topic.getTopicName().equalsIgnoreCase(nextTopic.getTopicName())){
                deleteTopicCompleteByDomainNameAndTopicId(domainName,topic.getTopicId());
                deleteTopicList.add(topic.getTopicName());
            }
        }
        return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), deleteTopicList);

    }

    private Result deleteTopicCompleteByDomainNameAndTopicEntity(String domainName, Topic topic) {
        Domain domain = domainRepository.findByDomainName(domainName);
        if (domain == null) {
            logger.error("主题删除失败：指定课程不存在");
            return ResultUtil.error(ResultEnum.TOPIC_DELETE_ERROR_1.getCode(), ResultEnum.TOPIC_DELETE_ERROR_1.getMsg());
        }
        if (topic == null) {
            logger.error("主题删除失败：此课程下不存在该主题");
            return ResultUtil.error(ResultEnum.TOPIC_DELETE_ERROR_2.getCode(), ResultEnum.TOPIC_DELETE_ERROR_2.getMsg());
        }
        String topicName=topic.getTopicName();
        try{
            Long topicId = topic.getTopicId();
            logger.info("开始删除 " + domain.getDomainName() + " 课程下 " + topicName + " 主题的依赖关系");
            dependencyRepository.deleteByStartTopicIdOrEndTopicId(topicId, topicId);
            logger.info("删除 " + domain.getDomainName() + " 课程下 " + topicName + " 主题的依赖关系完成");
            logger.info("开始删除 " + topicName + " 主题的碎片");
            assembleRepository.deleteByTopicId(topicId);
            logger.info("删除 " + topicName + " 主题的碎片完成");
            logger.info("开始删除 " + topicName + " 主题的分面");
            facetRepository.deleteByTopicId(topicId);
            logger.info("删除 " + topicName + " 主题的分面完成");
            logger.info("开始删除该主题信息");
            topicRepository.deleteByDomainIdAndTopicId(domain.getDomainId(), topicId);
            logger.info("删除该主题信息完成！" + topicName + " 主题下的所有主题依赖关系-分面-碎片已完全删除。");
            return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), "主题 " + topicName + " 下的所有主题依赖关系-分面-碎片已完全删除。");
        } catch (Exception excepetion) {
            logger.error("错误：" + excepetion);
            logger.error("主题完整删除失败：删除语句执行失败");
            return ResultUtil.error(ResultEnum.TOPIC_DELETE_ERROR.getCode(), ResultEnum.TOPIC_DELETE_ERROR.getMsg());
        }
    }

    public Result deleteTopicCompleteByDomainNameAndTopicId(String domainName, Long topicId) {
        Domain domain = domainRepository.findByDomainName(domainName);
        if (domain == null) {
            logger.error("主题删除失败：指定课程不存在");
            return ResultUtil.error(ResultEnum.TOPIC_DELETE_ERROR_1.getCode(), ResultEnum.TOPIC_DELETE_ERROR_1.getMsg());
        }
        Topic topic = topicRepository.findByTopicId(topicId);
        if (topic == null) {
            logger.error("主题删除失败：此课程下不存在该主题");
            return ResultUtil.error(ResultEnum.TOPIC_DELETE_ERROR_2.getCode(), ResultEnum.TOPIC_DELETE_ERROR_2.getMsg());
        }
        try{
            String topicName = topic.getTopicName();
            logger.info("开始删除 " + domain.getDomainName() + " 课程下 " + topicName + " 主题的依赖关系");
            dependencyRepository.deleteByStartTopicIdOrEndTopicId(topicId, topicId);
            logger.info("删除 " + domain.getDomainName() + " 课程下 " + topicName + " 主题的依赖关系完成");
            logger.info("开始删除 " + topicName + " 主题的碎片");
            assembleRepository.deleteByTopicId(topicId);
            logger.info("删除 " + topicName + " 主题的碎片完成");
            logger.info("开始删除 " + topicName + " 主题的分面");
            facetRepository.deleteByTopicId(topicId);
            logger.info("删除 " + topicName + " 主题的分面完成");
            logger.info("开始删除该主题信息");
            topicRepository.deleteByDomainIdAndTopicId(domain.getDomainId(), topicId);
            logger.info("删除该主题信息完成！" + topicName + " 主题下的所有主题依赖关系-分面-碎片已完全删除。");
            return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), "主题 " + topicName + " 下的所有主题依赖关系-分面-碎片已完全删除。");
        } catch (Exception excepetion) {
            logger.error("错误：" + excepetion);
            logger.error("主题完整删除失败：删除语句执行失败");
            return ResultUtil.error(ResultEnum.TOPIC_DELETE_ERROR.getCode(), ResultEnum.TOPIC_DELETE_ERROR.getMsg());
        }
    }

    public Result insertNewCompleteTopicByNameAndDomainName(String topicName, String domainName,boolean isChinese) {

        Domain domain = domainRepository.findByDomainName(domainName);
        if (domain == null) {
            logger.error("主题信息插入失败：没有对应的课程");
            return ResultUtil.error(ResultEnum.TOPIC_INSERT_ERROR_3.getCode(), ResultEnum.TOPIC_INSERT_ERROR_3.getMsg());
        }

        Result insertTopicResult = insertTopicByNameAndDomainName(topicName, domainName);
        if(!insertTopicResult.getCode().equals(ResultEnum.SUCCESS.getCode())){
            return ResultUtil.error(ResultEnum.TOPIC_INSERT_ERROR_4.getCode(), ResultEnum.TOPIC_INSERT_ERROR_4.getMsg());
        }

        Topic topic = topicRepository.findByDomainIdAndTopicName(domain.getDomainId(), topicName);
        try {
            Result result = spiderService.crawlEmptyTopic(topic.getTopicId(), isChinese);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), "ok");
    }
}