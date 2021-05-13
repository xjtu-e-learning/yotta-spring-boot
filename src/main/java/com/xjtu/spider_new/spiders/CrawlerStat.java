package com.xjtu.spider_new.spiders;

import java.util.List;

public class CrawlerStat {

    private int totalProcessedPages;
    private long totalLinks;
    private long totalTextSize;
    private List<String> facetNameList;

    public int getTotalProcessedPages() {
        return totalProcessedPages;
    }

    public void setTotalProcessedPages(int totalProcessedPages) {
        this.totalProcessedPages = totalProcessedPages;
    }

    public void incProcessedPages() {
        this.totalProcessedPages++;
    }

    public long getTotalLinks() {
        return totalLinks;
    }

    public void setTotalLinks(long totalLinks) {
        this.totalLinks = totalLinks;
    }

    public long getTotalTextSize() {
        return totalTextSize;
    }

    public void setTotalTextSize(long totalTextSize) {
        this.totalTextSize = totalTextSize;
    }

    public void incTotalLinks(int count) {
        this.totalLinks += count;
    }

    public void incTotalTextSize(int count) {
        this.totalTextSize += count;
    }

    public List<String> getFacetNameList() {
        return facetNameList;
    }

    public void setFacetNameList(List<String> facetNameList) {
        this.facetNameList = facetNameList;
    }
}
