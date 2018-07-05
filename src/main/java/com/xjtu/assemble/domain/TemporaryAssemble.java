package com.xjtu.assemble.domain;


import javax.persistence.*;

/**
 * 碎片暂存类，对应数据库中的碎片暂存表temporary_assemble
 *
 * @author yangkuan
 * @date 2018/04/14 13:31
 */
@Entity
@Table(name = "temporary_assemble")
public class TemporaryAssemble {

    /**
     * 碎片id
     */
    @Id
    @GeneratedValue
    Long assembleId;

    /**
     * 碎片内容
     */
    @Column(columnDefinition = "longtext")
    String assembleContent;

    /**
     * 碎片添加时间
     */
    String assembleScratchTime;

    /**
     * 添加碎片的用户
     */
    String userName;

    public TemporaryAssemble() {
    }

    public TemporaryAssemble(String assembleContent, String assembleScratchTime, String userName) {
        this.assembleContent = assembleContent;
        this.assembleScratchTime = assembleScratchTime;
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "TemporaryAssemble{" +
                "assembleId=" + assembleId +
                ", assembleContent='" + assembleContent + '\'' +
                ", assembleScratchTime='" + assembleScratchTime + '\'' +
                ", userName='" + userName + '\'' +
                '}';
    }

    public Long getAssembleId() {
        return assembleId;
    }

    public void setAssembleId(Long assembleId) {
        this.assembleId = assembleId;
    }

    public String getAssembleContent() {
        return assembleContent;
    }

    public void setAssembleContent(String assembleContent) {
        this.assembleContent = assembleContent;
    }

    public String getAssembleScratchTime() {
        return assembleScratchTime;
    }

    public void setAssembleScratchTime(String assembleScratchTime) {
        this.assembleScratchTime = assembleScratchTime;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
