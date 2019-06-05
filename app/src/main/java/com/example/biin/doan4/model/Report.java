package com.example.biin.doan4.model;

public class Report {
    private String rp_id;
    private String rp_content;
    private String rp_user_id;
    private String rp_user_report_id;
    private String rp_image;
    private int rp_status;

    public Report() {
    }

    public Report(String rp_id, String rp_content, String rp_user_id, String rp_user_report_id, String rp_image, int rp_status) {
        this.rp_id = rp_id;
        this.rp_content = rp_content;
        this.rp_user_id = rp_user_id;
        this.rp_user_report_id = rp_user_report_id;
        this.rp_image = rp_image;
        this.rp_status = rp_status;
    }

    public int getRp_status() {
        return rp_status;
    }

    public void setRp_status(int rp_status) {
        this.rp_status = rp_status;
    }

    public String getRp_id() {
        return rp_id;
    }

    public void setRp_id(String rp_id) {
        this.rp_id = rp_id;
    }

    public String getRp_content() {
        return rp_content;
    }

    public void setRp_content(String rp_content) {
        this.rp_content = rp_content;
    }

    public String getRp_user_id() {
        return rp_user_id;
    }

    public void setRp_user_id(String rp_user_id) {
        this.rp_user_id = rp_user_id;
    }

    public String getRp_user_report_id() {
        return rp_user_report_id;
    }

    public void setRp_user_report_id(String rp_user_report_id) {
        this.rp_user_report_id = rp_user_report_id;
    }

    public String getRp_image() {
        return rp_image;
    }

    public void setRp_image(String rp_image) {
        this.rp_image = rp_image;
    }
}
