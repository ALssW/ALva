package com.alsw.entity;

public class User {
    private String userName;
    private String userAct;
    private String userPswd;
    private String userBirth;
    private String userRegTime;
    private String a;
    private String[] array;

    public String[] getArray() {
        return array;
    }

    public void setArray(String[] array) {
        this.array = array;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserAct() {
        return userAct;
    }

    public void setUserAct(String userAct) {
        this.userAct = userAct;
    }

    public String getUserPswd() {
        return userPswd;
    }

    public void setUserPswd(String userPswd) {
        this.userPswd = userPswd;
    }

    public String getUserBirth() {
        return userBirth;
    }

    public void setUserBirth(String userBirth) {
        this.userBirth = userBirth;
    }

    public String getUserRegTime() {
        return userRegTime;
    }

    public void setUserRegTime(String userRegTime) {
        this.userRegTime = userRegTime;
    }
}
