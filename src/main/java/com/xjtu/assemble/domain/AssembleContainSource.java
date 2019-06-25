package com.xjtu.assemble.domain;

/**
 * 碎片类，包含数据源名
 *
 * @author yangkuan
 */
public class AssembleContainSource extends Assemble {

    String sourceName = "";

    public AssembleContainSource() {
    }

    @Override
    public String toString() {
        return "AssembleContainSource{" +
                "sourceName='" + sourceName + '\'' +
                ", assembleId=" + assembleId +
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

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }
}
