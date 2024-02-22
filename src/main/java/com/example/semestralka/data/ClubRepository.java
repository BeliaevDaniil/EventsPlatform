package com.example.semestralka.data;

import com.example.semestralka.model.Club;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClubRepository extends CrudRepository<Club, Integer> {
    Club getByName(String name);

    boolean existsByName(String name);
}
