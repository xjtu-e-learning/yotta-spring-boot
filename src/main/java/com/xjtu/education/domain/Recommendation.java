package com.xjtu.education.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 推荐主题实体类
 *
 * @author yangkuan
 */
@Entity
@Table
public class Recommendation {

    @GeneratedValue
    @Id
    private Long recommendationId;

    /**
     * 课程id
     */
    private Long domainId;

    /**
     * 推荐主题列表
     * 形式：id1,id2,id3;id1,id3,id2;.....
     */
    private String recommendationTopics;

    /**
     * 用户id
     */
    private Long userId;
    /**
     * 创建时间
     */
    private Date createdTime;
    /**
     * 修改时间
     */
    private Date modifiedTime;

    public Long getRecommendationId() {
        return recommendationId;
    }

    public void setRecommendationId(Long recommendationId) {
        this.recommendationId = recommendationId;
    }

    public Long getDomainId() {
        return domainId;
    }

    public void setDomainId(Long domainId) {
        this.domainId = domainId;
    }

    public String getRecommendationTopics() {
        return recommendationTopics;
    }

    public void setRecommendationTopics(String recommendationTopics) {
        this.recommendationTopics = recommendationTopics;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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
