package com.example.semestralka.data;


import com.example.semestralka.model.Genre;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GenreRepository extends CrudRepository<Genre, Integer> {
    Genre getByName(String name);

    boolean existsByName(String name);
}
