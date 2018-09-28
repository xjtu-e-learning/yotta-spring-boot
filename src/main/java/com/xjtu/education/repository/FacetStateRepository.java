package com.xjtu.education.repository;

import com.xjtu.education.domain.FacetState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author yangkuan
 */
public interface FacetStateRepository extends JpaRepository<FacetState, Long> {
    /**
     * 查询分面状态
     *
     * @param domainId
     * @param topicId
     * @param userId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    FacetState findByDomainIdAndTopicIdAndUserId(Long domainId, Long topicId, Long userId);

    /**
     * 查询分面状态
     *
     * @param domainId
     * @param userId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    List<FacetState> findByDomainIdAndUserId(Long domainId, Long userId);


    /**
     * 删除分面状态
     *
     * @param domainId
     * @param userId
     */
    @Modifying(clearAutomatically = true)
    @Transactional(rollbackFor = Exception.class)
    void deleteByDomainIdAndUserId(Long domainId, Long userId);
    /**
     * 更新分面状态
     *
     * @param domainId
     * @param topicId
     * @param userId
     * @param states
     * @param modifiedTime
     */
    @Modifying(clearAutomatically = true)
    @Transactional(rollbackFor = Exception.class)
    @Query("update FacetState set states=?4, modifiedTime=?5 where domainId=?1 and topicId=?2 and userId=?3")
    void updateByDomainIdAndTopicIdAndUserId(Long domainId, Long topicId, Long userId
            , String states, Date modifiedTime);

}
