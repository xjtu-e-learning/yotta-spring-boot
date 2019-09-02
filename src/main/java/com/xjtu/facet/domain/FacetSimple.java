package com.xjtu.facet.domain;

/**
 * 简单分面对象
 * 1.分面名
 * 2.分面对应层级
 *
 * @author Lynn
 * @date 2019/08/21
 */
public class FacetSimple {

    public String facetName;
    public int facetLayer;
    //public Long parentFacetId

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

    public FacetSimple() {
    }

    /**
     * @param facetName
     * @param facetLayer
     */
    public FacetSimple(String facetName, int facetLayer) {
        super();
        this.facetName = facetName;
        this.facetLayer = facetLayer;
    }

}