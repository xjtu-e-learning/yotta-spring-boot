package com.xjtu.topic.repository;


import com.xjtu.topic.domain.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 主题数据的数据库操作
 *
 * @author yangkuan
 * @date 2018/03/09 15:15
 */
public interface TopicRepository extends JpaRepository<Topic, Long>, JpaSpecificationExecutor<Topic> {

    /**
     * 根据课程id，查询课程下的所有主题
     *
     * @param domainId 课程id
     * @return List<Topic>
     */
    @Transactional(rollbackFor = Exception.class)
    List<Topic> findByDomainId(Long domainId);

    /**
     * 根据课程id和主题所在层，查询课程下的所有主题
     *
     * @param domainId
     * @param topicLayer
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    List<Topic> findByDomainIdAndTopicLayer(Long domainId, Long topicLayer);

    /**
     * 根据课程名，查询课程下的主题数
     *
     * @param domainId 课程id
     * @return 主题数
     */
    @Transactional(rollbackFor = Exception.class)
    @Query(value = "select count(t.topic_id) from topic t where t.domain_id=?1", nativeQuery = true)
    Integer findTopicNumberByDomainId(Long domainId);

    /**
     * 查询所有主题
     *
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Query(value = "SELECT * \n" +
            "FROM topic \n" +
            "WHERE topic.domain_id IN\n" +
            "(SELECT domain_id FROM domain);", nativeQuery = true)
    List<Topic> findAll();


    /**
     * 根据课程，查询课程下的主题数
     *
     * @param domainIds
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Query(value = "select t.domain_id,count(t.topic_id) from topic t where t.domain_id IN ?1 " +
            "GROUP BY t.domain_id", nativeQuery = true)
    List<Object[]> countTopicsGroupByDomainId(List<Long> domainIds);

    /**
     * 统计一门课程下的碎片，按主题分布
     *
     * @param domainId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Query(value = "SELECT topic.topic_id,COUNT(*)\n" +
            "FROM topic,facet,assemble\n" +
            "WHERE topic.topic_id=facet.topic_id and facet.facet_id=assemble.facet_id and topic.domain_id=?1\n" +
            "GROUP BY topic.topic_id;", nativeQuery = true)
    List<Object[]> countAssemblesByDomainIdGroupByTopicId(Long domainId);


    /**
     * 统计主题数
     *
     * @param domainId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    Integer countByDomainId(Long domainId);


    /**
     * 根据课程id，查询课程下的第一个主题
     *
     * @param domainId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    Topic findFirstByDomainId(Long domainId);

    /**
     * 根据课程名，查询课程下的第一个主题
     *
     * @param domainName
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Query("select t from Topic t,Domain d where d.domainName=?1 and d.domainId=t.domainId")
    List<Topic> findByDomainName(String domainName);

    /**
     * 根据主题ID，查询课程下的所有主题
     *
     * @param topicId 主题ID
     * @return Topic
     */
    @Transactional(rollbackFor = Exception.class)
    Topic findByTopicId(Long topicId);

    /**
     * 根据主题名，查询课程下的所有主题
     *
     * @param topicName 主题名
     * @return List<Topic>
     */
    @Transactional(rollbackFor = Exception.class)
    List<Topic> findByTopicName(String topicName);

    /**
     * 根据主题名和课程id，查询对应主题信息
     *
     * @param domainId  课程id
     * @param topicName 主题名
     * @return Topic
     */
    @Transactional(rollbackFor = Exception.class)
    Topic findByDomainIdAndTopicName(Long domainId, String topicName);


    /**
     * 根据主题名和课程id，删除对应主题信息
     *
     * @param domainId  课程id
     * @param topicName 主题名
     * @return void
     */
    @Transactional(rollbackFor = Exception.class)
    void deleteByDomainIdAndTopicName(Long domainId, String topicName);


    /**
     * 修改主题名字
     * */
    /**
     * 根据主题名，修改对应主题信息(新主题名、课程id)
     *
     * @param oldTopicName 旧主题名
     * @param newTopicName 新主题名
     * @param domainId     课程id
     * @return void
     */
    @Modifying(clearAutomatically = true)
    @Transactional(rollbackFor = Exception.class)
    @Query("update Topic t set t.topicName = ?2, t.domainId = ?3 where t.topicName = ?1")
    void updateByTopicName(String oldTopicName, String newTopicName, Long domainId);

    /**
     * 根据关键字，查询相关主题
     *
     * @param keyword
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Query("select new map(t.topicName,d.domainName) from Topic t, Domain d where t.domainId = d.domainId and t.topicName like %?1%")
    List<Map<String, Object>> findTopicInformationByKeyword(String keyword);

}
