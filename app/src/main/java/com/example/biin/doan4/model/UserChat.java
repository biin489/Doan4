package com.example.biin.doan4.model;

public class UserChat {
    String chatContent;
    String chatImgurl;
    String idSender;
    String idReceive;
    String nameSender;

    public UserChat() {
    }

    public UserChat(String chatContent, String chatImgurl, String idSender, String idReceive, String nameSender) {
        this.chatContent = chatContent;
        this.chatImgurl = chatImgurl;
        this.idSender = idSender;
        this.idReceive = idReceive;
        this.nameSender = nameSender;
    }

    public String getIdReceive() {
        return idReceive;
    }

    public void setIdReceive(String idReceive) {
        this.idReceive = idReceive;
    }

    public String getNameSender() {
        return nameSender;
    }

    public void setNameSender(String nameSender) {
        this.nameSender = nameSender;
    }

    public String getIdSender() {
        return idSender;
    }

    public void setIdSender(String idSender) {
        this.idSender = idSender;
    }

    public String getChatContent() {
        return chatContent;
    }

    public void setChatContent(String chatContent) {
        this.chatContent = chatContent;
    }

    public String getChatImgurl() {
        return chatImgurl;
    }

    public void setChatImgurl(String chatImgurl) {
        this.chatImgurl = chatImgurl;
    }
}
