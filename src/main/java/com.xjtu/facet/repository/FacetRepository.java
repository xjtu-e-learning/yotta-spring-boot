package com.xjtu.facet.repository;

import com.xjtu.facet.domain.Facet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 分面表数据库操作
 * @author yangkuan
 * @date 2018/03/12 10:29
 * */

public interface FacetRepository extends JpaRepository<Facet, Long>, JpaSpecificationExecutor<Facet> {

    /**
     * 指定主题Id，查找分面
     * @param topicId 主题Id
     * @return List<Facet>
     */
    @Transactional(rollbackFor = Exception.class)
    List<Facet> findByTopicId(Long topicId);

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
     * @param facetName 分面名
     * @param topicId 主题Id
     * @param facetLayer 分面所在层
     * @return Facet
     */
    @Transactional(rollbackFor = Exception.class)
    Facet findByFacetNameAndTopicIdAndFacetLayer(String facetName, Long topicId, Integer facetLayer);

    /**
     * 指定分面所在层以及主题Id，查找分面
     * @param facetLayer 分面所在层
     * @param topicId 主题Id
     * @return List<Facet>
     */
    @Transactional(rollbackFor = Exception.class)
    List<Facet> findByFacetLayerAndTopicId(Integer facetLayer, Long topicId);


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


}
