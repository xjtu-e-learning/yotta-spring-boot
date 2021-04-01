package com.xjtu.spider_new.domain;

import javax.persistence.*;

/**
 * missing_record表
 * 记录缺失的数据
 * @author hongzhenjie
 * @date 2021年3月30日 19点33分
 *
 */
@Entity
@Table(name = "missing_record")
public class MissingRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 0 代表缺失的数据是 domain
     * 1 代表缺失的数据是 topic
     * 2 代表缺失的数据是 facet
     */
    private int type;

    /**
     * 具体的domain/topic/facet的Id
     * 根据这个id可以获取到相应的domain/topic/facet
     */
    private Long specificId;

    public MissingRecord() {
    }

    public MissingRecord(int type, Long specificId) {
        this.type = type;
        this.specificId = specificId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Long getSpecificId() {
        return specificId;
    }

    public void setSpecificId(Long specificId) {
        this.specificId = specificId;
    }
}
