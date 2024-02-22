package com.example.semestralka.services;

import com.example.semestralka.data.ClubRepository;
import com.example.semestralka.data.FavoriteRepository;
import com.example.semestralka.exceptions.NotFoundException;
import com.example.semestralka.model.Club;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Objects;

@Service
public class ClubService {

    private final ClubRepository clubRepo;
    private final FavoriteRepository favoriteRepo;

    @Autowired
    public ClubService(ClubRepository clubRepo, FavoriteRepository favoriteRepo) {
        this.clubRepo = clubRepo;
        this.favoriteRepo = favoriteRepo;
    }

    @Transactional(readOnly = true)
    public Club find(Integer id){
        Objects.requireNonNull(id);
        return clubRepo.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public Club findByName(String name) {
        Objects.requireNonNull(name);
        return clubRepo.getByName(name);
    }

    @Transactional(readOnly = true)
    public Iterable<Club> findAll(){
        try {
            return clubRepo.findAll();
        } catch (DataAccessException e) {
            throw new NotFoundException("There are no clubs");
        }
    }

    @Transactional
    public void save(Club club){
        Objects.requireNonNull(club);
        if (!existsByName(club.getName())) {
            clubRepo.save(club);
        }
    }

    @Transactional
    public void update(Club club){
        Objects.requireNonNull(club);
        if (exists(club.getId())) {
            clubRepo.save(club);
        }
    }

    @Transactional
    public void delete(Club club){
        Objects.requireNonNull(club);
        if (exists(club.getId())) {
            club.getEvents().forEach(event -> event.setAccepted(false));
            club.getEvents().forEach(event -> event.setClub(null));
            club.getEvents().forEach(event -> favoriteRepo.deleteAllByEventId(event.getId()));
            clubRepo.delete(club);
        }
    }

    public boolean exists(Integer id){
        Objects.requireNonNull(id);
        return  clubRepo.existsById(id);
    }

    public boolean existsByName(String name){
        Objects.requireNonNull(name);
        return clubRepo.existsByName(name);
    }
}
