package com.example.licenta.Models;

import java.io.Serializable;

public abstract class Person implements Serializable {

    private String id;

    private String firstName;

    private String lastName;

    private String email;

    private String CNP;

    private Integer age;

    private String address;

    private String photoUrl;

    public Person() {
    }

    public Person(String id, String firstName, String lastName, String email, String CNP, Integer varsta, String adresa, String photoUrl) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.CNP = CNP;
        this.age = varsta;
        this.address = adresa;
        this.photoUrl = photoUrl;
    }

    public Person(String id, String firstName, String lastName, String email, String CNP, Integer varsta, String adresa) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.CNP = CNP;
        this.age = varsta;
        this.address = adresa;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
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

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "Persoana{" +
                "id='" + id + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", CNP='" + CNP + '\'' +
                ", varsta=" + age +
                ", adresa='" + address + '\'' +
                '}';
    }
}
