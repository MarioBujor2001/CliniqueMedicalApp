package com.example.licenta.Models;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Appointment implements Serializable {
    private Integer id;
    private LocalDateTime date;
    private String comments;
    private Medic medic;
    private Patient patient;

    public Appointment(Integer id, LocalDateTime date, String comments, Medic medic, Patient patient) {
        this.id = id;
        this.date = date;
        this.comments = comments;
        this.medic = medic;
        this.patient = patient;
    }

    public Appointment() {
        this.comments = "n/a";
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Medic getMedic() {
        return medic;
    }

    public void setMedic(Medic medic) {
        this.medic = medic;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    @Override
    public String toString() {
        return "Programare{" +
                "id=" + id +
                ", data=" + date +
                ", observatii='" + comments + '\'' +
                ", medic=" + medic +
                ", pacient=" + patient +
                '}';
    }
}
