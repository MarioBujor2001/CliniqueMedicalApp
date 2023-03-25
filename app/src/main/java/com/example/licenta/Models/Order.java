package com.example.licenta.Models;

import java.time.LocalDateTime;
import java.util.List;

public class Order {
    private Integer id;
    private LocalDateTime data;

    List<Investigation> investigations;

    public Order() {
    }

    public Integer getId() {
        return id;
    }

    public float getTotal(){
        float total = 0;
        for(Investigation i:investigations){
            total+=i.getPrice();
        }
        return total;
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

    public List<Investigation> getInvestigations() {
        return investigations;
    }

    public void setInvestigations(List<Investigation> investigations) {
        this.investigations = investigations;
    }
}
