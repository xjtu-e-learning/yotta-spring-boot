package com.xjtu.topic.domain;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
/**
 * 移植来自爬虫系统的工具
 *
 * @author Lynn
 * @date 2019.07.17
 */

@Entity
@Table(name = "term")
public class Term {

    @GeneratedValue
    @Id
    private Long termId;
    private String termName;
    private String termUrl;
    private Long domainId;

    public Term(){
    }

    public Term(String termName, String termUrl) {
        this.termName = termName;
        this.termUrl = termUrl;
    }

    public Term(String termName, String termUrl, Long domainId) {
        super();
        this.termName = termName;
        this.termUrl = termUrl;
        this.domainId = domainId;
    }

    @Override
    public String toString() {
        return "Term{" +
                "termId=" + termId +
                ", termName='" + termName + '\'' +
                ", termUrl='" + termUrl + '\'' +
                ", domainId=" + domainId +
                '}';
    }

    public Long getTermId() {
        return termId;
    }

    public void setTermId(Long termId) {
        this.termId = termId;
    }

    public String getTermName() {
        return termName;
    }

    public void setTermName(String termName) {
        this.termName = termName;
    }

    public String getTermUrl() {
        return termUrl;
    }

    public void setTermUrl(String termUrl) {
        this.termUrl = termUrl;
    }

    public Long getDomainId() {
        return domainId;
    }

    public void setDomainId(Long domainId) {
        this.domainId = domainId;
    }
}
