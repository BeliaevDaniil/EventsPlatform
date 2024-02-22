package com.example.semestralka.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Club extends AbstractEntity{

    @Column(nullable = false, unique = true)
    public String name;

    @OneToMany(mappedBy = "club")
    @JsonIgnore
    public List<Event> events;

    public void addEvent(Event event){
        if (events == null) events = new ArrayList<>();
        events.add(event);
    }

    public void removeEvent(Event event){
        if (event!=null){
            events.remove(event);
        }
    }
}
