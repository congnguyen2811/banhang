package com.example.appbanhang.model;

public class LoaiSP {
    private int id;
   private String tensanpham;
    private String hinhanh;

    public LoaiSP(String tensanpham, String hinhanh) {
        this.tensanpham = tensanpham;
        this.hinhanh = hinhanh;
    }

    public LoaiSP() {
    }

    public LoaiSP(int id, String tensanpham, String hinhanh) {
        this.id = id;
        this.tensanpham = tensanpham;
        this.hinhanh = hinhanh;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTensanpham() {
        return tensanpham;
    }

    public void setTensanpham(String tensanpham) {
        this.tensanpham = tensanpham;
    }

    public String getHinhanh() {
        return hinhanh;
    }

    public void setHinhanh(String hinhanh) {
        this.hinhanh = hinhanh;
    }
}
