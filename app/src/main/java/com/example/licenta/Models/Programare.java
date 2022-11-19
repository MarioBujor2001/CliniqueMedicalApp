package com.example.licenta.Models;

import java.time.LocalDateTime;

public class Programare {
    private Integer id;
    private LocalDateTime data;
    private String observatii;
    private Medic medic;
    private Pacient pacient;

    public Programare(Integer id, LocalDateTime data, String observatii, Medic medic, Pacient pacient) {
        this.id = id;
        this.data = data;
        this.observatii = observatii;
        this.medic = medic;
        this.pacient = pacient;
    }

    public Programare() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    public String getObservatii() {
        return observatii;
    }

    public void setObservatii(String observatii) {
        this.observatii = observatii;
    }

    public Medic getMedic() {
        return medic;
    }

    public void setMedic(Medic medic) {
        this.medic = medic;
    }

    public Pacient getPacient() {
        return pacient;
    }

    public void setPacient(Pacient pacient) {
        this.pacient = pacient;
    }

    @Override
    public String toString() {
        return "Programare{" +
                "id=" + id +
                ", data=" + data +
                ", observatii='" + observatii + '\'' +
                ", medic=" + medic +
                ", pacient=" + pacient +
                '}';
    }
}
