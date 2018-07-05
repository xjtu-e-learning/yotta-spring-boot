package com.xjtu.relation.domain;

import java.util.List;

/**
 * 上下位(Hyponymy)关系，主要用于知识主题爬取页面上下位关系的展示
 *
 * @
 */
public class HyponymyRelation {

    /**
     * 课程或主题id
     */
    private Long id = new Long(0);

    /**
     * 课程或主题名字
     */
    private String name;

    /**
     * 数据
     */
    private String data;

    /**
     * 下位主题，即孩子主题
     */
    private List<HyponymyRelation> children;

    public HyponymyRelation() {
    }

    public HyponymyRelation(Long id, String name, String data, List<HyponymyRelation> children) {
        this.id = id;
        this.name = name;
        this.data = data;
        this.children = children;
    }

    @Override
    public String toString() {
        return "HyponymyRelation{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", data='" + data + '\'' +
                ", children=" + children +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public List<HyponymyRelation> getChildren() {
        return children;
    }

    public void setChildren(List<HyponymyRelation> children) {
        this.children = children;
    }
}
