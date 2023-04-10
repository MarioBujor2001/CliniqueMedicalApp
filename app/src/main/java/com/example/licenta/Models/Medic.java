package com.example.licenta.Models;

public class Medic extends Person {

    private Specialty specialty;

    private Integer seniority;

    private Float rating;

    public Medic() {
    }

    public Medic(String id, String firstName, String lastName, String email, String CNP, Integer varsta, String adresa, Specialty specialitate, Integer vechime, Float rating) {
        super(id, firstName, lastName, email, CNP, varsta, adresa);
        this.specialty = specialitate;
        this.seniority = vechime;
        this.rating = rating;
    }

    public Medic(String id, String firstName, String lastName, String email, String CNP, Integer varsta, String adresa, Integer vechime, Float rating) {
        super(id, firstName, lastName, email, CNP, varsta, adresa);
        this.seniority = vechime;
        this.rating = rating;
    }

    public Specialty getSpecialty() {
        return specialty;
    }

    public void setSpecialty(Specialty specialty) {
        this.specialty = specialty;
    }

    public Integer getSeniority() {
        return seniority;
    }

    public void setSeniority(Integer seniority) {
        this.seniority = seniority;
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
                "specialitate=" + specialty +
                ", vechime=" + seniority +
                ", rating=" + rating +
                '}';
    }
}
