package com.example.licenta.Models;

public class Investigation {
    private String name;
    private float price;
    private Specialitate specialty;

    public Investigation(String name, float price, Specialitate specialty) {
        this.name = name;
        this.price = price;
        this.specialty = specialty;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public Specialitate getSpecialty() {
        return specialty;
    }

    public void setSpecialty(Specialitate specialty) {
        this.specialty = specialty;
    }

    @Override
    public String toString() {
        return name + " " + price;
    }
}
