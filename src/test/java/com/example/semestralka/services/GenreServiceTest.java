package com.example.semestralka.services;

import com.example.semestralka.data.EventRepository;
import com.example.semestralka.data.GenreRepository;
import com.example.semestralka.environment.Generator;
import com.example.semestralka.model.Event;
import com.example.semestralka.model.Genre;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
@Transactional
public class GenreServiceTest {

    @Autowired
    private GenreService genreService;
    @Autowired
    private GenreRepository genreRepo;
    @Autowired
    private EventRepository eventRepo;

    private Genre genre;

    @BeforeEach
    public void setUp(){
        genre = Generator.generateGenre();
        genreRepo.save(genre);
    }

    @Test
    public void deleteGenreRemovesGenreFromEvents(){
        List<Event> events = Arrays.asList(Generator.generateUpcomingEvent(), Generator.generateUpcomingEvent(), Generator.generateUpcomingEvent());
        int i = 0;
        for (Event event : events){
            ++i;
            event.setId(i);
            eventRepo.save(event);
            genre.addEvent(event);
            event.addGenre(genre);
        }
        genreService.delete(genre);
        for (Event event : events){
            assertFalse(event.getGenres().contains(genre));
        }
    }
}
