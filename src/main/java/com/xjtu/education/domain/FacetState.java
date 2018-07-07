package com.xjtu.education.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 分面学习状态
 *
 * @author yangkuan
 * @date 2018/06/28 21:21
 */
@Entity
@Table
public class FacetState {
    /**
     * 主键
     */
    @GeneratedValue
    @Id
    private Long stateId;
    /**
     * 课程id
     */
    private Long domainId;
    /**
     * 主题id
     */
    private Long topicId;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 分面状态列表（以，分割开的）
     */
    private String states;

    /**
     * 创建时间
     */
    private Date createdTime;

    /**
     * 修改时间
     */
    private Date modifiedTime;

    @Override
    public String toString() {
        return "FacetState{" +
                "stateId=" + stateId +
                ", domainId=" + domainId +
                ", userId=" + userId +
                ", states='" + states + '\'' +
                ", createdTime=" + createdTime +
                ", modifiedTime=" + modifiedTime +
                '}';
    }

    public Long getTopicId() {
        return topicId;
    }

    public void setTopicId(Long topicId) {
        this.topicId = topicId;
    }

    public Long getStateId() {
        return stateId;
    }

    public void setStateId(Long stateId) {
        this.stateId = stateId;
    }

    public Long getDomainId() {
        return domainId;
    }

    public void setDomainId(Long domainId) {
        this.domainId = domainId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getStates() {
        return states;
    }

    public void setStates(String states) {
        this.states = states;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Date getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(Date modifiedTime) {
        this.modifiedTime = modifiedTime;
    }
}
