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
 * @author yangkuan
 * @date 2018/03/21 12:39
 * */

public interface DependencyRepository extends JpaRepository<Dependency, Long>,JpaSpecificationExecutor<Dependency>{

    /**
     * 根据起始主题id,查询依赖关系
     * @param startTopicId
     * @return List<Dependency>
     * */
    @Transactional(rollbackFor = Exception.class)
    List<Dependency> findByStartTopicId(Long startTopicId);


    /**
     * 根据起始主题id或终止主题id,查询依赖关系
     * @param startTopicId
     * @param endTopicId
     * @return
     */
    List<Dependency> findByStartTopicIdOrEndTopicId(Long startTopicId,Long endTopicId);

    /**
     * 根据终止主题id,查询依赖关系
     * @param endTopicId
     * @return List<Dependency>
     * */
    @Transactional(rollbackFor = Exception.class)
    List<Dependency> findByEndTopicId(Long endTopicId);

    /**
     * 根据课程id,查询依赖关系
     * @param domainId
     * @return List<Dependency>
     * */
    @Transactional(rollbackFor = Exception.class)
    List<Dependency> findByDomainId(Long domainId);

    /**
     * 删除依赖关系，指定起始主题或者终止主题
     * @param startTopicId
     * @param endTopicId
     */
    @Transactional(rollbackFor = Exception.class)
    @Modifying(clearAutomatically = true)
    void deleteByStartTopicIdOrEndTopicId(Long startTopicId, Long endTopicId);

    /**
     * 删除依赖关系，指定课程、起始主题以及终止主题
     * @param domainId
     * @param startTopicId
     * @param endTopicId
     */
    @Transactional(rollbackFor = Exception.class)
    @Modifying(clearAutomatically = true)
    void deleteByDomainIdAndStartTopicIdAndEndTopicId(Long domainId,Long startTopicId, Long endTopicId);

    /**
     * 通过课程名和关键词，获取该课程下的主题依赖关系
     * @param domainId
     * @param keyword
     */
    @Transactional(rollbackFor = Exception.class)
    @Modifying(clearAutomatically = true)
    @Query("select d from Dependency d,Topic t where d.domainId=?1 " +
            "and ((d.startTopicId=t.topicId and t.topicName like %?2%) " +
            "or (d.endTopicId=t.topicId and t.topicName like  %?2%))")
    List<Dependency> findDependenciesByDomainIdAndKeyword(Long domainId,String keyword);


}
