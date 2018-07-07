package com.xjtu.topic.domain;

import com.xjtu.facet.domain.Facet;

import java.util.List;

/**
 * 主题类，包含主题下的分面
 *
 * @author yangkuan
 * @date 2018/03/15 15:18
 */
public class TopicContainFacet extends Topic {
    private List<Facet> children;
    private Integer childrenNumber;

    public TopicContainFacet() {
    }

    public TopicContainFacet(String topicName, String topicUrl, Long topicLayer, Long domainId, List<Facet> children) {
        super(topicName, topicUrl, topicLayer, domainId);
        this.children = children;
    }

    @Override
    public String toString() {
        return "TopicContainFacet{" +
                "children=" + children +
                '}';
    }

    public List<Facet> getChildren() {
        return children;
    }

    public void setChildren(List<Facet> children) {
        this.children = children;
    }

    public Integer getChildrenNumber() {
        return childrenNumber;
    }

    public void setChildrenNumber(Integer childrenNumber) {
        this.childrenNumber = childrenNumber;
    }

    public void setTopic(Topic topic) {
        setTopicId(topic.getTopicId());
        setTopicName(topic.getTopicName());
        setTopicUrl(topic.getTopicUrl());
        setTopicLayer(topic.getTopicLayer());
        setDomainId(topic.getDomainId());
    }
}
