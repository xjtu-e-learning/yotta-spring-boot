package com.xjtu.questionquality.domain;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 问题碎片：包括问题和提问者信息
 * @author yangkuan
 * @date 2018/05/12
 */

@Entity
@Table(name = "question_assemble")
public class QuestionAssemble {

    @Id
    @GeneratedValue
    private int questionId;
    /**
     * 问题网站信息
     */
    /**
     * 问题网站logo。SO为 fa fa-stack-overflow, Yahoo为 fa fa-yahoo
     */
    private String pageWebsiteLogo;
    /**
     * 问题网站搜索链接.
     * SO为 https://stackoverflow.com/search?q=, Yahoo为 https://answers.search.yahoo.com/search?p=
     */
    private String pageSearchUrl;
    /**
     * 问题网站高质量显示颜色
     */
    private String pageColumnColor;

    /**
     * 问题信息
     */
    private String questionUrl;
    private String questionTitle;
    private String questionTitlePure;
    private String questionBody;
    private String questionBodyPure;
    private String questionBestAnswer;
    private String questionBestAnswerPure;
    private String questionScore;
    private String questionAnswerCount;
    private String questionViewCount;
    private String askerUrl;

    /**
     * 用户信息
     */
    private String askerName;
    private String askerReputation;
    private String askerAnswerCount;
    private String askerQuestionCount;
    private String askerViewCount;
    private String askerBestAnswerRate;

    /**
     * 标签信息
     */
    private String questionQualityLabel;

    /**
     * 问题对应在碎片表assemble的id，外键
     */
    private int AssembleId;

    @Override
    public String toString() {
        return "QuestionAssemble{" +
                "questionId=" + questionId +
                ", pageWebsiteLogo='" + pageWebsiteLogo + '\'' +
                ", pageSearchUrl='" + pageSearchUrl + '\'' +
                ", pageColumnColor='" + pageColumnColor + '\'' +
                ", questionUrl='" + questionUrl + '\'' +
                ", questionTitle='" + questionTitle + '\'' +
                ", questionTitlePure='" + questionTitlePure + '\'' +
                ", questionBody='" + questionBody + '\'' +
                ", questionBodyPure='" + questionBodyPure + '\'' +
                ", questionBestAnswer='" + questionBestAnswer + '\'' +
                ", questionBestAnswerPure='" + questionBestAnswerPure + '\'' +
                ", questionScore='" + questionScore + '\'' +
                ", questionAnswerCount='" + questionAnswerCount + '\'' +
                ", questionViewCount='" + questionViewCount + '\'' +
                ", askerUrl='" + askerUrl + '\'' +
                ", askerName='" + askerName + '\'' +
                ", askerReputation='" + askerReputation + '\'' +
                ", askerAnswerCount='" + askerAnswerCount + '\'' +
                ", askerQuestionCount='" + askerQuestionCount + '\'' +
                ", askerViewCount='" + askerViewCount + '\'' +
                ", askerBestAnswerRate='" + askerBestAnswerRate + '\'' +
                ", questionQualityLabel='" + questionQualityLabel + '\'' +
                ", AssembleId=" + AssembleId +
                '}';
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public String getPageWebsiteLogo() {
        return pageWebsiteLogo;
    }

    public void setPageWebsiteLogo(String pageWebsiteLogo) {
        this.pageWebsiteLogo = pageWebsiteLogo;
    }

    public String getPageSearchUrl() {
        return pageSearchUrl;
    }

    public void setPageSearchUrl(String pageSearchUrl) {
        this.pageSearchUrl = pageSearchUrl;
    }

    public String getPageColumnColor() {
        return pageColumnColor;
    }

    public void setPageColumnColor(String pageColumnColor) {
        this.pageColumnColor = pageColumnColor;
    }

    public String getQuestionUrl() {
        return questionUrl;
    }

    public void setQuestionUrl(String questionUrl) {
        this.questionUrl = questionUrl;
    }

    public String getQuestionTitle() {
        return questionTitle;
    }

    public void setQuestionTitle(String questionTitle) {
        this.questionTitle = questionTitle;
    }

    public String getQuestionTitlePure() {
        return questionTitlePure;
    }

    public void setQuestionTitlePure(String questionTitlePure) {
        this.questionTitlePure = questionTitlePure;
    }

    public String getQuestionBody() {
        return questionBody;
    }

    public void setQuestionBody(String questionBody) {
        this.questionBody = questionBody;
    }

    public String getQuestionBodyPure() {
        return questionBodyPure;
    }

    public void setQuestionBodyPure(String questionBodyPure) {
        this.questionBodyPure = questionBodyPure;
    }

    public String getQuestionBestAnswer() {
        return questionBestAnswer;
    }

    public void setQuestionBestAnswer(String questionBestAnswer) {
        this.questionBestAnswer = questionBestAnswer;
    }

    public String getQuestionBestAnswerPure() {
        return questionBestAnswerPure;
    }

    public void setQuestionBestAnswerPure(String questionBestAnswerPure) {
        this.questionBestAnswerPure = questionBestAnswerPure;
    }

    public String getQuestionScore() {
        return questionScore;
    }

    public void setQuestionScore(String questionScore) {
        this.questionScore = questionScore;
    }

    public String getQuestionAnswerCount() {
        return questionAnswerCount;
    }

    public void setQuestionAnswerCount(String questionAnswerCount) {
        this.questionAnswerCount = questionAnswerCount;
    }

    public String getQuestionViewCount() {
        return questionViewCount;
    }

    public void setQuestionViewCount(String questionViewCount) {
        this.questionViewCount = questionViewCount;
    }

    public String getAskerUrl() {
        return askerUrl;
    }

    public void setAskerUrl(String askerUrl) {
        this.askerUrl = askerUrl;
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

    public String getQuestionQualityLabel() {
        return questionQualityLabel;
    }

    public void setQuestionQualityLabel(String questionQualityLabel) {
        this.questionQualityLabel = questionQualityLabel;
    }

    public int getAssembleId() {
        return AssembleId;
    }

    public void setAssembleId(int assembleId) {
        AssembleId = assembleId;
    }
}
