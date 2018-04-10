package com.xjtu.facet.repository;

import com.xjtu.facet.domain.Facet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 分面表数据库操作
 * @author yangkuan
 * @date 2018/03/12 10:29
 * */

public interface FacetRepository extends JpaRepository<Facet, Long>, JpaSpecificationExecutor<Facet> {

    /**
     * 根据主题id删除主题下的所有分面
     * @param topicId
     */
    @Modifying(clearAutomatically = true)
    @Transactional(rollbackFor = Exception.class)
    void deleteByTopicId(Long topicId);

    /**
     * 指定主题Id，查找分面
     * @param topicId 主题Id
     * @return List<Facet>
     */
    @Transactional(rollbackFor = Exception.class)
    List<Facet> findByTopicId(Long topicId);

    /**
     * 指定主题和分面名，查询对应分面
     * @param topicId
     * @param facetName
     * @return
     */
    Facet findByTopicIdAndFacetName(Long topicId, String facetName);
    /**
     * 指定父分面Id，查找分面
     * @param parentFacetId 父分面Id
     * @return List<Facet>
     */
    @Transactional(rollbackFor = Exception.class)
    List<Facet> findByParentFacetId(Long parentFacetId);

    /**
     * 指定父分面Id以及分面所在层，查找分面
     * @param parentFacetId 父分面Id
     * @param facetLayer 分面所在层
     * @return List<Facet>
     */
    @Transactional(rollbackFor = Exception.class)
    List<Facet> findByParentFacetIdAndFacetLayer(Long parentFacetId, Integer facetLayer);

    /**
     * 指定分面名、主题Id以及分面所在层，查找分面
     * @param topicId 主题Id
     * @param facetName 分面名
     * @param facetLayer 分面所在层
     * @return Facet
     */
    @Transactional(rollbackFor = Exception.class)
    Facet findByTopicIdAndFacetNameAndFacetLayer(Long topicId, String facetName, Integer facetLayer);

    /**
     * 指定分面所在层以及主题Id，查找分面
     * @param topicId 主题Id
     * @param facetLayer 分面所在层
     * @return List<Facet>
     */
    @Transactional(rollbackFor = Exception.class)
    List<Facet> findByTopicIdAndFacetLayer(Long topicId, Integer facetLayer);

    /**
     * 查询一门课程下的所有分面，连表查询
     * @param domainId 课程Id
     * @return
     */
    @Modifying(clearAutomatically = true)
    @Transactional(rollbackFor = Exception.class)
    @Query("SELECT \n" +
            "f.facetId,\n" +
            "f.facetName,\n" +
            "f.facetLayer,\n" +
            "f.parentFacetId,\n" +
            "f.topicId \n" +
            "FROM \n" +
            "Topic t,\n" +
            "Facet f \n" +
            "WHERE \n" +
            "t.domainId = ?1 AND \n" +
            "t.topicId = f .topicId")
    List<Facet> findAllFacetsByDomainId(Long domainId);

    /**
     * 查询一门课程下的各层分面，连表查询
     * @param domainId 课程Id
     * @param facetLayer 分面所在层
     * @return
     */
    @Modifying(clearAutomatically = true)
    @Transactional(rollbackFor = Exception.class)
    @Query("SELECT \n" +
            "f.facetId,\n" +
            "f.facetName,\n" +
            "f.facetLayer,\n" +
            "f.parentFacetId,\n" +
            "f.topicId \n" +
            "FROM \n" +
            "Topic t,\n" +
            "Facet f \n" +
            "WHERE \n" +
            "t.domainId = ?1 AND \n" +
            "t.topicId = f .topicId AND \n" +
            "f.facetLayer = ?2 ")
    List<Facet> findFacetsByDomainIdAndFacetLayer(Long domainId, Integer facetLayer);

    /**
     * 更新分面
     * @param facetId 分面Id
     * @param facetName 分面名
     * @param facetLayer 分面所在层
     * @param parentFacetId 父分面Id
     * @param topicId 主题Id
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
     * @param keyword
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Query("select new map(f.facetName,f.facetLayer,t.topicName,d.domainName) from Facet f, Topic t, Domain d where f.topicId = t.topicId and t.domainId = d.domainId and f.facetName like %?1%")
    List<Map<String,Object>> findFacetInformationByKeyword(String keyword);

}
