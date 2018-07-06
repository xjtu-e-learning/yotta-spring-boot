package com.xjtu.education.service;

import com.xjtu.common.domain.Result;
import com.xjtu.common.domain.ResultEnum;
import com.xjtu.domain.domain.Domain;
import com.xjtu.domain.repository.DomainRepository;
import com.xjtu.education.domain.Recommendation;
import com.xjtu.education.repository.RecommendationRepository;
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
public class RecommendationService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RecommendationRepository recommendationRepository;

    @Autowired
    private DomainRepository domainRepository;

    /**
     * 保存推荐主题
     *
     * @param domainId
     * @param recommendationTopics
     * @param userId
     * @return
     */
    public Result saveRecommendation(Long domainId, String recommendationTopics
            , Long userId) {
        Recommendation recommendation = recommendationRepository
                .findByDomainIdAndUserId(domainId, userId);
        if (recommendation == null) {
            recommendation = new Recommendation();
            recommendation.setDomainId(domainId);
            recommendation.setRecommendationTopics(recommendationTopics);
            recommendation.setUserId(userId);
            recommendation.setCreatedTime(new Date());
            recommendation.setModifiedTime(new Date());
            recommendationRepository.save(recommendation);
        } else {
            recommendationRepository.updateByDomainIdAndUserId(domainId
                    , userId
                    , recommendationTopics
                    , new Date());
        }
        return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), "推荐主题保存成功");
    }

    /**
     * 保存推荐主题
     *
     * @param domainName
     * @param recommendationTopics
     * @param userId
     * @return
     */
    public Result saveRecommendation(String domainName, String recommendationTopics
            , Long userId) {
        Domain domain = domainRepository.findByDomainName(domainName);
        if (domain == null) {
            return ResultUtil.error(ResultEnum.RECOMMENDATION_INSERT_ERROR.getCode(), ResultEnum.RECOMMENDATION_INSERT_ERROR.getMsg());
        }
        return saveRecommendation(domain.getDomainId()
                , recommendationTopics
                , userId);
    }

    /**
     * 查询推荐主题
     *
     * @param domainId
     * @param userId
     * @return
     */
    public Result findByDomainIdAndUserId(Long domainId, Long userId) {
        Recommendation recommendation = recommendationRepository.findByDomainIdAndUserId(domainId, userId);
        return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), recommendation);
    }

}
