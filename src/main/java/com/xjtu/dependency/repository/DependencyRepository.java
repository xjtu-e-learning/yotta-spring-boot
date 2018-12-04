package com.xjtu.dependency.repository;

import com.xjtu.dependency.domain.Dependency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 主题依赖关系数据库操作
 *
 * @author yangkuan
 * @date 2018/03/21 12:39
 */

public interface DependencyRepository extends JpaRepository<Dependency, Long>, JpaSpecificationExecutor<Dependency> {

    /**
     * 根据起始主题id,查询依赖关系
     *
     * @param startTopicId
     * @return List<Dependency>
     */
    @Transactional(rollbackFor = Exception.class)
    List<Dependency> findByStartTopicId(Long startTopicId);

    /**
     * 根据起始主题id和终止主题id,查询依赖关系
     *
     * @param startTopicId
     * @param endTopicId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    Dependency findByStartTopicIdAndEndTopicId(Long startTopicId, Long endTopicId);

    /**
     * 根据起始主题id或终止主题id,查询依赖关系
     *
     * @param startTopicId
     * @param endTopicId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    List<Dependency> findByStartTopicIdOrEndTopicId(Long startTopicId, Long endTopicId);

    /**
     * 根据起始主题id或终止主题id,统计依赖关系
     *
     * @param startTopicId
     * @param endTopicId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    Integer countByStartTopicIdOrEndTopicId(Long startTopicId, Long endTopicId);

    /**
     * 根据终止主题id,查询依赖关系
     *
     * @param endTopicId
     * @return List<Dependency>
     */
    @Transactional(rollbackFor = Exception.class)
    List<Dependency> findByEndTopicId(Long endTopicId);

    /**
     * 统计出度
     *
     * @param domainId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Query(value = "SELECT start_topic_id,COUNT(start_topic_id) \n" +
            "FROM dependency \n" +
            "WHERE domain_id=?1 \n" +
            "GROUP BY start_topic_id", nativeQuery = true)
    List<Object[]> countOutDegreeByDomainId(Long domainId);

    /**
     * 统计依赖关系
     *
     * @param domainId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    Integer countByDomainId(Long domainId);
    /**
     * 统计入度
     *
     * @param domainId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Query(value = "SELECT end_topic_id,COUNT(end_topic_id) \n" +
            "FROM dependency \n" +
            "WHERE domain_id=?1 \n" +
            "GROUP BY end_topic_id", nativeQuery = true)
    List<Object[]> countInDegreeByDomainId(Long domainId);

    /**
     * 根据课程id,查询依赖关系
     *
     * @param domainId
     * @return List<Dependency>
     */
    @Transactional(rollbackFor = Exception.class)
    List<Dependency> findByDomainId(Long domainId);

    /**
     * 根据课程名查询依赖关系
     *
     * @param domainName
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Query("select de from Dependency de,Domain d where d.domainName=?1 and d.domainId=de.domainId")
    List<Dependency> findByDomainName(String domainName);

    /**
     * 根据课程ids,查询依赖关系数量
     *
     * @param domainIds
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Query(value = "select d.domain_id,count(d.dependency_id) " +
            "from dependency d " +
            "where d.domain_id IN ?1 GROUP BY d.domain_id", nativeQuery = true)
    List<Object[]> countDependenciesGroupByDomainId(List<Long> domainIds);

    /**
     * 删除依赖关系，指定起始主题或者终止主题
     *
     * @param startTopicId
     * @param endTopicId
     */
    @Transactional(rollbackFor = Exception.class)
    @Modifying(clearAutomatically = true)
    void deleteByStartTopicIdOrEndTopicId(Long startTopicId, Long endTopicId);

    /**
     * 删除依赖关系，指定课程、起始主题以及终止主题
     *
     * @param domainId
     * @param startTopicId
     * @param endTopicId
     */
    @Transactional(rollbackFor = Exception.class)
    @Modifying(clearAutomatically = true)
    void deleteByDomainIdAndStartTopicIdAndEndTopicId(Long domainId, Long startTopicId, Long endTopicId);

    /**
     * 通过课程名和关键词，获取该课程下的主题依赖关系
     *
     * @param domainId
     * @param keyword
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Modifying(clearAutomatically = true)
    @Query("select d from Dependency d,Topic t where d.domainId=?1 " +
            "and ((d.startTopicId=t.topicId and t.topicName like %?2%) " +
            "or (d.endTopicId=t.topicId and t.topicName like  %?2%))")
    List<Dependency> findDependenciesByDomainIdAndKeyword(Long domainId, String keyword);


}
