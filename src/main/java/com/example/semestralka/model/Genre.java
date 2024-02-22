package com.example.semestralka.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Genre extends AbstractEntity{

    @Column(nullable = false, unique = true)
    private String name;

    @ManyToMany(mappedBy = "genres")
    private List<Event> events;

    @Override
    public String toString() {
        return "Genre{" +
                "name='" + name + '\'' +
                "}";
    }

    public void addEvent(Event event){
        if (events == null) events = new ArrayList<>();
        events.add(event);
    }

    public void removeEvent(Event event){
        if (events != null) events.remove(event);
    }

}
