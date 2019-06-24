package com.xjtu.assemble.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 碎片类，包含了碎片类型（type:leaf;flag:fragment），主要用于API生成分面树
 *
 * @author yangkuan
 * @date 2018/03/16 19:00
 */
@JsonIgnoreProperties(value = "assembleText")
public class AssembleContainType extends Assemble {
    String type = "leaf";
    String flag = "fragment";
    String url = "";

    public AssembleContainType() {
    }

    @Override
    public String toString() {
        return "AssembleContainType{" +
                "type='" + type + '\'' +
                ", flag='" + flag + '\'' +
                ", url='" + url + '\'' +
                ", assembleId=" + assembleId +
                ", assembleContent='" + assembleContent + '\'' +
                ", assembleText='" + assembleText + '\'' +
                ", assembleScratchTime='" + assembleScratchTime + '\'' +
                ", facetId=" + facetId +
                ", sourceId=" + sourceId +
                '}';
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    public void setAssemble(Assemble assemble) {
        setAssembleId(assemble.getAssembleId());
        setAssembleContent(assemble.getAssembleContent());
        setAssembleText(assemble.getAssembleText());
        setAssembleScratchTime(assemble.getAssembleScratchTime());
        setFacetId(assemble.getFacetId());
    }

}
