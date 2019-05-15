package com.example.biin.doan4.model;

import android.net.Uri;

import java.io.Serializable;

public class User implements Serializable {

    private String user_id;
    private String user_name;
    private String user_fullname;
    private String user_email;
    private String user_address;
    private String user_phone;
    private int user_age;
    private String user_gender;
    private String user_avatar;
    private int user_isLinked; // 0 chi dang nhap email, 1 da lien ket , 2 chi dang nhap fb
    private boolean user_isVerify;

    public User() {
        this.user_id = "";
        this.user_name = "";
        this.user_fullname = "";
        this.user_email = "";
        this.user_address = "";
        this.user_phone = "";
        this.user_age = 0;
        this.user_gender = "";
        this.user_avatar = "";
        this.user_isLinked = 0;
        this.user_isVerify = false;
    }

    public User(String user_id, String user_name, String user_fullname, String user_email, String user_address, String user_phone, int user_age, String user_gender, String user_avatar, int user_isLinked, boolean user_isVerify) {
        this.user_id = user_id;
        this.user_name = user_name;
        this.user_fullname = user_fullname;
        this.user_email = user_email;
        this.user_address = user_address;
        this.user_phone = user_phone;
        this.user_age = user_age;
        this.user_gender = user_gender;
        this.user_avatar = user_avatar;
        this.user_isLinked = user_isLinked;
        this.user_isVerify = user_isVerify;
    }

    public boolean isUser_isVerify() {
        return user_isVerify;
    }

    public void setUser_isVerify(boolean user_isVerify) {
        this.user_isVerify = user_isVerify;
    }

    public int isUser_isLinked() {
        return user_isLinked;
    }

    public void setUser_isLinked(int user_isLinked) {
        this.user_isLinked = user_isLinked;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getUser_phone() {
        return user_phone;
    }

    public void setUser_phone(String user_phone) {
        this.user_phone = user_phone;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_fullname() {
        return user_fullname;
    }

    public void setUser_fullname(String user_fullname) {
        this.user_fullname = user_fullname;
    }

    public String getUser_address() {
        return user_address;
    }

    public void setUser_address(String user_address) {
        this.user_address = user_address;
    }

    public int getUser_age() {
        return user_age;
    }

    public void setUser_age(int user_age) {
        this.user_age = user_age;
    }

    public String getUser_gender() {
        return user_gender;
    }

    public void setUser_gender(String user_gender) {
        this.user_gender = user_gender;
    }

    public String getUser_avatar() {
        return user_avatar;
    }

    public void setUser_avatar(String user_avatar) {
        this.user_avatar = user_avatar;
    }
}
