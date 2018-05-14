package com.xjtu.spider.spiders.webmagic.bean;

/**
 * @author yuanhao
 * @date 2018/4/10 16:58
 */
public class FragmentContentAsker {

    private String asker_name;
    private String asker_reputation;
    private String asker_answerCount;
    private String asker_questionCount;
    private String asker_viewCount;
    private String asker_best_answer_rate;

    @Override
    public String toString() {
        return "FragmentContentAsker{" +
                "asker_name='" + asker_name + '\'' +
                ", asker_reputation='" + asker_reputation + '\'' +
                ", asker_answerCount='" + asker_answerCount + '\'' +
                ", asker_questionCount='" + asker_questionCount + '\'' +
                ", asker_viewCount='" + asker_viewCount + '\'' +
                ", asker_best_answer_rate='" + asker_best_answer_rate + '\'' +
                '}';
    }

    public String getAsker_name() {
        return asker_name;
    }

    public void setAsker_name(String asker_name) {
        this.asker_name = asker_name;
    }

    public String getAsker_reputation() {
        return asker_reputation;
    }

    public void setAsker_reputation(String asker_reputation) {
        this.asker_reputation = asker_reputation;
    }

    public String getAsker_answerCount() {
        return asker_answerCount;
    }

    public void setAsker_answerCount(String asker_answerCount) {
        this.asker_answerCount = asker_answerCount;
    }

    public String getAsker_questionCount() {
        return asker_questionCount;
    }

    public void setAsker_questionCount(String asker_questionCount) {
        this.asker_questionCount = asker_questionCount;
    }

    public String getAsker_viewCount() {
        return asker_viewCount;
    }

    public void setAsker_viewCount(String asker_viewCount) {
        this.asker_viewCount = asker_viewCount;
    }

    public String getAsker_best_answer_rate() {
        return asker_best_answer_rate;
    }

    public void setAsker_best_answer_rate(String asker_best_answer_rate) {
        this.asker_best_answer_rate = asker_best_answer_rate;
    }

    public FragmentContentAsker() {

    }

    public FragmentContentAsker(String asker_name, String asker_reputation, String asker_answerCount, String asker_questionCount, String asker_viewCount, String asker_best_answer_rate) {

        this.asker_name = asker_name;
        this.asker_reputation = asker_reputation;
        this.asker_answerCount = asker_answerCount;
        this.asker_questionCount = asker_questionCount;
        this.asker_viewCount = asker_viewCount;
        this.asker_best_answer_rate = asker_best_answer_rate;
    }
}
