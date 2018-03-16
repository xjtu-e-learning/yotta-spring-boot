package com.xjtu.assemble.domain;

import javax.persistence.*;

/**
 * 碎片类，对应数据库中的碎片表assemble
 * @author yangkuan
 * @date 2018/03/15 14:46
 * */
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

    public Assemble() {
    }

    public Assemble(String assembleContent, String assembleText, String assembleScratchTime, Long facetId) {
        this.assembleContent = assembleContent;
        this.assembleText = assembleText;
        this.assembleScratchTime = assembleScratchTime;
        this.facetId = facetId;
    }

    @Override
    public String toString() {
        return "Assemble{" +
                "assembleId=" + assembleId +
                ", assembleContent='" + assembleContent + '\'' +
                ", assembleText='" + assembleText + '\'' +
                ", assembleScratchTime='" + assembleScratchTime + '\'' +
                ", facetId=" + facetId +
                '}';
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
}
