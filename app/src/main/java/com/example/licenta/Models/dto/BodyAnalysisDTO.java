package com.example.licenta.Models.dto;

import com.example.licenta.Models.enums.ActivityLevels;
import com.example.licenta.Models.enums.Genders;

public class BodyAnalysisDTO {
    private float weight;
    private float height;
    private Genders gender;
    private ActivityLevels activityLevel;

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public Genders getGender() {
        return gender;
    }

    public void setGender(Genders gender) {
        this.gender = gender;
    }

    public ActivityLevels getActivityLevel() {
        return activityLevel;
    }

    public void setActivityLevel(ActivityLevels activityLevel) {
        this.activityLevel = activityLevel;
    }
}
