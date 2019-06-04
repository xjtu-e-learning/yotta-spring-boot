package com.xjtu.user.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "permission")
public class Permission {

    @GeneratedValue
    @Id
    private Long permissionId;

    private Long userId;
    private String userName;
    private Long subjectId;
    private String subjectName;
    private Long domainId;
    private String domainName;


    public Permission() {
    }

    public Permission(Long userId, String userName, Long subjectId, String subjectName, Long domainId, String domainName) {
        this.userId = userId;
        this.userName = userName;
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.domainId = domainId;
        this.domainName = domainName;
    }


    @Override
    public String toString() {
        return "Permission{" +
                "permissionId=" + permissionId +
                ", userId=" + userId +
                ", userName='" + userName + '\'' +
                ", subjectId=" + subjectId +
                ", subjectName='" + subjectName + '\'' +
                ", domainId=" + domainId +
                ", domainName='" + domainName + '\'' +
                '}';
    }

    public Long getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(Long permissionId) {
        this.permissionId = permissionId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public Long getDomainId() {
        return domainId;
    }

    public void setDomainId(Long domainId) {
        this.domainId = domainId;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }
}
