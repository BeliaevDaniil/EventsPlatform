package com.example.semestralka.services;

import com.example.semestralka.data.GenreRepository;
import com.example.semestralka.model.Genre;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Objects;

@Service
public class GenreService {

    private final GenreRepository genreRepo;

    @Autowired
    public GenreService(GenreRepository genreRepo) {
        this.genreRepo = genreRepo;
    }

    public Genre find(Integer id){
        Objects.requireNonNull(id);
        return genreRepo.findById(id).orElse(null);
    }

    public Iterable<Genre> findAll(){
        try {
            return genreRepo.findAll();
        } catch (RuntimeException e) {
            throw new RuntimeException("There are no genres");
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
}
