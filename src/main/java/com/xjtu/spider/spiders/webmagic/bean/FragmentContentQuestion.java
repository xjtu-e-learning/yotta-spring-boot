package com.xjtu.spider.spiders.webmagic.bean;

import java.util.List;

/**
 * 问题碎片、包含问题标题、问题正文、问题最佳回答、问题社区特征、提问者特征、问题质量标签
 *
 * @author yuanhao
 * @date 2018/4/10 15:30
 */
public class FragmentContentQuestion extends Assembles {

    private String page_website_logo;
    private String page_search_url;
    private String page_column_color;
    private String question_url;
    private String question_title;
    private String question_title_pure;
    private String question_body;
    private String question_body_pure;
    private String question_best_answer;
    private String question_best_answer_pure;
    private String question_score;
    private String question_answerCount;
    private String question_viewCount;
    private String asker_url;

    @Override
    public String toString() {
        return "FragmentContentQuestion{" +
                "page_website_logo='" + page_website_logo + '\'' +
                ", page_search_url='" + page_search_url + '\'' +
                ", page_column_color='" + page_column_color + '\'' +
                ", question_url='" + question_url + '\'' +
                ", question_title='" + question_title + '\'' +
                ", question_title_pure='" + question_title_pure + '\'' +
                ", question_body='" + question_body + '\'' +
                ", question_body_pure='" + question_body_pure + '\'' +
                ", question_best_answer='" + question_best_answer + '\'' +
                ", question_best_answer_pure='" + question_best_answer_pure + '\'' +
                ", question_score='" + question_score + '\'' +
                ", question_answerCount='" + question_answerCount + '\'' +
                ", question_viewCount='" + question_viewCount + '\'' +
                ", asker_url='" + asker_url + '\'' +
                '}';
    }

    public String getPage_website_logo() {
        return page_website_logo;
    }

    public void setPage_website_logo(String page_website_logo) {
        this.page_website_logo = page_website_logo;
    }

    public String getPage_search_url() {
        return page_search_url;
    }

    public void setPage_search_url(String page_search_url) {
        this.page_search_url = page_search_url;
    }

    public String getPage_column_color() {
        return page_column_color;
    }

    public void setPage_column_color(String page_column_color) {
        this.page_column_color = page_column_color;
    }

    public String getQuestion_url() {
        return question_url;
    }

    public void setQuestion_url(String question_url) {
        this.question_url = question_url;
    }

    public String getQuestion_title() {
        return question_title;
    }

    public void setQuestion_title(String question_title) {
        this.question_title = question_title;
    }

    public String getQuestion_title_pure() {
        return question_title_pure;
    }

    public void setQuestion_title_pure(String question_title_pure) {
        this.question_title_pure = question_title_pure;
    }

    public String getQuestion_body() {
        return question_body;
    }

    public void setQuestion_body(String question_body) {
        this.question_body = question_body;
    }

    public String getQuestion_body_pure() {
        return question_body_pure;
    }

    public void setQuestion_body_pure(String question_body_pure) {
        this.question_body_pure = question_body_pure;
    }

    public String getQuestion_best_answer() {
        return question_best_answer;
    }

    public void setQuestion_best_answer(String question_best_answer) {
        this.question_best_answer = question_best_answer;
    }

    public String getQuestion_best_answer_pure() {
        return question_best_answer_pure;
    }

    public void setQuestion_best_answer_pure(String question_best_answer_pure) {
        this.question_best_answer_pure = question_best_answer_pure;
    }

    public String getQuestion_score() {
        return question_score;
    }

    public void setQuestion_score(String question_score) {
        this.question_score = question_score;
    }

    public String getQuestion_answerCount() {
        return question_answerCount;
    }

    public void setQuestion_answerCount(String question_answerCount) {
        this.question_answerCount = question_answerCount;
    }

    public String getQuestion_viewCount() {
        return question_viewCount;
    }

    public void setQuestion_viewCount(String question_viewCount) {
        this.question_viewCount = question_viewCount;
    }

    public String getAsker_url() {
        return asker_url;
    }

    public void setAsker_url(String asker_url) {
        this.asker_url = asker_url;
    }

    public FragmentContentQuestion() {
    }

    public FragmentContentQuestion(List<String> fragments, List<String> fragmentsPureText, String page_website_logo, String page_search_url, String page_column_color, String question_url, String question_title, String question_title_pure, String question_body, String question_body_pure, String question_best_answer, String question_best_answer_pure, String question_score, String question_answerCount, String question_viewCount, String asker_url) {
        super(fragments, fragmentsPureText);
        this.page_website_logo = page_website_logo;
        this.page_search_url = page_search_url;
        this.page_column_color = page_column_color;
        this.question_url = question_url;
        this.question_title = question_title;
        this.question_title_pure = question_title_pure;
        this.question_body = question_body;
        this.question_body_pure = question_body_pure;
        this.question_best_answer = question_best_answer;
        this.question_best_answer_pure = question_best_answer_pure;
        this.question_score = question_score;
        this.question_answerCount = question_answerCount;
        this.question_viewCount = question_viewCount;
        this.asker_url = asker_url;
    }
}
