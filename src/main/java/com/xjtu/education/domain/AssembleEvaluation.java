package com.xjtu.education.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 碎片质量
 *
 * @author yangkuan
 */
@Entity
@Table
public class AssembleEvaluation {
    /**
     * 主键
     */
    @GeneratedValue
    @Id
    private Long evaluationId;
    /**
     * 课程id
     */
    private Long assembleId;

    /**
     * 用户id
     */
    private Long userId;
    /**
     * 碎片质量
     */
    private Integer value;

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
        return "AssembleEvaluation{" +
                "evaluationId=" + evaluationId +
                ", assembleId=" + assembleId +
                ", userId=" + userId +
                ", value=" + value +
                ", createdTime=" + createdTime +
                ", modifiedTime=" + modifiedTime +
                '}';
    }

    public Long getEvaluationId() {
        return evaluationId;
    }

    public void setEvaluationId(Long evaluationId) {
        this.evaluationId = evaluationId;
    }

    public Long getAssembleId() {
        return assembleId;
    }

    public void setAssembleId(Long assembleId) {
        this.assembleId = assembleId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
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
