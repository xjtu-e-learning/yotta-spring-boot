package com.xjtu.statistics.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 数据统计表
 *
 * @author yangkuan
 * @date 2018/10/30
 */
@Entity
@Table
public class Statistics {

    @Id
    @GeneratedValue
    private Long statisticsId;

    private Long domainId;
    private String domainName;

    private Integer topicNumber;
    private Integer dependencyNumber;
    private Integer firstLayerFacetNumber;
    private Integer secondLayerFacetNumber;
    private Integer thirdLayerFacetNumber;
    private Integer facetNumber;
    private Integer assembleNumber;

    public Statistics() {
    }

    public Long getStatisticsId() {
        return statisticsId;
    }

    public void setStatisticsId(Long statisticsId) {
        this.statisticsId = statisticsId;
    }

    public Long getDomainId() {
        return domainId;
    }

    public void setDomainId(Long domainId) {
        this.domainId = domainId;
    }


    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public Integer getTopicNumber() {
        return topicNumber;
    }

    public void setTopicNumber(Integer topicNumber) {
        this.topicNumber = topicNumber;
    }

    public Integer getDependencyNumber() {
        return dependencyNumber;
    }

    public void setDependencyNumber(Integer dependencyNumber) {
        this.dependencyNumber = dependencyNumber;
    }

    public Integer getFirstLayerFacetNumber() {
        return firstLayerFacetNumber;
    }

    public void setFirstLayerFacetNumber(Integer firstLayerFacetNumber) {
        this.firstLayerFacetNumber = firstLayerFacetNumber;
    }

    public Integer getSecondLayerFacetNumber() {
        return secondLayerFacetNumber;
    }

    public void setSecondLayerFacetNumber(Integer secondLayerFacetNumber) {
        this.secondLayerFacetNumber = secondLayerFacetNumber;
    }

    public Integer getThirdLayerFacetNumber() {
        return thirdLayerFacetNumber;
    }

    public void setThirdLayerFacetNumber(Integer thirdLayerFacetNumber) {
        this.thirdLayerFacetNumber = thirdLayerFacetNumber;
    }

    public Integer getFacetNumber() {
        return facetNumber;
    }

    public void setFacetNumber(Integer facetNumber) {
        this.facetNumber = facetNumber;
    }

    public Integer getAssembleNumber() {
        return assembleNumber;
    }

    public void setAssembleNumber(Integer assembleNumber) {
        this.assembleNumber = assembleNumber;
    }
}
