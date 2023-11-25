package com.example.semestralka.services;

import com.example.semestralka.data.EventRepository;
import com.example.semestralka.data.GenreRepository;
import com.example.semestralka.exceptions.NotFoundException;
import com.example.semestralka.model.Event;
import com.example.semestralka.model.Genre;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Objects;

@Service
public class GenreService {

    private final GenreRepository genreRepo;
    private final EventRepository eventRepo;

    @Autowired
    public GenreService(GenreRepository genreRepo, EventRepository eventRepo) {
        this.genreRepo = genreRepo;
        this.eventRepo = eventRepo;
    }

    @Transactional(readOnly = true)
    public Genre find(Integer id){
        Objects.requireNonNull(id);
        return genreRepo.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public Iterable<Genre> findAll(){
        try {
            return genreRepo.findAll();
        } catch (DataAccessException e) {
            throw new NotFoundException("There are no genres");
        }
    }
    @Transactional
    public void save(Genre genre){
        Objects.requireNonNull(genre);
        genreRepo.save(genre);
    }

    @Transactional
    public void update(Genre genre){
        Objects.requireNonNull(genre);
        if (exists(genre.getId())) {
            genreRepo.save(genre);
        }
    }

    @Transactional
    public void delete(Genre genre){
        Objects.requireNonNull(genre);
        if (exists(genre.getId())) {
            genreRepo.delete(genre);
        }
    }

    public boolean exists(Integer id){
        Objects.requireNonNull(id);
        return  genreRepo.existsById(id);
    }

    @Transactional
    public void addEvent(Genre genre, Event event){
        Objects.requireNonNull(genre);
        Objects.requireNonNull(event);
        event.addGenre(genre);
        eventRepo.save(event);
    }

    @Transactional
    public void removeEvent(Genre genre, Event event){
        Objects.requireNonNull(genre);
        Objects.requireNonNull(event);
        event.removeGenre(genre);
        eventRepo.save(event);
    }
}
