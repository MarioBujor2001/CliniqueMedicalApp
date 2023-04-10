package com.example.licenta.Models;

public class Specialty {

    private Integer id;

    private Specialties type;

    private String description;

    public Specialty() {
    }

    public Specialty(Specialties type, String description) {
        this.type = type;
        this.description = description;
    }

    public Specialties getType() {
        return type;
    }

    public void setType(Specialties type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Specialitate{" +
                "id=" + id +
                ", tip=" + type +
                ", descriere='" + description + '\'' +
                '}';
    }
}
