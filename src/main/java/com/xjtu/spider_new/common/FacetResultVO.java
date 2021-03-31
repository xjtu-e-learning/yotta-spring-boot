package com.xjtu.spider_new.common;

import java.util.*;

public class FacetResultVO<T> {
    private Boolean running;

    private String message;

    private T facets;

    public FacetResultVO() {
    }

    public FacetResultVO(Boolean running) {
        this.running = running;
    }

    public FacetResultVO(Boolean running, String message, T facets) {
        this.running = running;
        this.message = message;
        this.facets = facets;
    }

    public FacetResultVO(Boolean running, String message) {
        this.running = running;
        this.message = message;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(Boolean running) {
        this.running = running;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getFacets() {
        return facets;
    }

    public void setFacets(T facets) {
        this.facets = facets;
    }

    @Override
    public String toString() {
        return "FacetResultVO{" +
                "running=" + running +
                ", message='" + message + '\'' +
                ", facets=" + facets +
                '}';
    }
}
