package com.xjtu.topic.repository;


import com.xjtu.topic.domain.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TopicRepository extends JpaRepository<Topic, Long>, JpaSpecificationExecutor<Topic>{

    /**
     * 根据课程名，查询课程下的所有主题
     */
    @Transactional
    List<Topic> findByDomainId(Long domainId);

    @Transactional
    List<Topic> findByTopicName(String topicName);


    @Transactional
    Topic findByTopicNameAndDomainId(String topicName, Long domainId);

    @Transactional
    void deleteByTopicNameAndDomainId(String topicName, Long domainId);


    /**
     * 修改主题名字
     * */
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("update Topic t set t.topicName = ?2, t.domainId = ?3 where t.topicName = ?1")
    void updateByTopicName(String oldTopicName, String newTopicName, Long domainId);

}
