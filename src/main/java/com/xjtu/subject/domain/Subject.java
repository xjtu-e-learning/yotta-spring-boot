package com.xjtu.subject.domain;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 学科类
 *
 * @author yangkuan
 * @date:2018/03/06 15:36
 */
@Entity
@Table(name = "subject")
public class Subject {

    @Id
    @GeneratedValue
    private Long subjectId;

    private String subjectName;

    private String note;

    public Subject() {

    }

    public Subject(String subjectName, String note) {
        this.subjectName = subjectName;
        this.note = note;
    }

    @Override
    public String toString() {
        return "Subject{" +
                "subjectId=" + subjectId +
                ", subjectName='" + subjectName + '\'' +
                ", note='" + note + '\'' +
                '}';
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public String getNote() {
        return note;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
