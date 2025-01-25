package com.example.belajar_android_sturio;

public class Buku {
    private long id;
    private String nama;
    private int stok;

    public Buku(long id, String nama, int stok) {
        this.id = id;
        this.nama = nama;
        this.stok = stok;
    }

    public long getId() {
        return id;
    }

    public String getNama() {
        return nama;
    }

    public void setStok(int stok){
        this.stok = stok;
    }
    public int getStok() {return stok;}
}
