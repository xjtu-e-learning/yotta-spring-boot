package com.xjtu.user_domain_list.domain;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @Author Qi Jingchao
 * @Date 2020/11/23 10:20
 */

@Entity
@Table(name = "user_domain_list")
public class UserDomainList {
    @GeneratedValue
    @Id
    private Long userId;
    /**
     * domainList 用户可见的课程名列表
     */
    private Long subjectId;
    private String domainList;

    public UserDomainList() {
    }

    public UserDomainList(Long userId, Long subjectId, String domainList) {
        this.userId = userId;
        this.subjectId = subjectId;
        this.domainList = domainList;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public String getDomainList() {
        return domainList;
    }

    public void setDomainList(String domainList) {
        this.domainList = domainList;
    }
}
