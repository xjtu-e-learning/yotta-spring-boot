package com.xjtu.relation.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 主题上下位关系表
 *
 * @author yangkuan
 * @data 2018/03/17 12:39
 */
@Entity
@Table(name = "relation")
public class Relation {

    @Id
    @GeneratedValue
    private Long relationId;
    /**
     * 父主题id
     */
    private Long parentTopicId;
    /**
     * 子主题id
     */
    private Long childTopicId;

    /**
     * 关系所属课程
     */
    private Long domainId;

    public Relation() {

    }

    public Relation(Long parentTopicId, Long childTopicId, Long domainId) {
        this.parentTopicId = parentTopicId;
        this.childTopicId = childTopicId;
        this.domainId = domainId;
    }

    @Override
    public String toString() {
        return "Relation{" +
                "relationId=" + relationId +
                ", parentTopicId=" + parentTopicId +
                ", childTopicId=" + childTopicId +
                ", domainId=" + domainId +
                '}';
    }

    public Long getParentTopicId() {
        return parentTopicId;
    }

    public void setParentTopicId(Long parentTopicId) {
        this.parentTopicId = parentTopicId;
    }

    public Long getChildTopicId() {
        return childTopicId;
    }

    public void setChildTopicId(Long childTopicId) {
        this.childTopicId = childTopicId;
    }

    public Long getRelationId() {
        return relationId;
    }

    public void setRelationId(Long relationId) {
        this.relationId = relationId;
    }

    public Long getDomainId() {
        return domainId;
    }

    public void setDomainId(Long domainId) {
        this.domainId = domainId;
    }
}
