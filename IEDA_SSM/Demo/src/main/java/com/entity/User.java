package com.entity;

import java.util.Date;

/**
 * 实体类
 * auto lingtong 2018-09-11
 */
public class User {
    /**
     * 用户ID
     */
    private int id;
    /**
     * 用户邮箱
     */
    private String email;
    /**
     * 用户密码
     */
    private String password;
    /**
     * 用户昵称
     */
    private String username;
    /**
     * 用户身份
     */
    private String role;
    /**
     * 用户状态
     */
    private int status;
    /**
     * 注册时间
     */
    private Date regtime;
    /**
     * 注册IP
     */
    private String regip;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }
    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }
    public Date getRegtime() {
        return regtime;
    }
    public void setRegtime(Date regtime) {
        this.regtime = regtime;
    }
    public String getRegip() {
        return regip;
    }
    public void setRegip(String regip) {
        this.regip = regip;
    }

}