package com.example.licenta.Models;

import java.io.Serializable;

public class Investigation implements Serializable {
    private Integer id;
    private String name;
    private float price;
    private Specialitate specialty;
    private String description;

    public Investigation(Integer id, String name, float price, Specialitate specialty, String description) {
        this.name = name;
        this.price = price;
        this.specialty = specialty;
        this.description = description;
    }

    public Investigation() {
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return name + "........................" + price;
    }
}
