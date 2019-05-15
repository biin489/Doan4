package com.example.biin.doan4.model;

public class Inbox {
    private String url;
    private String userid;
    private String username;

    public Inbox() {
    }

    public Inbox(String url, String userid, String username) {
        this.url = url;
        this.userid = userid;
        this.username = username;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
