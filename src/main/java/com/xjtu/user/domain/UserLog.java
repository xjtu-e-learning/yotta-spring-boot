package com.xjtu.user.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 用户登录的记录类
 *
 * @author yangkuan
 * @date 2018/03/14 11:17
 */
@Entity
@Table(name = "user_log")
public class UserLog {

    @GeneratedValue
    @Id
    private Long id;

    /**
     * userName 用户名
     */
    private String userName;

    /**
     * password 密码
     */
    private String password;

    /**
     * ip 登录ip
     */
    private String ip;

    /**
     * place 登录地点
     */
    private String place;

    /**
     * date 登录时间
     */
    private String date;

    public UserLog() {
    }

    public UserLog(String userName, String password, String ip, String place, String date) {
        this.userName = userName;
        this.password = password;
        this.ip = ip;
        this.place = place;
        this.date = date;
    }

    @Override
    public String toString() {
        return "UserLog{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", ip='" + ip + '\'' +
                ", place='" + place + '\'' +
                ", date='" + date + '\'' +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
