package com.xjtu.relation.repository;

import com.xjtu.relation.domain.Relation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 上下位关系，数据操作
 *
 * @author yangkuan
 * @date 2018/03/17 17:31
 */

public interface RelationRepository extends JpaRepository<Relation, Long>, JpaSpecificationExecutor<Relation> {
    /**
     * 通过子主题id和父主题id，查询主题关系
     *
     * @param childTopicId  子主题id
     * @param parentTopicId 父主题id
     * @return Relation
     */
    @Transactional(rollbackFor = Exception.class)
    Relation findByChildTopicIdAndParentTopicId(Long childTopicId, Long parentTopicId);

    /**
     * 通过子主题id，查询主题关系列表
     *
     * @param childTopicId 子主题id
     * @return List<Relation>
     */
    @Transactional(rollbackFor = Exception.class)
    List<Relation> findByChildTopicId(Long childTopicId);

    /**
     * 通过父主题id，查询主题关系列表
     *
     * @param parentTopicId 父主题id
     * @return List<Relation>
     */
    @Transactional(rollbackFor = Exception.class)
    List<Relation> findByParentTopicId(Long parentTopicId);

    /**
     * 通过课程id，查询主题关系列表
     *
     * @param domainId 课程id
     * @return List<Relation>
     */
    @Transactional(rollbackFor = Exception.class)
    List<Relation> findByDomainId(Long domainId);

    /**
     * 删除上下位关系，指定父主题以及子主题
     *
     * @param childTopicId
     * @param parentTopicId
     */
    @Modifying(clearAutomatically = true)
    @Transactional(rollbackFor = Exception.class)
    void deleteByChildTopicIdOrParentTopicId(Long childTopicId, Long parentTopicId);

    /**
     * 删除上下位关系，指定子主题
     *
     * @param childTopicId
     */
    @Modifying(clearAutomatically = true)
    @Transactional(rollbackFor = Exception.class)
    void deleteByChildTopicId(Long childTopicId);

    /**
     * 删除上下位关系，指定父主题
     *
     * @param parentTopicId
     */
    @Modifying(clearAutomatically = true)
    @Transactional(rollbackFor = Exception.class)
    void deleteByParentTopicId(Long parentTopicId);

    /**
     * 统计上下位关系数
     *
     * @param domainId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    Integer countByDomainId(Long domainId);

}
