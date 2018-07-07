package com.xjtu.education.repository;

import com.xjtu.education.domain.TopicState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * 主题状态查询接口
 *
 * @author yangkuan
 */
public interface TopicStateRepository extends JpaRepository<TopicState, Long> {

    /**
     * 查询主题状态
     *
     * @param domainId
     * @param userId
     * @return
     */
    TopicState findByDomainIdAndUserId(Long domainId, Long userId);

    /**
     * 更新主题状态
     *
     * @param domainId
     * @param userId
     * @param states
     * @param modifiedTime
     */
    @Modifying(clearAutomatically = true)
    @Transactional(rollbackFor = Exception.class)
    @Query("update TopicState set states=?3, modifiedTime=?4 where domainId=?1 and userId=?2")
    void updateByDomainIdAndUserId(Long domainId
            , Long userId
            , String states
            , Date modifiedTime);
}
