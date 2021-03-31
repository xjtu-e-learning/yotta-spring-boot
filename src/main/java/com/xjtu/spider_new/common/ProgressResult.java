package com.xjtu.spider_new.common;

public class ProgressResult {

    private int topicCount;

    private int facetCount;

    private long assembleCount;

    private double progress;

    public ProgressResult(int topicCount, double progress) {
        this.topicCount = topicCount;
        this.progress = progress;
    }

    public ProgressResult(int topicCount, int facetCount, double progress) {
        this.topicCount = topicCount;
        this.facetCount = facetCount;
        this.progress = progress;
    }

    public ProgressResult(int topicCount, int facetCount, long assembleCount, double progress) {
        this.topicCount = topicCount;
        this.facetCount = facetCount;
        this.assembleCount = assembleCount;
        this.progress = progress;
    }

    public int getTopicCount() {
        return topicCount;
    }

    public void setTopicCount(int topicCount) {
        this.topicCount = topicCount;
    }

    public int getFacetCount() {
        return facetCount;
    }

    public void setFacetCount(int facetCount) {
        this.facetCount = facetCount;
    }

    public long getAssembleCount() {
        return assembleCount;
    }

    public void setAssembleCount(long assembleCount) {
        this.assembleCount = assembleCount;
    }

    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }
}
