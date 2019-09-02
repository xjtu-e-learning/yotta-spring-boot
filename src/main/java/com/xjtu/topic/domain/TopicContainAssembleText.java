package com.xjtu.topic.domain;

public class TopicContainAssembleText extends Topic {
    private String text;    //topic的碎片信息

    public TopicContainAssembleText() {
    }

    public TopicContainAssembleText(String topicName, String topicUrl, Long topicLayer, Long domainId, String text) {
        super(topicName, topicUrl, topicLayer, domainId);
        this.text = text;
    }

    public TopicContainAssembleText(Topic topic)
    {
        super(topic.getTopicName(),
                topic.getTopicUrl(),
                topic.getTopicLayer(),
                topic.getDomainId());
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "TopicContainAssembleText{" +
                "text='" + text + '\'' +
                '}';
    }
}
