package com.example.licenta.Models;

public class Specialitate {

    private Integer id;

    private Specialitati tip;

    private String descriere;

    public Specialitate() {
    }

    public Specialitate(Specialitati tip, String descriere) {
        this.tip = tip;
        this.descriere = descriere;
    }

    public Specialitati getTip() {
        return tip;
    }

    public void setTip(Specialitati tip) {
        this.tip = tip;
    }

    public String getDescriere() {
        return descriere;
    }

    public void setDescriere(String descriere) {
        this.descriere = descriere;
    }

    public Integer getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Specialitate{" +
                "id=" + id +
                ", tip=" + tip +
                ", descriere='" + descriere + '\'' +
                '}';
    }
}
