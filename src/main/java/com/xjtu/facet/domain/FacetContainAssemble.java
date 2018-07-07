package com.xjtu.facet.domain;


import java.util.List;

/**
 * 分面类，包含分面下的子分面以及碎片
 *
 * @author yangkuan
 * @date 2018/03/15 15:23
 */

public class FacetContainAssemble extends Facet {


    List<Object> children;

    Integer childrenNumber = 0;
    String type = "branch";


    /**
     * 是否包含子级分面
     */
    boolean containChildrenFacet = false;

    public FacetContainAssemble() {
    }

    public FacetContainAssemble(List<Object> children, Integer childrenNumber, String type) {
        this.children = children;
        this.childrenNumber = childrenNumber;
        this.type = type;
    }

    public FacetContainAssemble(String facetName, Integer facetLayer, Long topicId, Long parentFacetId, List<Object> children, Integer childrenNumber, String type) {
        super(facetName, facetLayer, topicId, parentFacetId);
        this.children = children;
        this.childrenNumber = childrenNumber;
        this.type = type;
    }

    @Override
    public String toString() {
        return "FacetContainAssemble{" +
                "children=" + children +
                ", childrenNumber=" + childrenNumber +
                ", type='" + type + '\'' +
                '}';
    }

    public List<Object> getChildren() {
        return children;
    }

    public void setChildren(List<Object> children) {
        this.children = children;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getChildrenNumber() {
        return childrenNumber;
    }

    public void setChildrenNumber(Integer childrenNumber) {
        this.childrenNumber = childrenNumber;
    }

    public boolean isContainChildrenFacet() {
        return containChildrenFacet;
    }

    public void setContainChildrenFacet(boolean containChildrenFacet) {
        this.containChildrenFacet = containChildrenFacet;
    }

    public void setFacet(Facet facet) {
        setFacetId(facet.getFacetId());
        setFacetName(facet.getFacetName());
        setTopicId(facet.getTopicId());
        setFacetLayer(facet.getFacetLayer());
        setParentFacetId(facet.getParentFacetId());
    }
}
