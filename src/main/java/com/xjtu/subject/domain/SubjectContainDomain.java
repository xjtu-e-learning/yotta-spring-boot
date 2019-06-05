package com.xjtu.subject.domain;

import com.xjtu.domain.domain.Domain;

import java.util.List;

/**
 * 学科类，包含学科下的课程列表
 *
 * @author yangkuan
 * @date:2018/03/06 15:36
 */

public class SubjectContainDomain extends Subject {
    private List<Domain> domains;

    @Override
    public String toString() {
        return "SubjectContainDomain{" +
                "domains=" + domains +
                '}';
    }

    public List<Domain> getDomains() {
        return domains;
    }



    public void setDomains(List<Domain> domains) {
        this.domains = domains;
    }

    public SubjectContainDomain() {
        super();
    }

    public SubjectContainDomain(Long subjectId, String subjectName, String note, List<Domain> domains) {
        super(subjectName, note);
        this.setSubjectId(subjectId);
        this.domains = domains;
    }



}
