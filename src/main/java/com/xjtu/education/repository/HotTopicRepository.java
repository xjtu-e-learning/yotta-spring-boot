package com.xjtu.education.repository;

import com.xjtu.education.domain.HotTopic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * 数据库访问接口
 *
 * @author yangkuan
 * @date 2018/10/24
 */
public interface HotTopicRepository extends JpaRepository<HotTopic, Long> {

    /**
     * 查询热度主题
     *
     * @param domainId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    HotTopic findByDomainId(Long domainId);

    /**
     * 更新热度主题
     *
     * @param domainId
     * @param hotTopics
     * @param modifiedTime
     */
    @Modifying(clearAutomatically = true)
    @Transactional(rollbackFor = Exception.class)
    @Query(value = "update HotTopic set hotTopics=?2,modifiedTime=?3 where domainId=?1")
    void updateByDomainId(Long domainId, String hotTopics, Date modifiedTime);

}
