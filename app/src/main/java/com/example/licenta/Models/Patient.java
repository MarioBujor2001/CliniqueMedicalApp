package com.example.licenta.Models;

import java.util.ArrayList;
import java.util.List;

public class Patient extends Person {

    private Integer grad_urgenta;

    private List<Appointment> appointments = new ArrayList<>();

    public Patient() {
    }

    public Patient(String id, String firstName, String lastName, String email, String CNP, Integer varsta, String adresa, Integer grad_urgenta) {
        super(id, firstName, lastName, email, CNP, varsta, adresa);
        this.grad_urgenta = grad_urgenta;
    }

    public List<Appointment> getAppointments() {
        return appointments;
    }

    public void setAppointments(List<Appointment> appointments) {
        this.appointments = appointments;
    }

    public Integer getGrad_urgenta() {
        return grad_urgenta;
    }

    public void setGrad_urgenta(Integer grad_urgenta) {
        this.grad_urgenta = grad_urgenta;
    }

    @Override
    public String toString() {
        return super.toString()+"Pacient{" +
                "grad_urgenta=" + grad_urgenta +
                '}';
    }
}
