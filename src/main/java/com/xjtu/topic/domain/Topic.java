package com.xjtu.topic.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "topic")
public class Topic {

    @GeneratedValue
    @Id
    private Long topicId;

    private String topicName;
    private String topicUrl;
    private Long topicLayer;
    private Long domainId;

    public Topic() {
    }


    public Topic(String topicName, String topicUrl, Long topicLayer, Long domainId)
    {
        this.topicName = topicName;
        this.topicUrl = topicUrl;
        this.topicLayer = topicLayer;
        this.domainId = domainId;
    }

    public Topic(Long topicId, String topicName, String topicUrl)
    {
        this.topicId = topicId;
        this.topicName = topicName;
        this.topicUrl = topicUrl;
    }


    @Override
    public String toString() {
        return "Topic{" +
                "topicId=" + topicId +
                ", topicName='" + topicName + '\'' +
                ", topicUrl='" + topicUrl + '\'' +
                ", topicLayer=" + topicLayer +
                ", domainId=" + domainId +
                '}';
    }

    public Long getTopicId() {
        return topicId;
    }

    public void setTopicId(Long topicId) {
        this.topicId = topicId;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getTopicUrl() {
        return topicUrl;
    }

    public void setTopicUrl(String topicUrl) {
        this.topicUrl = topicUrl;
    }

    public Long getTopicLayer() {
        return topicLayer;
    }

    public void setTopicLayer(Long topicLayer) {
        this.topicLayer = topicLayer;
    }

    public Long getDomainId() {
        return domainId;
    }

    public void setDomainId(Long domainId) {
        this.domainId = domainId;
    }
}
