package com.example.semestralka.controllers;

import com.example.semestralka.controllers.util.RestUtils;
import com.example.semestralka.exceptions.NotFoundException;
import com.example.semestralka.model.Event;
import com.example.semestralka.model.Genre;
import com.example.semestralka.services.EventService;
import com.example.semestralka.services.GenreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("rest/genres")
public class GenreController {

    private final GenreService genreService;
    private final EventService eventService;

    @Autowired
    public GenreController(GenreService genreService, EventService eventService) {
        this.genreService = genreService;
        this.eventService = eventService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Iterable<Genre> getAll(){
        return genreService.findAll();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> addGenre(@RequestBody Genre genre){
        genreService.save(genre);
        final HttpHeaders headers = RestUtils.createLocationHeaderFromCurrentUri("/{id}", genre.getId());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping(value = "/{id}")
    public void removeGenre(@PathVariable Integer id) {
        Genre genreToRemove = genreService.find(id);
        if (genreToRemove==null) throw NotFoundException.create("Genre", id);
        genreService.delete(genreToRemove);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Genre getById(@PathVariable Integer id) {
        final Genre genre = genreService.find(id);
        if (genre == null) {
            throw NotFoundException.create("Genre", id);
        }
        return genre;
    }

    @GetMapping(value = "/{id}/events", produces = MediaType.APPLICATION_JSON_VALUE)
    public Iterable<Event> getEventsByGenre(@PathVariable Integer id) {
        final Genre genre = genreService.find(id);
        return eventService.getAllByGenre(genre);
    }

    @PostMapping(value = "/{id}/events", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void addEventToGenre(@PathVariable Integer id, @RequestBody Event event) {
        final Genre genre = getById(id);
        genreService.addEvent(genre, event);
    }

    @DeleteMapping(value = "/{genreId}/events/{eventId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void removeEventFromGenre(@PathVariable Integer genreId, @PathVariable Integer eventId) {
        final Genre genre = genreService.find(genreId);
        final Event eventToRemove = eventService.find(eventId);
        if (eventToRemove != null) {
            genreService.removeEvent(genre, eventToRemove);
        } else throw NotFoundException.create("Event", eventId);
    }
}
