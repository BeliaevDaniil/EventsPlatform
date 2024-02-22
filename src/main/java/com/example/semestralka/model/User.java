package com.example.semestralka.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Data
public class User extends AbstractEntity {

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Favorite> favorites;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonIgnore
    private List<Comment> comments;

    public User() {
        this.role = Role.GUEST;
    }

    public void addComment(Comment comment){
        if (comments==null) comments = new ArrayList<>();
        comments.add(comment);
    }

    public void removeComment(Comment comment){
        comments.remove(comment);
    }

    public void addFavorite(Favorite favorite){
        if (favorites==null) favorites = new ArrayList<>();
        favorites.add(favorite);
    }

    public void removeFavorite(Favorite favorite){
        if (favorites!=null) favorites.remove(favorite);
    }

    @JsonIgnore
    public boolean isAdmin() {
        return role == Role.ADMIN;
    }

    public void encodePassword(PasswordEncoder encoder) {
        this.password = encoder.encode(password);
    }
}
