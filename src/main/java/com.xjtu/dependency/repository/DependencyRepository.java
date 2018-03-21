package com.xjtu.dependency.repository;

import com.xjtu.dependency.domain.Dependency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
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

}
