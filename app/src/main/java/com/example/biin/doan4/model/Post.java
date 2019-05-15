package com.example.biin.doan4.model;

import java.io.Serializable;

public class Post implements Serializable {
    private String post_id;
    private String post_user_id;
    private String post_title;
    private String post_detail;
    private Long post_price;
    private String post_image;
    private String post_type;
    private int post_status;
    private String post_maker;
    private Boolean post_is_guarantee;
    private int post_trangthai; // 0 dang ban, 1//da ban
    private int post_score; // 0 - 5 diem

    public Post() {
    }

    public Post(String post_id, String post_user_id, String post_title, String post_detail, long post_price, String post_image, String post_type, int post_status, String post_maker, Boolean post_is_guarantee, int post_trangthai, int post_score) {
        this.post_id = post_id;
        this.post_user_id = post_user_id;
        this.post_title = post_title;
        this.post_detail = post_detail;
        this.post_price = post_price;
        this.post_image = post_image;
        this.post_type = post_type;
        this.post_status = post_status;
        this.post_maker = post_maker;
        this.post_is_guarantee = post_is_guarantee;
        this.post_trangthai = post_trangthai;
        this.post_score = post_score;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public String getPost_user_id() {
        return post_user_id;
    }

    public void setPost_user_id(String post_user_id) {
        this.post_user_id = post_user_id;
    }

    public String getPost_title() {
        return post_title;
    }

    public void setPost_title(String post_title) {
        this.post_title = post_title;
    }

    public String getPost_detail() {
        return post_detail;
    }

    public void setPost_detail(String post_detail) {
        this.post_detail = post_detail;
    }

    public long getPost_price() {
        return post_price;
    }

    public void setPost_price(long post_price) {
        this.post_price = post_price;
    }

    public String getPost_image() {
        return post_image;
    }

    public void setPost_image(String post_image) {
        this.post_image = post_image;
    }

    public String getPost_type() {
        return post_type;
    }

    public void setPost_type(String post_type) {
        this.post_type = post_type;
    }

    public int getPost_status() {
        return post_status;
    }

    public void setPost_status(int post_status) {
        this.post_status = post_status;
    }

    public String getPost_maker() {
        return post_maker;
    }

    public void setPost_maker(String post_maker) {
        this.post_maker = post_maker;
    }

    public Boolean getPost_is_guarantee() {
        return post_is_guarantee;
    }

    public void setPost_is_guarantee(Boolean post_is_guarantee) {
        this.post_is_guarantee = post_is_guarantee;
    }

    public int getPost_trangthai() {
        return post_trangthai;
    }

    public void setPost_trangthai(int post_trangthai) {
        this.post_trangthai = post_trangthai;
    }

    public int getPost_score() {
        return post_score;
    }

    public void setPost_score(int post_score) {
        this.post_score = post_score;
    }
}
