package com.xjtu.education.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 网院课程与wiki课程对照表
 *
 * @author yangkuan
 */
@Entity
@Table
public class CourseWangyuan {
    /**
     * 主键
     */
    @Id
    @GeneratedValue
    private Long courseId;
    /**
     * 网院课程名
     */
    private String courseName;
    /**
     * 维基百科课程名
     */
    private String courseWiki;

    /**
     * 网院课程编码
     */
    private String courseCode;

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

    public String getCourseWiki() {
        return courseWiki;
    }

    public void setCourseWiki(String courseWiki) {
        this.courseWiki = courseWiki;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }
}
