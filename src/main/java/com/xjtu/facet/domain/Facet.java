package com.xjtu.facet.domain;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * facet表
 * 与原表有所改动（将facet和facet_relation整合在一个表中）
 * 注：由于一级分面没有父分面，因此可以设一级分面的父分面默认id为0
 *
 * @author yangkuan
 * @date 2018/03/09 15:15
 */
@Entity
@Table(name = "facet")
public class Facet {

    @GeneratedValue
    @Id
    private Long facetId;

    private String facetName;
    private Integer facetLayer;
    private Long parentFacetId = new Long(0);
    private Long topicId;

    public Facet() {
    }

    public Facet(String facetName, Integer facetLayer, Long topicId, Long parentFacetId) {
        this.facetName = facetName;
        this.facetLayer = facetLayer;
        this.topicId = topicId;
        this.parentFacetId = parentFacetId;
    }

    @Override
    public String toString() {
        return "Facet{" +
                "facetId=" + facetId +
                ", facetName='" + facetName + '\'' +
                ", facetLayer=" + facetLayer +
                ", topicId=" + topicId +
                ", parentFacetId=" + parentFacetId +
                '}';
    }

    public Long getFacetId() {
        return facetId;
    }

    public void setFacetId(Long facetId) {
        this.facetId = facetId;
    }

    public String getFacetName() {
        return facetName;
    }

    public void setFacetName(String facetName) {
        this.facetName = facetName;
    }

    public Integer getFacetLayer() {
        return facetLayer;
    }

    public void setFacetLayer(Integer facetLayer) {
        this.facetLayer = facetLayer;
    }

    public Long getTopicId() {
        return topicId;
    }

    public void setTopicId(Long topicId) {
        this.topicId = topicId;
    }

    public Long getParentFacetId() {
        return parentFacetId;
    }

    public void setParentFacetId(Long parentFacetId) {
        this.parentFacetId = parentFacetId;
    }
}
