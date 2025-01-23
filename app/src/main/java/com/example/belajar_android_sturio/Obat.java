package com.example.belajar_android_sturio;

public class Obat {
    private long id;
    private String nama;
    private double harga;
    private int stok;

    public Obat(long id, String nama, double harga, int stok) {
        this.id = id;
        this.nama = nama;
        this.harga = harga;
        this.stok = stok;
    }

    public long getId() {
        return id;
    }

    public String getNama() {
        return nama;
    }

    public double getHarga() {
        return harga;
    }

    public void setStok(int stok){
        this.stok = stok;
    }
    public int getStok() {return stok;}
}
