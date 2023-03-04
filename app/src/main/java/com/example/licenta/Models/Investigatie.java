package com.example.licenta.Models;

public class Investigatie {
    private String name;
    private float price;
    private Specialitate specialty;
    private String description;

    public Investigatie(String name, float price, Specialitate specialty, String description) {
        this.name = name;
        this.price = price;
        this.specialty = specialty;
        this.description = description;
    }

    public Investigatie() {
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
