package com.xjtu.user.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 用户信息类
 *
 * @author yangkuan
 * @date 2018/03/14 11:17
 */

@Entity
@Table(name = "user")
public class User {

    @GeneratedValue
    @Id
    private Long userId;
    /**
     * userName 用户名
     */
    private String userName;
    /**
     * password 密码
     */
    private String password;
    /**
     * note 一些额外信息（例如用户真实姓名）
     */
    private String note;

    public User() {
    }

    public User(String userName, String password, String note) {
        this.userName = userName;
        this.password = password;
        this.note = note;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", note='" + note + '\'' +
                '}';
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
