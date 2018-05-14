package com.xjtu.spider.bean;

/**
 * 碎片采集页面echart图展示碎片数据源：数据信息
 *
 * @author yuanhao
 * @date 2017/12/27 12:23
 */
public class EchartObj1 {

    private int value;
    private String name;

    @Override
    public String toString() {
        return "EchartObj1{" +
                "value='" + value + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EchartObj1() {

    }

    public EchartObj1(int value, String name) {

        this.value = value;
        this.name = name;
    }
}
