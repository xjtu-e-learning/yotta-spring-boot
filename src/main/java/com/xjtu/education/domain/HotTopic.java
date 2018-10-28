package com.xjtu.education.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 热度主题实体
 *
 * @author yangkuan
 * @date 2018/10/24
 */
@Entity
@Table
public class HotTopic {
    @GeneratedValue
    @Id
    private Long hotTopicId;

    /**
     * 课程id
     */
    private Long domainId;

    public String getHotTopics() {
        return hotTopics;
    }

    public void setHotTopics(String hotTopics) {
        this.hotTopics = hotTopics;
    }

    /**
     * 热度主题列表
     * 形式：id1,id2,id3;id1,id3,id2;.....
     */
    private String hotTopics;
    /**
     * 创建时间
     */
    private Date createdTime;
    /**
     * 修改时间
     */
    private Date modifiedTime;

    public HotTopic() {
    }

    public Long getHotTopicId() {
        return hotTopicId;
    }

    public void setHotTopicId(Long hotTopicId) {
        this.hotTopicId = hotTopicId;
    }

    public Long getDomainId() {
        return domainId;
    }

    public void setDomainId(Long domainId) {
        this.domainId = domainId;
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
