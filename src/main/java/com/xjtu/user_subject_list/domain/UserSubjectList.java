package com.xjtu.user_subject_list.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @Author Qi Jingchao
 * @Date 2020/11/23 16:43
 */

@Entity
@Table(name = "user_subject_list")
public class UserSubjectList {
    @GeneratedValue
    @Id
    private Long userId;
    /**
     * domainList 用户可见的课程名列表
     */
    private String subjectList;

    public UserSubjectList() {
    }

    public UserSubjectList(Long userId, String subjectList) {
        this.userId = userId;
        this.subjectList = subjectList;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getSubjectList() {
        return subjectList;
    }

    public void setSubjectList(String subjectList) {
        this.subjectList = subjectList;
    }

    @Override
    public String toString() {
        return "UserSubject{" +
                "userId=" + userId +
                ", subjectList='" + subjectList + '\'' +
                '}';
    }
}
