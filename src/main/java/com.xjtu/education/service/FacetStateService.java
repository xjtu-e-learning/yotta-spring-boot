package com.xjtu.education.service;

import com.xjtu.common.domain.Result;
import com.xjtu.common.domain.ResultEnum;
import com.xjtu.domain.domain.Domain;
import com.xjtu.domain.repository.DomainRepository;
import com.xjtu.education.domain.FacetState;
import com.xjtu.education.repository.FacetStateRepository;
import com.xjtu.utils.ResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

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

    /**
     * 保存分面状态
     *
     * @param domainId
     * @param states
     * @param userId
     * @return
     */
    public Result saveState(Long domainId, String states
            , Long userId) {
        FacetState facetState = facetStateRepository.findByDomainIdAndUserId(domainId, userId);
        if (facetState == null) {
            facetState = new FacetState();
            facetState.setDomainId(domainId);
            facetState.setStates(states);
            facetState.setUserId(userId);
            facetState.setCreatedTime(new Date());
            facetState.setModifiedTime(new Date());
            facetStateRepository.save(facetState);
        } else {
            facetStateRepository.updateByDomainIdAndUserId(domainId
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
            logger.info("保存失败:课程不存在");
            return ResultUtil.error(ResultEnum.STATE_INSERT_ERROR.getCode(), ResultEnum.STATE_INSERT_ERROR.getMsg());
        }
        return saveState(domain.getDomainId()
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
    public Result findByDomainIdAndUserId(Long domainId, Long userId) {
        FacetState facetState = facetStateRepository.findByDomainIdAndUserId(domainId, userId);
        logger.info("主题状态查询成功");
        return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), facetState);
    }

}
