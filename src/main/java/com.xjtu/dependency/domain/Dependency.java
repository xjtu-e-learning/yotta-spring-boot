package com.xjtu.dependency.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 主题间依赖关系表
 * @author yangkuan
 * @data 2018/03/17 12:39
 * */
@Entity
@Table(name = "dependency")
public class Dependency {

    @Id
    @GeneratedValue
    private Long dependencyId;
    /**
     * 起始主题id
     * */
    private Long startTopicId;

    /**
     * 终止主题id
     * */
    private Long endTopicId;

    /**
     * 置信度
    * */
    private float confidence;

    public Dependency() {
    }

    public Dependency(Long startTopicId, Long endTopicId, float confidence) {
        this.startTopicId = startTopicId;
        this.endTopicId = endTopicId;
        this.confidence = confidence;
    }

    @Override
    public String toString() {
        return "Dependency{" +
                "startTopicId=" + startTopicId +
                ", endTopicId=" + endTopicId +
                ", confidence=" + confidence +
                '}';
    }

    public Long getStartTopicId() {
        return startTopicId;
    }

    public void setStartTopicId(Long startTopicId) {
        this.startTopicId = startTopicId;
    }

    public Long getEndTopicId() {
        return endTopicId;
    }

    public void setEndTopicId(Long endTopicId) {
        this.endTopicId = endTopicId;
    }

    public float getConfidence() {
        return confidence;
    }

    public void setConfidence(float confidence) {
        this.confidence = confidence;
    }
}
