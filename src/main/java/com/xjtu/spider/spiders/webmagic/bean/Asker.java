package com.xjtu.spider.spiders.webmagic.bean;

/**
 * 提问者信息
 *
 * @author yangkuan
 * @date 2018/5/19
 */
public class Asker {

    private String askerName;
    private String askerReputation;
    private String askerAnswerCount;
    private String askerQuestionCount;
    private String askerViewCount;
    private String askerBestAnswerRate;

    public Asker(String askerName
            , String askerReputation
            , String askerAnswerCount
            , String askerQuestionCount
            , String askerViewCount
            , String askerBestAnswerRate) {
        this.askerName = askerName;
        this.askerReputation = askerReputation;
        this.askerAnswerCount = askerAnswerCount;
        this.askerQuestionCount = askerQuestionCount;
        this.askerViewCount = askerViewCount;
        this.askerBestAnswerRate = askerBestAnswerRate;
    }

    public Asker() {
    }

    @Override
    public String toString() {
        return "Asker{" +
                "askerName='" + askerName + '\'' +
                ", askerReputation='" + askerReputation + '\'' +
                ", askerAnswerCount='" + askerAnswerCount + '\'' +
                ", askerQuestionCount='" + askerQuestionCount + '\'' +
                ", askerViewCount='" + askerViewCount + '\'' +
                ", askerBestAnswerRate='" + askerBestAnswerRate + '\'' +
                '}';
    }

    public String getAskerName() {
        return askerName;
    }

    public void setAskerName(String askerName) {
        this.askerName = askerName;
    }

    public String getAskerReputation() {
        return askerReputation;
    }

    public void setAskerReputation(String askerReputation) {
        this.askerReputation = askerReputation;
    }

    public String getAskerAnswerCount() {
        return askerAnswerCount;
    }

    public void setAskerAnswerCount(String askerAnswerCount) {
        this.askerAnswerCount = askerAnswerCount;
    }

    public String getAskerQuestionCount() {
        return askerQuestionCount;
    }

    public void setAskerQuestionCount(String askerQuestionCount) {
        this.askerQuestionCount = askerQuestionCount;
    }

    public String getAskerViewCount() {
        return askerViewCount;
    }

    public void setAskerViewCount(String askerViewCount) {
        this.askerViewCount = askerViewCount;
    }

    public String getAskerBestAnswerRate() {
        return askerBestAnswerRate;
    }

    public void setAskerBestAnswerRate(String askerBestAnswerRate) {
        this.askerBestAnswerRate = askerBestAnswerRate;
    }
}
