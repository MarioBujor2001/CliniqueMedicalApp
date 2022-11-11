package com.example.licenta.Models;

import java.util.ArrayList;
import java.util.List;

public class Medic extends Persoana{

    private List<Specialitate> specialitati = new ArrayList<>();

    private Integer vechime;

    private Float rating;

    public Medic() {
    }

    public Medic(String id, String firstName, String lastName, String email, String CNP, Integer varsta, String adresa, List<Specialitate> specialitati, Integer vechime, Float rating) {
        super(id, firstName, lastName, email, CNP, varsta, adresa);
        this.specialitati = specialitati;
        this.vechime = vechime;
        this.rating = rating;
    }

    public Medic(String id, String firstName, String lastName, String email, String CNP, Integer varsta, String adresa, Integer vechime, Float rating) {
        super(id, firstName, lastName, email, CNP, varsta, adresa);
        this.vechime = vechime;
        this.rating = rating;
    }

    public List<Specialitate> getSpecialitati() {
        return specialitati;
    }

    public void setSpecialitati(List<Specialitate> specialitati) {
        this.specialitati = specialitati;
    }

    public Integer getVechime() {
        return vechime;
    }

    public void setVechime(Integer vechime) {
        this.vechime = vechime;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        return "Medic{" +
                "specialitati=" + specialitati +
                ", vechime=" + vechime +
                ", rating=" + rating +
                '}';
    }
}
