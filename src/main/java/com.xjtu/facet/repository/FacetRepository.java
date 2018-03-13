package com.xjtu.facet.repository;

import com.xjtu.facet.domain.Facet;
import com.xjtu.topic.domain.Topic;
import io.swagger.models.auth.In;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface FacetRepository extends JpaRepository<Facet, Long>, JpaSpecificationExecutor<Facet> {

    @Transactional
    List<Facet> findByTopicId(Long topicId);

    @Transactional
    List<Facet> findByParentFacetId(Long parentFacetId);

    @Transactional
    List<Facet> findByParentFacetIdAndFacetLayer(Long parentFacetId, Integer facetLayer);

    @Transactional
    Facet findByFacetNameAndTopicIdAndFacetLayer(String facetName, Long topicId, Integer facetLayer);

    @Transactional
    List<Facet> findByFacetLayerAndTopicId(Integer facetLayer, Long topicId);

    /**
     * 修改主题名字
     * */
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("update Facet f set f.facetId = ?1, f.facetName = ?2, f.facetLayer = ?3, " +
            "f.parentFacetId = ?4, f.topicId = ?5 where f.facetId = ?1")
    void updateFacetById(Long facetId, String facetName,
                         Integer facetLayer, Long parentFacetId, Long topicId);


}
