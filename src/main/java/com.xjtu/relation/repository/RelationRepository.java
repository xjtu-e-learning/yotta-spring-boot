package com.xjtu.relation.repository;

import com.xjtu.relation.domain.Relation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * 上下位关系，数据操作
 * @author yangkuan
 * @date 2018/03/17 17:31
 * */

public interface RelationRepository extends JpaRepository<Relation, Long>,JpaSpecificationExecutor<Relation>{
    /**
     * 通过子主题id和父主题id，查询主题关系
     * @param childTopicId 子主题id
     * @param parentTopicId 父主题id
     * @return Relation
     * */
     Relation findByChildTopicIdAndParentTopicId(Long childTopicId, Long parentTopicId);

    /**
     * 通过子主题id，查询主题关系列表
     * @param childTopicId 子主题id
     * @return List<Relation>
     * */
     List<Relation> findByChildTopicId(Long childTopicId);

    /**
     * 通过父主题id，查询主题关系列表
     * @param parentTopicId 父主题id
     * @return List<Relation>
     * */
    List<Relation> findByParentTopicId(Long parentTopicId);

    /**
     * 通过课程id，查询主题关系列表
     * @param domainId 课程id
     * @return List<Relation>
     * */
    List<Relation> findByDomainId(Long domainId);

}
