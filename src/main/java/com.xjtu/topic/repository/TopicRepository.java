package com.xjtu.topic.repository;


import com.xjtu.topic.domain.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
/**
 * 主题数据的数据库操作
 * @author yangkuan
 * @date 2018/03/09 15:15
 * */
public interface TopicRepository extends JpaRepository<Topic, Long>, JpaSpecificationExecutor<Topic>{

    /**
     * 根据课程名，查询课程下的所有主题
     * @param domainId 课程id
     * @return List<Topic>
     * */
    @Transactional(rollbackFor = Exception.class)
    List<Topic> findByDomainId(Long domainId);

    /**
     *根据课程名，查询课程下的第一个主题
     * @param domainId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    Topic findFirstByDomainId(Long domainId);

    /**
     * 根据主题名，查询课程下的所有主题
     * @param topicName 主题名
     * @return List<Topic>
     * */
    @Transactional(rollbackFor = Exception.class)
    List<Topic> findByTopicName(String topicName);

    /**
     * 根据主题名和课程id，查询对应主题信息
     * @param topicName 主题名
     * @param domainId 课程id
     * @return Topic
     * */
    @Transactional(rollbackFor = Exception.class)
    Topic findByTopicNameAndDomainId(String topicName, Long domainId);


    /**
     * 根据主题id和课程id，查询对应主题信息
     * @param topicId 主题id
     * @param domainId 课程id
     * @return Topic
     * */
    @Transactional(rollbackFor = Exception.class)
    Topic findByTopicIdAndDomainId(Long topicId, Long domainId);

    /**
     * 根据主题名和课程id，删除对应主题信息
     * @param topicName 主题名
     * @param domainId 课程id
     * @return void
     * */
    @Transactional(rollbackFor = Exception.class)
    void deleteByTopicNameAndDomainId(String topicName, Long domainId);



    /**
     * 修改主题名字
     * */
    /**
     * 根据主题名，修改对应主题信息(新主题名、课程id)
     * @param oldTopicName 旧主题名
     * @param newTopicName 新主题名
     * @param domainId 课程id
     * @return void
     * */
    @Modifying(clearAutomatically = true)
    @Transactional(rollbackFor = Exception.class)
    @Query("update Topic t set t.topicName = ?2, t.domainId = ?3 where t.topicName = ?1")
    void updateByTopicName(String oldTopicName, String newTopicName, Long domainId);

}
