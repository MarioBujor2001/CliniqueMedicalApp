package com.example.licenta.Models;

import java.util.ArrayList;
import java.util.List;

public class Medic extends Persoana{

    private Specialitate specialitate;

    private Integer vechime;

    private Float rating;

    public Medic() {
    }

    public Medic(String id, String firstName, String lastName, String email, String CNP, Integer varsta, String adresa, Specialitate specialitate, Integer vechime, Float rating) {
        super(id, firstName, lastName, email, CNP, varsta, adresa);
        this.specialitate = specialitate;
        this.vechime = vechime;
        this.rating = rating;
    }

    public Medic(String id, String firstName, String lastName, String email, String CNP, Integer varsta, String adresa, Integer vechime, Float rating) {
        super(id, firstName, lastName, email, CNP, varsta, adresa);
        this.vechime = vechime;
        this.rating = rating;
    }

    public Specialitate getSpecialitate() {
        return specialitate;
    }

    public void setSpecialitate(Specialitate specialitate) {
        this.specialitate = specialitate;
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
        return super.toString()+"Medic{" +
                "specialitate=" + specialitate +
                ", vechime=" + vechime +
                ", rating=" + rating +
                '}';
    }
}
