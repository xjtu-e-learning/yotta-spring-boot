package com.xjtu.assemble.repository;


import com.xjtu.assemble.domain.Assemble;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.Collection;
import java.util.List;

/**
 * 碎片表数据库操作
 *
 * @author yangkuan
 * @date 2018/03/15 14:47
 */
public interface AssembleRepository extends JpaRepository<Assemble, Long>, JpaSpecificationExecutor<Assemble> {


    /**
     * 根据分面id集合，删除分面下的所有碎片
     *
     * @param facetIds
     */
    @Modifying(clearAutomatically = true)
    @Transactional(rollbackFor = Exception.class)
    void deleteByFacetIdIsIn(Collection<Long> facetIds);

    /**
     * 根据分面id，删除分面下的所有碎片
     *
     * @param facetId
     */
    @Modifying(clearAutomatically = true)
    @Transactional(rollbackFor = Exception.class)
    void deleteByFacetId(Long facetId);

    /**
     * 根据分面id，查询分面下的所有碎片
     *
     * @param facetIds
     */
    @Transactional(rollbackFor = Exception.class)
    @Query("select a from Assemble as a where a.facetId in ?1")
    List<Assemble> findByFacetIdIn(Collection<Long> facetIds);

    /**
     * 根据主题id，删除主题下的所有碎片
     *
     * @param topicId
     * @return
     */
    @Modifying(clearAutomatically = true)
    @Transactional(rollbackFor = Exception.class)
    @Query(value = "DELETE \n" +
            "a \n" +
            "FROM\n" +
            "assemble AS a ,\n" +
            "facet AS f \n" +
            "WHERE\n" +
            "a.facet_id = f.facet_id AND\n" +
            "f.topic_id = ?1 ", nativeQuery = true)
    void deleteByTopicId(Long topicId);

    /**
     * 指定分面id，获取对应分面下的碎片
     *
     * @param facetId 分面id
     * @return List<Assemble>
     */
    @Transactional(rollbackFor = Exception.class)
    List<Assemble> findByFacetId(Long facetId);


    @Transactional(rollbackFor = Exception.class)
    List<Assemble> findByDomainId(Long domainId);


    /**
     * 根据课程名查询碎片
     *
     * @param domainName
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Query(value = "select a from Assemble a,Domain d where d.domainName=?1 and d.domainId=a.domainId")
    List<Assemble> findByDomainName(String domainName);

    /**
     * 指定分面id，统计对应分面下的碎片
     *
     * @param facetId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    Integer countByFacetId(Long facetId);

    /**
     * 根据分面id集合，查询碎片数量
     *
     * @param facetIds
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    Integer countByFacetIdIn(Collection<Long> facetIds);

    /**
     * 查询主题下的所有碎片
     *
     * @param topicId 主题id
     * @return
     */
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


    @Transactional(rollbackFor = Exception.class)
    @Query(value = "SELECT COUNT(a.assemble_id)\n" +
            "FROM assemble AS a,facet AS f\n" +
            "WHERE f.topic_id=?1 AND f.facet_id=a.facet_id;", nativeQuery = true)
    Integer countByTopicId(Long topicId);

    /**
     * 分页查询主题下的所有碎片
     *
     * @param topicId  主题id
     * @param pageable
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Query("SELECT\n" +
            "a \n" +
            "FROM\n" +
            "Assemble AS a ,\n" +
            "Facet AS f \n" +
            "WHERE\n" +
            "f.topicId = ?1 AND\n" +
            "f.facetId = a.facetId")
    Page<Assemble> findByTopicIdAndPageable(Long topicId, Pageable pageable);


    /**
     * 查询课程下的所有碎片
     *
     * @param domainId 课程id
     * @return
     */
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


    /**
     * 查询课程和主题下的某层分面下的所有碎片
     *
     * @param topicId    主题id
     * @param facetLayer 分面所在层
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Query("SELECT\n" +
            "a \n" +
            "FROM\n" +
            "Assemble AS a ,\n" +
            "Facet AS f \n" +
            "WHERE \n" +
            "f.topicId = ?1 AND \n" +
            "f.facetLayer = ?2 AND \n" +
            "f.facetId = a.facetId")
    List<Assemble> findByTopicIdAndFacetLayer(Long topicId, Integer facetLayer);

    /**
     * 查询课程下的碎片数量
     *
     * @param domainIds 课程id
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Query(value = "SELECT \n" +
            "count(a.assemble_id) \n" +
            "FROM \n" +
            "assemble AS a ,\n" +
            "facet AS f ,\n" +
            "topic AS t \n" +
            "WHERE \n" +
            "t.topic_id = f.topic_id AND \n" +
            "f.facet_id = a.facet_id AND \n" +
            "t.domain_id IN ?1 GROUP BY t.domain_id", nativeQuery = true)
    List<BigInteger> findAssembleNumbersByDomainId(List<Long> domainIds);


    /**
     * 查询课程下的碎片数量
     *
     * @param domainIds 课程id
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Query(value = "SELECT a.domain_id,count(a.assemble_id) \n" +
            "from assemble AS a \n" +
            "WHERE a.domain_id IN ?1\n" +
            "GROUP BY a.domain_id", nativeQuery = true)
    List<Object[]> countAssemblesGroupByDomainId(List<Long> domainIds);


    @Transactional(rollbackFor = Exception.class)
    @Query(value = "SELECT f.topic_id,COUNT(a.assemble_id)\n" +
            "FROM facet as f,assemble as a\n" +
            "WHERE f.topic_id in ?1 and f.facet_id=a.facet_id\n" +
            "GROUP BY topic_id;", nativeQuery = true)
    List<Object[]> countAssemblesByGroupByTopicId(List<Long> topicIds);

    /**
     * 统计课程下的碎片数
     *
     * @param domainId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    Integer countByDomainId(Long domainId);

    /**
     * 查询最大主键
     *
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Query("select max(a.assembleId) from Assemble a")
    Long findMaxId();

    /**
     * 更新碎片
     *
     * @param assembleId
     * @param assembleContent
     * @param assembleText
     * @param assembleScratchTime
     */
    @Modifying(clearAutomatically = true)
    @Transactional(rollbackFor = Exception.class)
    @Query("update Assemble set assembleContent=?2,assembleText=?3,assembleScratchTime=?4,sourceId=?5,url=?6" +
            " where assembleId=?1")
    void updateAssemble(Long assembleId, String assembleContent, String assembleText
            , String assembleScratchTime, Long sourceId, String url);

    @Query("select count(assembleId) from Assemble where assembleScratchTime > ?1 and domainId = ?2")
    Long countUpdateAssembleByDomainIdAndAssembleScratchTime(String localTime, Long domainId);

}
