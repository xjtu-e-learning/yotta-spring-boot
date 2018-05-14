package com.xjtu.spider.bean;

/**
 * 碎片类
 *
 * @author 郑元浩 
 * @date 2016年11月6日
 */
public class AssembleFragment {

    public int fragmentID;
    public String fragmentContent;
    public String text;
    public String fragmentScratchTime;
    public int termID;
    public String termName;
    public String facetName;
    public int facetLayer;
    public String className;
    public String sourceName;

    @Override
    public String toString() {
        return "AssembleFragment{" +
                "fragmentID=" + fragmentID +
                ", fragmentContent='" + fragmentContent + '\'' +
                ", text='" + text + '\'' +
                ", fragmentScratchTime='" + fragmentScratchTime + '\'' +
                ", termID=" + termID +
                ", termName='" + termName + '\'' +
                ", facetName='" + facetName + '\'' +
                ", facetLayer=" + facetLayer +
                ", className='" + className + '\'' +
                ", sourceName='" + sourceName + '\'' +
                '}';
    }

    public int getFragmentID() {
        return fragmentID;
    }

    public void setFragmentID(int fragmentID) {
        this.fragmentID = fragmentID;
    }

    public String getFragmentContent() {
        return fragmentContent;
    }

    public void setFragmentContent(String fragmentContent) {
        this.fragmentContent = fragmentContent;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getFragmentScratchTime() {
        return fragmentScratchTime;
    }

    public void setFragmentScratchTime(String fragmentScratchTime) {
        this.fragmentScratchTime = fragmentScratchTime;
    }

    public int getTermID() {
        return termID;
    }

    public void setTermID(int termID) {
        this.termID = termID;
    }

    public String getTermName() {
        return termName;
    }

    public void setTermName(String termName) {
        this.termName = termName;
    }

    public String getFacetName() {
        return facetName;
    }

    public void setFacetName(String facetName) {
        this.facetName = facetName;
    }

    public int getFacetLayer() {
        return facetLayer;
    }

    public void setFacetLayer(int facetLayer) {
        this.facetLayer = facetLayer;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public AssembleFragment() {

    }

    public AssembleFragment(int fragmentID, String fragmentContent, String text, String fragmentScratchTime, int termID, String termName, String facetName, int facetLayer, String className, String sourceName) {

        this.fragmentID = fragmentID;
        this.fragmentContent = fragmentContent;
        this.text = text;
        this.fragmentScratchTime = fragmentScratchTime;
        this.termID = termID;
        this.termName = termName;
        this.facetName = facetName;
        this.facetLayer = facetLayer;
        this.className = className;
        this.sourceName = sourceName;
    }
}
