package com.example.licenta.Models;

import java.io.Serializable;

public abstract class Persoana implements Serializable {

    private String id;

    private String firstName;

    private String lastName;

    private String email;

    private String CNP;

    private Integer varsta;

    private String adresa;

    private String photo;

    public Persoana() {
    }

    public Persoana(String id, String firstName, String lastName, String email, String CNP, Integer varsta, String adresa, String photo) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.CNP = CNP;
        this.varsta = varsta;
        this.adresa = adresa;
        this.photo = photo;
    }

    public Persoana(String id, String firstName, String lastName, String email, String CNP, Integer varsta, String adresa) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.CNP = CNP;
        this.varsta = varsta;
        this.adresa = adresa;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCNP() {
        return CNP;
    }

    public void setCNP(String CNP) {
        this.CNP = CNP;
    }

    public Integer getVarsta() {
        return varsta;
    }

    public void setVarsta(Integer varsta) {
        this.varsta = varsta;
    }

    public String getAdresa() {
        return adresa;
    }

    public void setAdresa(String adresa) {
        this.adresa = adresa;
    }

    @Override
    public String toString() {
        return "Persoana{" +
                "id='" + id + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", CNP='" + CNP + '\'' +
                ", varsta=" + varsta +
                ", adresa='" + adresa + '\'' +
                '}';
    }
}
