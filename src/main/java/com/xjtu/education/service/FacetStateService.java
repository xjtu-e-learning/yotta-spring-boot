package com.xjtu.education.service;

import com.xjtu.common.domain.Result;
import com.xjtu.common.domain.ResultEnum;
import com.xjtu.domain.domain.Domain;
import com.xjtu.domain.repository.DomainRepository;
import com.xjtu.education.domain.FacetState;
import com.xjtu.education.repository.FacetStateRepository;
import com.xjtu.facet.domain.Facet;
import com.xjtu.facet.repository.FacetRepository;
import com.xjtu.topic.domain.Topic;
import com.xjtu.topic.repository.TopicRepository;
import com.xjtu.utils.ResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author yangkuan
 */
@Service
public class FacetStateService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    FacetStateRepository facetStateRepository;

    @Autowired
    DomainRepository domainRepository;

    @Autowired
    TopicRepository topicRepository;

    @Autowired
    FacetRepository facetRepository;

    /**
     * 保存分面状态
     *
     * @param domainId
     * @param topicId
     * @param states
     * @param userId
     * @return
     */
    public Result saveState(Long domainId, Long topicId,
                            String states, Long userId) {
        FacetState facetState = facetStateRepository.findByDomainIdAndTopicIdAndUserId(domainId, topicId, userId);
        if (facetState == null) {
            facetState = new FacetState();
            facetState.setDomainId(domainId);
            facetState.setTopicId(topicId);
            facetState.setStates(states);
            facetState.setUserId(userId);
            facetState.setCreatedTime(new Date());
            facetState.setModifiedTime(new Date());
            facetStateRepository.save(facetState);
        } else {
            facetStateRepository.updateByDomainIdAndTopicIdAndUserId(domainId
                    , topicId
                    , userId
                    , states
                    , new Date());
        }
        return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), "分面状态保存成功");
    }

    /**
     * 保存分面状态
     *
     * @param domainId
     * @param states
     * @param userId
     * @return
     */
    public Result saveState(Long domainId,
                            String states,
                            Long userId) {
        String[] statesByComma = states.split(";");
        List<Topic> topics = topicRepository.findByDomainId(domainId);
        if (topics.size() != statesByComma.length) {
            logger.error("分面状态保存失败：数量不一致");
            return ResultUtil.error(ResultEnum.STATE_INSERT_ERROR_2.getCode(), ResultEnum.STATE_INSERT_ERROR_2.getMsg());
        }
        List<FacetState> facetStateResults = facetStateRepository.findByDomainIdAndUserId(domainId, userId);
        List<FacetState> facetStates = new ArrayList<>();
        for (int i = 0; i < statesByComma.length; i++) {
            String s = statesByComma[i];
            if (!s.equals("")) {
                Topic topic = topics.get(i);
                FacetState facetState = new FacetState();
                facetState.setDomainId(domainId);
                facetState.setTopicId(topic.getTopicId());
                facetState.setStates(s);
                facetState.setUserId(userId);
                facetState.setCreatedTime(new Date());
                facetState.setModifiedTime(new Date());
                facetStates.add(facetState);
            }
        }
        //表中有分面状态
        if (facetStateResults != null && facetStateResults.size() != 0) {
            facetStateRepository.deleteByDomainIdAndUserId(domainId, userId);
            //表中分面状态数量和即将更新的数量不一致
            if (facetStateResults.size() != facetStates.size()) {
                logger.error("current facet states number is not equal to the origin: " +
                        "current" + facetStates.size() + ",origin:" + facetStateResults.size());
                return ResultUtil.error(ResultEnum.STATE_INSERT_ERROR_2.getCode()
                        , ResultEnum.STATE_INSERT_ERROR_2.getMsg());
            }
        }
        //更新分面状态
        try {
            facetStateRepository.save(facetStates);
        } catch (Exception e) {
            return ResultUtil.error(ResultEnum.STATE_INSERT_ERROR_1.getCode()
                    , ResultEnum.STATE_INSERT_ERROR_1.getMsg(), e);
        }
        return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), "分面状态保存成功");
    }

    /**
     * 保存分面状态
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
        return saveState(domain.getDomainId(), states, userId);
    }


    /**
     * 保存主题状态
     *
     * @param domainName
     * @param topicName
     * @param states
     * @param userId
     * @return
     */
    public Result saveState(String domainName, String topicName, String states
            , Long userId) {
        Domain domain = domainRepository.findByDomainName(domainName);
        if (domain == null) {
            logger.error("保存失败:课程不存在");
            return ResultUtil.error(ResultEnum.STATE_INSERT_ERROR.getCode(), ResultEnum.STATE_INSERT_ERROR.getMsg());
        }
        Topic topic = topicRepository.findByDomainIdAndTopicName(domain.getDomainId(), topicName);
        if (topic == null) {
            logger.error("保存失败:主题不存在");
            logger.error("public Result saveState(String domainName, String topicName, String states, Long userId)");
            return ResultUtil.error(ResultEnum.STATE_INSERT_ERROR.getCode(), ResultEnum.STATE_INSERT_ERROR.getMsg());
        }
        return saveState(domain.getDomainId()
                , topic.getTopicId()
                , states
                , userId);
    }

    /**
     * 查询分面状态
     *
     * @param domainId
     * @param userId
     * @return
     */
    public Result findByDomainIdAndTopicIdAndUserId(Long domainId, Long topicId, Long userId) {
        FacetState facetState = facetStateRepository.findByDomainIdAndTopicIdAndUserId(domainId, topicId, userId);
        String states = facetState.getStates();
        String[] stateList = states.split(",");
        List<Facet> facets = facetRepository.findByTopicIdAndFacetLayer(topicId, 1);
        states = "";
        for (int i = 0; i < facets.size(); i++) {
            if (!facets.get(i).getFacetName().equals("匿名分面") && i != (facets.size() - 1)) {
                states += stateList[i] + ",";
            } else if (!facets.get(i).getFacetName().equals("匿名分面") && i == (facets.size() - 1)) {
                states += stateList[i];
            }
        }
        if (states.substring(states.length() - 1).equals(",")) {
            states = states.substring(0, states.length() - 1);
        }
        facetState.setStates(states);
        return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), facetState);
    }

}
