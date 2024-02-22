package com.example.semestralka.model;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Event extends AbstractEntity{

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime eventDate;

    @Column(nullable = false)
    private boolean accepted = false;

    @ManyToMany
    @OrderBy("name")
    @JoinTable(name = "event_genre")
    private List<Genre> genres;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    private List<Comment> comments;

    @ManyToOne
    @JoinColumn(name = "club_id")
    public Club club;

    public void addGenre(Genre genre) {
        if (genres == null) genres = new ArrayList<>();
        genres.add(genre);
    }

    public void addComment(Comment comment) {
        if (comments == null) comments = new ArrayList<>();
        comments.add(comment);
    }

    public void removeComment(Comment comment){
        if (comments != null) comments.remove(comment);
    }

    public void removeGenre(Genre genre){
        if (genres != null) genres.remove(genre);
    }
 }
