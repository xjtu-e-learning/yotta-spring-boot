package com.xjtu.education.repository;

import com.xjtu.education.domain.FacetState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @author yangkuan
 */
public interface FacetStateRepository extends JpaRepository<FacetState, Long> {
    /**
     * 查询分面状态
     *
     * @param domainId
     * @param userId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    FacetState findByDomainIdAndUserId(Long domainId, Long userId);

    /**
     * 更新分面状态
     *
     * @param domainId
     * @param userId
     * @param states
     * @param modifiedTime
     */
    @Modifying(clearAutomatically = true)
    @Transactional(rollbackFor = Exception.class)
    @Query("update FacetState set states=?3, modifiedTime=?4 where domainId=?1 and userId=?2")
    void updateByDomainIdAndUserId(Long domainId
            , Long userId
            , String states
            , Date modifiedTime);
}
