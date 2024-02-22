package com.example.semestralka.controllers;

import com.example.semestralka.controllers.util.RestUtils;
import com.example.semestralka.exceptions.NotFoundException;
import com.example.semestralka.exceptions.ValidationException;
import com.example.semestralka.model.Club;
import com.example.semestralka.model.Event;
import com.example.semestralka.model.Genre;
import com.example.semestralka.services.ClubService;
import com.example.semestralka.services.EventService;
import com.example.semestralka.services.GenreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/rest/events")
@PreAuthorize("permitAll()")
public class EventController {

    private final EventService eventService;

    private final GenreService genreService;

    private final ClubService clubService;

    @Autowired
    public EventController(EventService eventService, GenreService genreService, ClubService clubService) {
        this.eventService = eventService;
        this.genreService = genreService;
        this.clubService = clubService;
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Event getById(@PathVariable Integer id){
        final Event result = eventService.find(id);
        if (result==null) throw NotFoundException.create("Event", id);
        return result;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(value = "{id}/accept")
    public ResponseEntity<Void> acceptEvent(@PathVariable Integer id){
        final Event toAccept = eventService.find(id);
        eventService.acceptEvent(toAccept);
        final HttpHeaders headers = RestUtils.createLocationHeaderFromCurrentUri("/{id}", id);
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(value = "/not_accepted", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Event> getAllNotAccepted(){
        return eventService.getAllNotAccepted();
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Event> getAllUpcoming(){
        return eventService.getAllUpcomingEvents();
    }

    @GetMapping(value = "/by_genres", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Event> getAllUpcomingByGenres(@RequestParam(name = "genres", required = false) List<String> genreNames) {
        final List<Genre> genres = new ArrayList<>();
        for (String name : genreNames) {
            genres.add(genreService.findByName(name));
        }
        return eventService.getAllUpcomingByGenres(genres);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping(value = "/create/{clubName}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> createByUser(@RequestBody Event event, @PathVariable String clubName) {
        final Club club = clubService.findByName(clubName);
        eventService.createEventByUser(event, club);
        final HttpHeaders headers = RestUtils.createLocationHeaderFromCurrentUri("/{id}", event.getId());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(@PathVariable Integer id){
        final Event eventToRemove = eventService.find(id);
        if (eventToRemove==null) throw NotFoundException.create("Event", id);
        eventService.delete(eventToRemove);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void edit(@PathVariable Integer id, @RequestBody Event updatedEvent){
        final Event eventToUpdate = eventService.find(id);
        if (eventToUpdate == null || !eventService.exists(id)) throw NotFoundException.create("Event", id);
        updatedEvent.setId(id);
        updatedEvent.setClub(eventToUpdate.getClub());
        updatedEvent.setAccepted(true);
        eventService.update(updatedEvent);
    }
}
