package com.xjtu.facet.repository;

import com.xjtu.facet.domain.Facet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 * 分面表数据库操作
 *
 * @author yangkuan
 * @date 2018/03/12 10:29
 */

public interface FacetRepository extends JpaRepository<Facet, Long>, JpaSpecificationExecutor<Facet> {

    /**
     * 根据主题id删除主题下的所有分面
     *
     * @param topicId
     */
    @Modifying(clearAutomatically = true)
    @Transactional(rollbackFor = Exception.class)
    void deleteByTopicId(Long topicId);


    /**
     * 删除分面
     *
     * @param facetId
     * @param facetLayer
     */
    @Modifying(clearAutomatically = true)
    @Transactional(rollbackFor = Exception.class)
    void deleteByFacetIdAndFacetLayer(Long facetId, Integer facetLayer);

    /**
     * 指定主题Id，查找分面
     *
     * @param topicId 主题Id
     * @return List<Facet>
     */
    @Transactional(rollbackFor = Exception.class)
    List<Facet> findByTopicId(Long topicId);

    /**
     * 根据课程名查询分面
     *
     * @param domainName
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Query("select f from Facet f,Topic t,Domain d " +
            "where d.domainName = ?1 and " +
            "d.domainId = t.domainId and " +
            "t.topicId = f.topicId")
    List<Facet> findByDomainName(String domainName);


    /**
     * 指定主题和分面名，查询对应分面
     *
     * @param topicId
     * @param facetName
     * @return
     */
    Facet findByTopicIdAndFacetName(Long topicId, String facetName);

    /**
     * 指定父分面Id，查找分面
     *
     * @param parentFacetId 父分面Id
     * @return List<Facet>
     */
    @Transactional(rollbackFor = Exception.class)
    List<Facet> findByParentFacetId(Long parentFacetId);


    /**
     * 根据父分面id，查询子分面id
     *
     * @param parentFacetIds
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Query(value = "select f.facet_id from facet f where f.parent_facet_id in ?1", nativeQuery = true)
    List<BigInteger> findFacetIdsByParentFacetIds(List<Long> parentFacetIds);


    /**
     * 指定父分面Id以及分面所在层，查找分面
     *
     * @param parentFacetId 父分面Id
     * @param facetLayer    分面所在层
     * @return List<Facet>
     */
    @Transactional(rollbackFor = Exception.class)
    List<Facet> findByParentFacetIdAndFacetLayer(Long parentFacetId, Integer facetLayer);

    /**
     * 统计分面数量
     *
     * @param parentFacetId
     * @param facetLayer
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    Integer countByParentFacetIdAndFacetLayer(Long parentFacetId, Integer facetLayer);

    /**
     * 统计分面数量
     *
     * @param parentFacetIds
     * @param facetLayer
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Query(value = "select count(facet_id) " +
            "from Facet " +
            "where parentFacetId in (?1) and " +
            "facetLayer = ?2 " +
            "group by parentFacetId")
    List<Long> countAllFacetsByParentFacetIdAndFacetLayer(List<Long> parentFacetIds, Integer facetLayer);


    /**
     * 根据主题id集合，统计一级分面数量
     *
     * @param topicIds
     * @return
     */
    @Query(value = "SELECT topic_id,COUNT(facet_id)\n" +
            "FROM facet\n" +
            "WHERE topic_id in ?1 and facet_layer=?2\n" +
            "GROUP BY topic_id;", nativeQuery = true)
    List<Object[]> countFacetsGroupByTopicId(List<Long> topicIds, Integer facetLayer);

    /**
     * 指定分面名、主题Id以及分面所在层，查找分面
     * 注意：只能用于查询一级分面，因为二级、三级分面可能存在重名
     *
     * @param topicId    主题Id
     * @param facetName  分面名
     * @param facetLayer 分面所在层
     * @return Facet
     */
    @Transactional(rollbackFor = Exception.class)
    Facet findByTopicIdAndFacetNameAndFacetLayer(Long topicId, String facetName, Integer facetLayer);


    /**
     * @param topicId
     * @param facetName
     * @param parentFacetId
     * @return
     */
    Facet findByTopicIdAndFacetNameAndParentFacetId(Long topicId, String facetName, Long parentFacetId);


    /**
     * @param facetName
     * @param parentFacetId
     * @return
     */
    Facet findByFacetNameAndParentFacetId(String facetName, Long parentFacetId);

    /**
     * 指定分面所在层以及主题Id，查找分面
     *
     * @param topicId    主题Id
     * @param facetLayer 分面所在层
     * @return List<Facet>
     */
    @Transactional(rollbackFor = Exception.class)
    List<Facet> findByTopicIdAndFacetLayer(Long topicId, Integer facetLayer);

    /**
     * 统计分面数量
     *
     * @param topicId
     * @param facetLayer
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    Integer countByTopicIdAndFacetLayer(Long topicId, Integer facetLayer);

    /**
     * 查询一门课程下的所有分面，连表查询
     *
     * @param domainId 课程Id
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Query("SELECT \n" +
            "f \n" +
            "FROM \n" +
            "Topic t,\n" +
            "Facet f \n" +
            "WHERE \n" +
            "t.domainId = ?1 AND \n" +
            "t.topicId = f .topicId")
    List<Facet> findAllFacetsByDomainId(Long domainId);

    /**
     * 查询一门课程主题下的所有分面，连表查询
     *
     * @param domainId  课程Id
     * @param topicName 主题名
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Query("SELECT \n" +
            "f \n" +
            "FROM \n" +
            "Topic t,\n" +
            "Facet f \n" +
            "WHERE \n" +
            "t.domainId = ?1 AND \n" +
            "t.topicId = f .topicId and \n" +
            "t.topicName = ?2")
    List<Facet> findAllFacetsByDomainIdAndTopicName(Long domainId, String topicName);

    /**
     * 查询一门课程下的所有分面的数量，连表查询
     *
     * @param domainId 课程Id
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Query(value = "SELECT \n" +
            "count(f.facet_id) \n" +
            "FROM \n" +
            "topic t,\n" +
            "facet f \n" +
            "WHERE \n" +
            "t.domain_id = ?1 AND \n" +
            "t.topic_id = f .topic_id", nativeQuery = true)
    Integer findFacetNumberByDomainId(Long domainId);

    /**
     * 查询一门课程下的各层分面，连表查询
     *
     * @param domainId   课程Id
     * @param facetLayer 分面所在层
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Query("SELECT \n" +
            "f \n" +
            "FROM \n" +
            "Topic t,\n" +
            "Facet f \n" +
            "WHERE \n" +
            "t.domainId = ?1 AND \n" +
            "t.topicId = f .topicId AND \n" +
            "f.facetLayer = ?2 ")
    List<Facet> findFacetsByDomainIdAndFacetLayer(Long domainId, Integer facetLayer);

    /**
     * 查询一门课程下的各层分面数，连表查询
     *
     * @param domainId   课程Id
     * @param facetLayer 分面所在层
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Query(value = "SELECT \n" +
            "count(f.facet_id) \n" +
            "FROM \n" +
            "topic t,\n" +
            "facet f \n" +
            "WHERE \n" +
            "t.domain_id = ?1 AND \n" +
            "t.topic_id = f .topic_id AND \n" +
            "f.facet_layer IN ?2 GROUP BY f.facet_layer", nativeQuery = true)
    List<BigInteger> findFacetNumberByDomainIdAndFacetLayer(Long domainId, List<Integer> facetLayer);


    /**
     * 查询课程下的各层分面数，连表查询
     *
     * @param domainId
     * @param facetLayer
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Query(value = "SELECT t.domain_id,COUNT(f.facet_id)\n" +
            "FROM topic t, facet f\n" +
            "WHERE t.domain_id IN ?1 AND \n" +
            "t.topic_id = f.topic_id AND \n" +
            "f.facet_layer=?2 \n" +
            "GROUP BY t.domain_id", nativeQuery = true)
    List<Object[]> countFacetsGroupByDomainIdAndFacetLayer(List<Long> domainId, Integer facetLayer);


    /**
     * 查询课程下的各层分面数，连表查询
     *
     * @param domainIds
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Query(value = "SELECT t.domain_id,COUNT(f.facet_id)\n" +
            "FROM topic t, facet f\n" +
            "WHERE t.domain_id IN ?1 AND \n" +
            "t.topic_id = f.topic_id \n" +
            "GROUP BY t.domain_id", nativeQuery = true)
    List<Object[]> countFacetsGroupByDomainId(List<Long> domainIds);

    /**
     * 统计分面数
     *
     * @param domainId
     * @return
     */
    @Query(value = "SELECT COUNT(f.facet_id)\n" +
            "FROM facet AS f,topic AS t \n" +
            "WHERE t.domain_id=?1 and t.topic_id=f.topic_id; ", nativeQuery = true)
    @Transactional(rollbackFor = Exception.class)
    Integer countByDomainId(Long domainId);


    /**
     * 统计某一级分面数
     *
     * @param domainId
     * @return
     */
    @Query(value = "SELECT COUNT(f.facet_id)\n" +
            "FROM facet AS f,topic AS t \n" +
            "WHERE t.domain_id=?1 and t.topic_id=f.topic_id and f.facet_layer=?2", nativeQuery = true)
    @Transactional(rollbackFor = Exception.class)
    Integer countByDomainIdAndFacetLayer(Long domainId, Integer facetLayer);


    /**
     * 更新分面
     *
     * @param facetId       分面Id
     * @param facetName     分面名
     * @param facetLayer    分面所在层
     * @param parentFacetId 父分面Id
     * @param topicId       主题Id
     * @return
     */
    @Modifying(clearAutomatically = true)
    @Transactional(rollbackFor = Exception.class)
    @Query("update Facet f set f.facetId = ?1, f.facetName = ?2, f.facetLayer = ?3, " +
            "f.parentFacetId = ?4, f.topicId = ?5 where f.facetId = ?1")
    void updateFacetById(Long facetId, String facetName,
                         Integer facetLayer, Long parentFacetId, Long topicId);

    /**
     * 根据关键字，查询相关分面名、分面所在层、对应主题名、对应课程名
     *
     * @param keyword
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Query("select new map(f.facetName,f.facetLayer,t.topicName,d.domainName) from Facet f, Topic t, Domain d where f.topicId = t.topicId and t.domainId = d.domainId and f.facetName like %?1%")
    List<Map<String, Object>> findFacetInformationByKeyword(String keyword);

    @Transactional(rollbackFor = Exception.class)
    Facet findByFacetId(Long facetId);

}
