package com.xjtu.education.service;

import com.xjtu.common.domain.Result;
import com.xjtu.common.domain.ResultEnum;
import com.xjtu.domain.domain.Domain;
import com.xjtu.domain.repository.DomainRepository;
import com.xjtu.education.domain.TopicState;
import com.xjtu.education.repository.TopicStateRepository;
import com.xjtu.topic.domain.Topic;
import com.xjtu.topic.repository.TopicRepository;
import com.xjtu.utils.ResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 推荐方式服务层
 *
 * @author yangkuan
 */
@Service
public class TopicStateService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    TopicStateRepository topicStateRepository;

    @Autowired
    DomainRepository domainRepository;

    @Autowired
    TopicRepository topicRepository;

    /**
     * 保存主题状态
     *
     * @param domainId
     * @param states
     * @param userId
     * @return
     */
    public Result saveState(Long domainId, String states
            , Long userId) {
        TopicState topicState = topicStateRepository
                .findByDomainIdAndUserId(domainId, userId);
        if (topicState == null) {
            topicState = new TopicState();
            topicState.setDomainId(domainId);
            topicState.setStates(states);
            topicState.setUserId(userId);
            topicState.setCreatedTime(new Date());
            topicState.setModifiedTime(new Date());
            topicStateRepository.save(topicState);
        } else {
            topicStateRepository.updateByDomainIdAndUserId(domainId
                    , userId
                    , states
                    , new Date());
        }
        return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), "主题状态保存成功");
    }

    /**
     * 保存主题状态
     *
     * @param domainName
     * @param states
     * @param userId
     * @return
     */
    public Result saveState(String domainName, String states
            , Long userId) {
        Domain domain = domainRepository.findByDomainName(domainName);
        if (domain == null) {
            logger.error("保存失败:课程不存在");
            return ResultUtil.error(ResultEnum.STATE_INSERT_ERROR.getCode(), ResultEnum.STATE_INSERT_ERROR.getMsg());
        }
        return saveState(domain.getDomainId()
                , states
                , userId);
    }

    /**
     * 查询主题状态
     *
     * @param domainId
     * @param userId
     * @return
     */
    public Result findByDomainIdAndUserId(Long domainId, Long userId) {
        TopicState topicState = topicStateRepository
                .findByDomainIdAndUserId(domainId, userId);
        if (topicState == null) {
            logger.error("状态查询失败:指定课程和用户下不存在状态");
            return ResultUtil.error(ResultEnum.STATE_SEARCH_ERROR_4.getCode(), ResultEnum.STATE_SEARCH_ERROR_4.getMsg());

        }
        return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), topicState);
    }

    /**
     * 查询主题状态,按主题Id组织返回
     *
     * @param domainId
     * @param userId
     * @return
     */
    public Result findByDomainIdAndUserIdGroupTopicId(Long domainId, Long userId) {
        TopicState topicState = topicStateRepository
                .findByDomainIdAndUserId(domainId, userId);
        if (topicState == null) {
            logger.error("状态查询失败:指定课程和用户下不存在状态");
            return ResultUtil.error(ResultEnum.STATE_SEARCH_ERROR_4.getCode(), ResultEnum.STATE_SEARCH_ERROR_4.getMsg());

        }
        String[] states = topicState.getStates().split(",");

        List<Topic> topics = topicRepository.findByDomainId(domainId);
        if (topics.size() != states.length) {
            logger.error("状态查询失败:课程下主题和主题状态不一致（请咨询数据管理员）");
            return ResultUtil.error(ResultEnum.STATE_SEARCH_ERROR_3.getCode(), ResultEnum.STATE_SEARCH_ERROR_3.getMsg());
        }
        int size = topics.size();
        List<Map<String, Object>> result = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            Map<String, Object> r = new HashMap<>(2);
            r.put("topicId", topics.get(i).getTopicId());
            r.put("topicName", topics.get(i).getTopicName());
            r.put("state", states[i]);
            result.add(r);
        }
        return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), result);
    }
}
