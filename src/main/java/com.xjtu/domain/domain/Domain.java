package com.xjtu.domain.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "domain")
public class Domain {

    @GeneratedValue
    @Id
    private Long domainId;

    private String domainName;

    private Long subjectId;

    public Domain() {
    }

    public Domain(String domainName, Long subjectId) {
        this.domainName = domainName;
        this.subjectId = subjectId;
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

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    @Override
    public String toString() {
        return "Domain{" +
                "domainId=" + domainId +
                ", domainName='" + domainName + '\'' +
                ", subjectId=" + subjectId +
                '}';
    }
}
