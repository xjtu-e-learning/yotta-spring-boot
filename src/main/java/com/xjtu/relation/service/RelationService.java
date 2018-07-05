package com.xjtu.relation.service;

import com.xjtu.common.domain.Result;
import com.xjtu.common.domain.ResultEnum;
import com.xjtu.domain.domain.Domain;
import com.xjtu.domain.repository.DomainRepository;
import com.xjtu.relation.domain.HyponymyRelation;
import com.xjtu.relation.domain.Relation;
import com.xjtu.relation.repository.RelationRepository;
import com.xjtu.topic.domain.Topic;
import com.xjtu.topic.repository.TopicRepository;
import com.xjtu.utils.ResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 处理主题上下位关系
 *
 * @author yangkuan
 * @date 2018/03/09 18:10
 */

@Service
public class RelationService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DomainRepository domainRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private RelationRepository relationRepository;

    /**
     * 通过课程名，构建一门课程的上下位关系
     *
     * @param domainName 课程名
     * @return Result
     */
    public Result findHyponymyRelationByDomainName(String domainName) {

        Domain domain = domainRepository.findByDomainName(domainName);
        if (domain == null) {
            logger.error("课程查询失败：没有课程信息记录");
            return ResultUtil.error(ResultEnum.DOMAIN_SEARCH_ERROR.getCode(), ResultEnum.DOMAIN_SEARCH_ERROR.getMsg());
        }
        //获取课程id
        Long domainId = domain.getDomainId();
        //查询课程下的所有主题
        List<Topic> topics = topicRepository.findByDomainId(domainId);

        List<Relation> relations = relationRepository.findByDomainId(domainId);

        //查找到第一层的主题，也就是那些只在父id中出现，没有在子id中出现的id
        //创建为第一层的主题上下位关系的对象
        List<HyponymyRelation> firstLayerTopics = findFirstLayerTopics(relations);

        //寻找第一层主题的下位主题
        for (HyponymyRelation firstLayerTopic : firstLayerTopics) {
            logger.error(firstLayerTopic.getName());
            firstLayerTopic.setChildren(findInferiorTopic(firstLayerTopic, relations));
        }
        //把第一层主题放到课程下面
        HyponymyRelation Topdomain = new HyponymyRelation();
        //设置课程id为0
        Topdomain.setId(new Long(0));
        //设置课程名
        Topdomain.setName(domainName);
        //设置一级主题
        Topdomain.setChildren(firstLayerTopics);
        return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), Topdomain);
    }

    /**
     * 查找到第一层的主题，也就是那些只在父id中出现，没有在子id中出现的id
     * 创建为第一层的主题上下位关系的对象
     *
     * @param relations
     * @return List<HyponymyRelation>
     */
    private List<HyponymyRelation> findFirstLayerTopics(List<Relation> relations) {
        List<Long> childTopicIds = new ArrayList<>();
        List<Long> parentTopicIds = new ArrayList<>();
        for (Relation relation : relations) {
            childTopicIds.add(relation.getChildTopicId());
            parentTopicIds.add(relation.getParentTopicId());
        }
        List<HyponymyRelation> hyponymyRelations = new ArrayList<>();
        for (Long parentTopicId : parentTopicIds) {
            if (!childTopicIds.contains(parentTopicId)) {
                //上下位关系
                HyponymyRelation hyponymyRelation = new HyponymyRelation();
                //设id
                hyponymyRelation.setId(parentTopicId);
                //设name
                String parentTopicName = topicRepository.findOne(parentTopicId).getTopicName();
                hyponymyRelation.setName(parentTopicName);
                hyponymyRelations.add(hyponymyRelation);
            }
        }
        return hyponymyRelations;
    }

    /**
     * 寻找主题的下位主题
     *
     * @param hyponymyRelation
     * @param relations
     * @return List<HyponymyRelation>
     */
    private List<HyponymyRelation> findInferiorTopic(HyponymyRelation hyponymyRelation, List<Relation> relations) {
        //获取上位主题的id
        Long topicId = hyponymyRelation.getId();

        //设置终止条件，如果关系列表的父主题id中不存在则终止
        boolean isEnd = true;
        for (Relation relation : relations) {
            if (relation.getParentTopicId().equals(topicId)) {
                isEnd = false;
                break;
            }
        }
        //防止递归出现，即a主题是b主题的上位，b又是a的上位

        //设置终止条件，如果关系列表的父主题id中不存在则终止
        if (isEnd) {
            return null;
        }
        //通过上位主题的id,找到对应的下位主题
        List<HyponymyRelation> children = new ArrayList<>();
        for (Relation relation : relations) {
            //找到对应下位主题，并防止下位主题等于上位主题
            if (relation.getParentTopicId().equals(topicId) && !relation.getChildTopicId().equals(topicId)) {
                HyponymyRelation child = new HyponymyRelation();
                //下位主题id
                Long childTopicId = relation.getChildTopicId();
                //设id
                child.setId(childTopicId);
                //设name
                String childTopicName = topicRepository.findOne(childTopicId).getTopicName();
                child.setName(childTopicName);
                //设children（递归）
                child.setChildren(findInferiorTopic(child, relations));
                //添加child至children
                children.add(child);
            }
        }
        return children;
    }
}
