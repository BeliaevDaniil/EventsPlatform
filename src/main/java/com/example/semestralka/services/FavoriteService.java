package com.example.semestralka.services;

import com.example.semestralka.data.FavoriteRepository;
import com.example.semestralka.model.Event;
import com.example.semestralka.model.Favorite;
import com.example.semestralka.model.FavoriteId;
import com.example.semestralka.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class FavoriteService {

    private final FavoriteRepository favoriteRepo;

    @Autowired
    public FavoriteService(FavoriteRepository favoriteRepo) {
        this.favoriteRepo = favoriteRepo;
    }

    public Favorite find(FavoriteId id){
        Objects.requireNonNull(id);
        return favoriteRepo.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public Iterable<Favorite> findAll(){
        try {
            return favoriteRepo.findAll();
        } catch (RuntimeException e) {
            throw new RuntimeException("There are no favorite events");
        }
    }
    @Transactional
    public void save(Event event, User user){
        Objects.requireNonNull(event);
        Objects.requireNonNull(user);
        final Favorite f = new Favorite();
        final FavoriteId favoriteId = new FavoriteId();
        favoriteId.setUserId(user.getId());
        favoriteId.setEventId(event.getId());
        f.setId(favoriteId);
        f.setUser(user);
        f.setEvent(event);
        favoriteRepo.save(f);
    }

    @Transactional
    public void update(Favorite favorite){
        Objects.requireNonNull(favorite);
        if (exists(favorite.getId())) {
            favoriteRepo.save(favorite);
        }
    }

    @Transactional
    public void delete(Favorite favorite){
        Objects.requireNonNull(favorite);
        if (exists(favorite.getId())) {
            favoriteRepo.delete(favorite);
        }
    }

    public boolean exists(FavoriteId id){
        Objects.requireNonNull(id);
        return  favoriteRepo.existsById(id);
    }
}
