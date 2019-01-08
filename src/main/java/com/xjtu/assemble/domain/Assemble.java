package com.xjtu.assemble.domain;

import javax.persistence.*;

/**
 * 碎片类，对应数据库中的碎片表assemble
 *
 * @author yangkuan
 * @date 2018/03/15 14:46
 */
@Entity
@Table(name = "assemble")
public class Assemble {

    @Id
    @GeneratedValue
    Long assembleId;

    @Column(columnDefinition = "longtext")
    String assembleContent;
    @Column(columnDefinition = "longtext")
    String assembleText;
    String assembleScratchTime;
    Long facetId;
    Long sourceId = 0L;
    Long domainId;

    String url;

    String type = "text";

    public Assemble() {
    }


    @Override
    public String toString() {
        return "Assemble{" +
                "assembleId=" + assembleId +
                ", assembleContent='" + assembleContent + '\'' +
                ", assembleText='" + assembleText + '\'' +
                ", assembleScratchTime='" + assembleScratchTime + '\'' +
                ", facetId=" + facetId +
                ", sourceId=" + sourceId +
                ", domainId=" + domainId +
                ", url='" + url + '\'' +
                ", type='" + type + '\'' +
                '}';
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getDomainId() {
        return domainId;
    }

    public void setDomainId(Long domainId) {
        this.domainId = domainId;
    }

    public Long getAssembleId() {
        return assembleId;
    }

    public void setAssembleId(Long assembleId) {
        this.assembleId = assembleId;
    }

    public String getAssembleContent() {
        return assembleContent;
    }

    public void setAssembleContent(String assembleContent) {
        this.assembleContent = assembleContent;
    }

    public String getAssembleText() {
        return assembleText;
    }

    public void setAssembleText(String assembleText) {
        this.assembleText = assembleText;
    }

    public String getAssembleScratchTime() {
        return assembleScratchTime;
    }

    public void setAssembleScratchTime(String assembleScratchTime) {
        this.assembleScratchTime = assembleScratchTime;
    }

    public Long getFacetId() {
        return facetId;
    }

    public void setFacetId(Long facetId) {
        this.facetId = facetId;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
