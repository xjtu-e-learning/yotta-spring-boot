package com.xjtu.source.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 数据源
 *
 * @author yuanhao
 * @date 2018/3/3 22:33
 */
@Entity
@Table(name = "source")
public class Source {

    /**
     * @Column(name = "sourceId", columnDefinition="tinyint")
     */
    @Id
    @GeneratedValue
    private Long sourceId;

    private String sourceName;

    private String note;

    public Source() {
    }

    public Source(String sourceName, String note) {

        this.sourceName = sourceName;
        this.note = note;
    }

    @Override
    public String toString() {
        return "Source{" +
                "sourceId=" + sourceId +
                ", sourceName='" + sourceName + '\'' +
                ", note='" + note + '\'' +
                '}';
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
