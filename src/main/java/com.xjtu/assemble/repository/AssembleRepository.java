package com.xjtu.assemble.repository;


import com.xjtu.assemble.domain.Assemble;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

/**
 * 碎片表数据库操作
 * @author yangkuan
 * @date 2018/03/15 14:47
 * */
public interface AssembleRepository extends JpaRepository<Assemble, Long>,JpaSpecificationExecutor<Assemble> {


    /**
     * 根据分面id，删除分面下的所有碎片
     * @param facetIds
     */
    @Modifying(clearAutomatically = true)
    @Transactional(rollbackFor = Exception.class)
    void deleteByFacetIdIsIn(Collection<Long> facetIds);


    /**
     * 根据主题id，删除主题下的所有碎片
     * @param topicId
     * @return
     */
    @Modifying(clearAutomatically = true)
    @Transactional(rollbackFor = Exception.class)
    @Query(value = "DELETE \n" +
            "a \n"+
            "FROM\n" +
            "assemble AS a ,\n" +
            "facet AS f \n" +
            "WHERE\n" +
            "a.facet_id = f.facet_id AND\n" +
            "f.topic_id = ?1 ", nativeQuery = true)
    void deleteByTopicId(Long topicId);

    /**
     *指定分面id，获取对应分面下的碎片
     * @param facetId 分面id
     * @return List<Assemble>
     * */
    @Transactional(rollbackFor = Exception.class)
    List<Assemble> findByFacetId(Long facetId);

    /**
     * 查询主题下的所有碎片
     * @param topicId 主题id
     * @return
     */
    @Modifying(clearAutomatically = true)
    @Transactional(rollbackFor = Exception.class)
    @Query("SELECT\n" +
            "a \n" +
            "FROM\n" +
            "Assemble AS a ,\n" +
            "Facet AS f \n" +
            "WHERE\n" +
            "f.topicId = ?1 AND\n" +
            "f.facetId = a.facetId")
    List<Assemble> findAllAssemblesByTopicId(Long topicId);

    /**
     * 查询课程下的所有碎片
     * @param domainId 课程id
     * @return
     */
    @Modifying(clearAutomatically = true)
    @Transactional(rollbackFor = Exception.class)
    @Query("SELECT\n" +
            "a \n" +
            "FROM\n" +
            "Assemble AS a ,\n" +
            "Facet AS f ,\n" +
            "Topic AS t\n" +
            "WHERE\n" +
            "t.topicId = f.topicId AND\n" +
            "f.facetId = a.facetId AND\n" +
            "t.domainId = ?1\n")
    List<Assemble> findAllAssemblesByDomainId(Long domainId);
}
