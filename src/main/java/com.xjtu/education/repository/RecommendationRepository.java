package com.xjtu.education.repository;


import com.xjtu.education.domain.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * 推荐主题数据库操作
 *
 * @author yangkuan
 * @date 2018/06/13 19:55
 */
public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {

    /**
     * 查询推荐主题
     *
     * @param domainId
     * @param userId
     * @return
     */
    Recommendation findByDomainIdAndUserId(Long domainId, Long userId);

    /**
     * 更新推荐主题
     *
     * @param domainId
     * @param userId
     * @param recommendationTopics
     * @param modifiedTime
     */
    @Modifying(clearAutomatically = true)
    @Transactional(rollbackFor = Exception.class)
    @Query("update Recommendation set recommendationTopics=?3, modifiedTime=?4 where domainId=?1 and userId=?2")
    void updateByDomainIdAndUserId(Long domainId, Long userId
            , String recommendationTopics, Date modifiedTime);

}
