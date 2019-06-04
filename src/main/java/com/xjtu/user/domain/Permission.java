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

    private Long domainId;

    public Permission() {
    }

    public Permission(Long userId, String userName, Long subjectId, Long domainId) {
        this.userId = userId;
        this.userName = userName;
        this.subjectId = subjectId;
        this.domainId = domainId;
    }

    @Override
    public String toString() {
        return "Permission{" +
                "permissionId=" + permissionId +
                ", userId=" + userId +
                ", userName='" + userName + '\'' +
                ", subjectId=" + subjectId +
                ", domainId=" + domainId +
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

    public Long getDomainId() {
        return domainId;
    }

    public void setDomainId(Long domainId) {
        this.domainId = domainId;
    }
}
