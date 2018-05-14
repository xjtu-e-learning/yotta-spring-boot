package com.xjtu.spider.bean;

import java.util.List;

/**
 * 碎片采集页面echart图展示碎片数据源：数据和坐标轴信息
 *
 * @author yuanhao
 * @date 2017/12/27 14:31
 */
public class EchartObj2 {

    private List<EchartObj1> echartObj1s;
    private List<String> sources;

    @Override
    public String toString() {
        return "EchartObj2{" +
                "echartObj1s=" + echartObj1s +
                ", sources=" + sources +
                '}';
    }

    public List<EchartObj1> getEchartObj1s() {
        return echartObj1s;
    }

    public void setEchartObj1s(List<EchartObj1> echartObj1s) {
        this.echartObj1s = echartObj1s;
    }

    public List<String> getSources() {
        return sources;
    }

    public void setSources(List<String> sources) {
        this.sources = sources;
    }

    public EchartObj2() {

    }

    public EchartObj2(List<EchartObj1> echartObj1s, List<String> sources) {

        this.echartObj1s = echartObj1s;
        this.sources = sources;
    }
}
