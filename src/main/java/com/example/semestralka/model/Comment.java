package com.example.semestralka.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Comment extends AbstractEntity{

    @Column(nullable = false)
    private String text;

    @Column
    private LocalDateTime created;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    public void removeFromUser(){
        user.removeComment(this);
        user=null;
    }

    public void removeFromEvent(){
        event.removeComment(this);
        event=null;
    }
}
