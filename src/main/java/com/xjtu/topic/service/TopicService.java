package com.xjtu.topic.service;

import com.xjtu.assemble.domain.Assemble;
import com.xjtu.assemble.domain.AssembleContainType;
import com.xjtu.assemble.repository.AssembleRepository;
import com.xjtu.common.domain.Result;
import com.xjtu.common.domain.ResultEnum;
import com.xjtu.dependency.repository.DependencyRepository;
import com.xjtu.domain.domain.Domain;
import com.xjtu.domain.repository.DomainRepository;
import com.xjtu.facet.domain.Facet;
import com.xjtu.facet.domain.FacetContainAssemble;
import com.xjtu.facet.repository.FacetRepository;
import com.xjtu.relation.domain.Relation;
import com.xjtu.relation.repository.RelationRepository;
import com.xjtu.topic.domain.Topic;
import com.xjtu.topic.domain.TopicContainFacet;
import com.xjtu.topic.repository.TopicRepository;
import com.xjtu.utils.ResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Value("${gexfpath}")
    private String gexfPath;

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
            //删除主题表中的对应主题
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
     * 更新主题：根据主题名
     *
     * @param oldTopicName  旧主题名
     * @param newTopicName  新主题名
     * @param newDomainName 新课程名
     * @return 更新结果
     */
    public Result updateTopicByName(String oldTopicName, String newTopicName, String newDomainName) {
        if (newTopicName == null || newTopicName.equals("") || newTopicName.length() == 0) {
            logger.error("主题名更新失败：新主题名不存在或为空");
            return ResultUtil.error(ResultEnum.TOPIC_UPDATE_ERROR_1.getCode(), ResultEnum.TOPIC_UPDATE_ERROR_1.getMsg());
        }
        try {
            Domain domain = domainRepository.findByDomainName(newDomainName);
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
        List<Topic> topics = topicRepository.findByDomainId(domain.getDomainId());
        try {
            return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), topics);
        } catch (Exception error) {
            logger.error("主题查询失败：" + error);
            return ResultUtil.error(ResultEnum.TOPIC_SEARCH_ERROR_1.getCode(), ResultEnum.TOPIC_SEARCH_ERROR_1.getMsg());
        }
    }

    /**
     * 指定课程名和主题名，获取主题并包含其完整的下的分面、碎片数据
     * @param domainName
     * @param topicName
     * @return
     */
    public Result findCompleteTopicByNameAndDomainName(String domainName, String topicName) {
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
                            assembleContainType.setAssemble(assemble);
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
                        assembleContainType.setAssemble(assemble);
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
        return findCompleteTopicByNameAndDomainName(domainName, topic.getTopicName());
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
        List<Facet> firstLayerFacets = facetRepository.findByTopicIdAndFacetLayer(topicId, 1);
        //查询二级分面
        List<Facet> secondLayerFacets = facetRepository.findByTopicIdAndFacetLayer(topicId, 2);
        //查询三级分面
        List<Facet> thirdLayerFacets = facetRepository.findByTopicIdAndFacetLayer(topicId, 3);
        Map<String, Object> topicInformation = new HashMap<>(10);
        topicInformation.put("topicId", topic.getTopicId());
        topicInformation.put("topicName", topicName);
        topicInformation.put("topicUrl", topic.getTopicUrl());
        topicInformation.put("topicLayer", topic.getTopicLayer());
        topicInformation.put("domainId", domain.getDomainId());
        topicInformation.put("domainName", domainName);
        topicInformation.put("firstLayerFacetNumber", firstLayerFacets.size());
        topicInformation.put("secondLayerFacetNumber", secondLayerFacets.size());
        topicInformation.put("thirdLayerFacetNumber", thirdLayerFacets.size());
        topicInformation.put("facetNumber", firstLayerFacets.size()
                + secondLayerFacets.size()
                + thirdLayerFacets.size());
        return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), topicInformation);
    }

    public static void main(String[] args) {
        TopicService topicService = new TopicService();
        topicService.deleteTopicByNameAndDomainName("诺基亚操作系统", "Java");
    }
}