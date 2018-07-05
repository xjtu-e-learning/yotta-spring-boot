package com.xjtu.dependency.domain;

/**
 * 查询主题依赖关系所需要，继承Dependency，并加入起始终止主题名
 *
 * @author yangkuan
 * @date 2018/03/21 13:05
 */

public class DependencyContainName extends Dependency {

    /**
     * 起始主题名
     */
    private String startTopicName;

    /**
     * 终止主题名
     */
    private String endTopicName;

    public DependencyContainName() {
    }

    public DependencyContainName(Long startTopicId, Long endTopicId, float confidence, Long domainId, String startTopicName, String endTopicName) {
        super(startTopicId, endTopicId, confidence, domainId);
        this.startTopicName = startTopicName;
        this.endTopicName = endTopicName;
    }

    public DependencyContainName(Dependency dependency) {
        super(dependency.getStartTopicId()
                , dependency.getEndTopicId()
                , dependency.getConfidence()
                , dependency.getDomainId());
    }

    @Override
    public String toString() {
        return "DependencyContainName{" +
                "startTopicName='" + startTopicName + '\'' +
                ", endTopicName='" + endTopicName + '\'' +
                '}';
    }

    public String getStartTopicName() {
        return startTopicName;
    }

    public void setStartTopicName(String startTopicName) {
        this.startTopicName = startTopicName;
    }

    public String getEndTopicName() {
        return endTopicName;
    }

    public void setEndTopicName(String endTopicName) {
        this.endTopicName = endTopicName;
    }
}
