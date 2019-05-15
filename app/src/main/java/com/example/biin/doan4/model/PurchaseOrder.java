package com.example.biin.doan4.model;

public class PurchaseOrder {
    private String po_id;
    private String po_productid;
    private String po_img;
    private String po_title;
    private String po_sellerid;
    private String po_buyerid;
    private String po_address;
    private String po_note;
    private int po_status; // 0 - dang cho don, 1: dang giao hang, 2: da nhan hang

    public PurchaseOrder() {
    }

    public PurchaseOrder(String po_id, String po_productid, String po_img, String po_title, String po_sellerid, String po_buyerid, String po_address, String po_note, int po_status) {
        this.po_id = po_id;
        this.po_productid = po_productid;
        this.po_img = po_img;
        this.po_title = po_title;
        this.po_sellerid = po_sellerid;
        this.po_buyerid = po_buyerid;
        this.po_address = po_address;
        this.po_note = po_note;
        this.po_status = po_status;
    }

    public String getPo_id() {
        return po_id;
    }

    public void setPo_id(String po_id) {
        this.po_id = po_id;
    }

    public String getPo_note() {
        return po_note;
    }

    public void setPo_note(String po_note) {
        this.po_note = po_note;
    }

    public String getPo_productid() {
        return po_productid;
    }

    public void setPo_productid(String po_productid) {
        this.po_productid = po_productid;
    }

    public String getPo_img() {
        return po_img;
    }

    public void setPo_img(String po_img) {
        this.po_img = po_img;
    }

    public String getPo_title() {
        return po_title;
    }

    public void setPo_title(String po_title) {
        this.po_title = po_title;
    }

    public String getPo_sellerid() {
        return po_sellerid;
    }

    public void setPo_sellerid(String po_sellerid) {
        this.po_sellerid = po_sellerid;
    }

    public String getPo_buyerid() {
        return po_buyerid;
    }

    public void setPo_buyerid(String po_buyerid) {
        this.po_buyerid = po_buyerid;
    }

    public String getPo_address() {
        return po_address;
    }

    public void setPo_address(String po_address) {
        this.po_address = po_address;
    }

    public int getPo_status() {
        return po_status;
    }

    public void setPo_status(int po_status) {
        this.po_status = po_status;
    }
}
