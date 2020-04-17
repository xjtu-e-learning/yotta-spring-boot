package com.xjtu.timerTask_assemble_spider.spiders.webmagic.bean;

import java.util.List;


/**
 * 从网页爬取的碎片列表
 */
public class Assembles {
    /**
     * 碎片内容（HTML）
     */
    List<String> assembleContents;

    /**
     * 碎片文本（纯文本）
     */
    List<String> assembleTexts;


    public Assembles(List<String> assembleContents, List<String> assembleTexts) {
        this.assembleContents = assembleContents;
        this.assembleTexts = assembleTexts;
    }

    public Assembles() {
    }

    @Override
    public String toString() {
        return "Assembles{" +
                "assembleContents=" + assembleContents +
                ", assembleTexts=" + assembleTexts +
                '}';
    }

    public List<String> getAssembleContents() {
        return assembleContents;
    }

    public void setAssembleContents(List<String> assembleContents) {
        this.assembleContents = assembleContents;
    }

    public List<String> getAssembleTexts() {
        return assembleTexts;
    }

    public void setAssembleTexts(List<String> assembleTexts) {
        this.assembleTexts = assembleTexts;
    }
}
