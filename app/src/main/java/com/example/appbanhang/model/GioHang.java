package com.example.appbanhang.model;

public class GioHang {
    private int idsp;
    private String tensp;
    private long giasp;
    private String hinhanh;
    private int soluong;
     boolean ischecked;
     int soluongtonkho;

    public int getSoluongtonkho() {
        return soluongtonkho;
    }

    public void setSoluongtonkho(int soluongtonkho) {
        this.soluongtonkho = soluongtonkho;
    }

    public boolean isChecked() {
        return ischecked;
    }

    public void setChecked(boolean checked) {
        ischecked = checked;
    }

    public GioHang() {
    }

    public GioHang(int idsp, String tensp, long giasp, String hinhanh, int soluong) {
        this.idsp = idsp;
        this.tensp = tensp;
        this.giasp = giasp;
        this.hinhanh = hinhanh;
        this.soluong = soluong;
    }

    public int getIdsp() {
        return idsp;
    }

    public void setIdsp(int idsp) {
        this.idsp = idsp;
    }

    public String getTensp() {
        return tensp;
    }

    public void setTensp(String tensp) {
        this.tensp = tensp;
    }

    public long getGiasp() {
        return giasp;
    }

    public void setGiasp(long giasp) {
        this.giasp = giasp;
    }

    public String getHinhanh() {
        return hinhanh;
    }

    public void setHinhanh(String hinhanh) {
        this.hinhanh = hinhanh;
    }

    public int getSoluong() {
        return soluong;
    }

    public void setSoluong(int soluong) {
        this.soluong = soluong;
    }
}
