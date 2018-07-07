package com.xjtu.education.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 与网院联合，课程对应表,对应课程信息;
 *
 * @author yangkuan
 */
@Entity
@Table(name = "mapping_domain")
public class MappingDomian {
    /**
     * 网院课程id
     */
    @GeneratedValue
    @Id
    private Long courseId;

    /**
     * 网院课程名
     */
    private String courseName;

    /**
     * wiki课程名
     */
    private String domainName;

    /**
     * wiki课程id（外键，对应Domain）
     */
    private Long domainId;

    /**
     * 网院课程编码
     */
    private String courseCode;

    @Override
    public String toString() {
        return "MappingDomian{" +
                "courseId=" + courseId +
                ", courseName='" + courseName + '\'' +
                ", domainName='" + domainName + '\'' +
                ", domainId=" + domainId +
                ", courseCode='" + courseCode + '\'' +
                '}';
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public Long getDomainId() {
        return domainId;
    }

    public void setDomainId(Long domainId) {
        this.domainId = domainId;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }
}
