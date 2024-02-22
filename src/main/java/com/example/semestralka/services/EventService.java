package com.example.semestralka.services;

import com.example.semestralka.data.ClubRepository;
import com.example.semestralka.data.EventRepository;
import com.example.semestralka.data.FavoriteRepository;
import com.example.semestralka.exceptions.NotFoundException;
import com.example.semestralka.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class EventService {

    private final EventRepository eventRepo;
    private final ClubRepository clubRepo;
    private final FavoriteRepository favoriteRepo;

    @Autowired
    public EventService(EventRepository eventRepo, ClubRepository clubRepo, FavoriteRepository favoriteRepo) {
        this.eventRepo = eventRepo;
        this.clubRepo = clubRepo;
        this.favoriteRepo = favoriteRepo;
    }

    @Transactional
    public void createEventByUser(Event event, Club club){
        Objects.requireNonNull(event);
        Objects.requireNonNull(club);
        if (clubRepo.existsById(club.getId())
            && event.getEventDate().isAfter(LocalDateTime.now())) {
            event.setClub(club);
            event.setAccepted(false);
            eventRepo.save(event);
        }
    }

    @Transactional
    public void acceptEvent(Event event){
        Objects.requireNonNull(event);
        if (eventRepo.existsById(event.getId())
                && clubRepo.existsById(event.getClub().getId())
                && event.getEventDate().isAfter(LocalDateTime.now())){
            event.setAccepted(true);
            Club club = event.getClub();
            club.addEvent(event);
            eventRepo.save(event);
            clubRepo.save(club);
        }
    }

    @Transactional(readOnly = true)
    public List<Event> getAllNotAccepted(){
        return eventRepo.getAllByAcceptedIsFalse();
    }

    @Transactional(readOnly = true)
    public List<Event> getAllUpcomingByClub(Club club){
        Objects.requireNonNull(club);
        try {
            return eventRepo.getAllUpcomingByClub(club);
        } catch (DataAccessException e) {
            throw new NotFoundException("There are no events in this club");
        }
    }

    @Transactional(readOnly = true)
    public List<Event> getAllUpcomingEvents(){
        try {
            return eventRepo.getAllUpcomingEvents();
        } catch (DataAccessException e) {
            throw new NotFoundException("There are no upcoming events");
        }
    }

    @Transactional(readOnly = true)
    public List<Event> getAllUpcomingByGenres(List<Genre> genres) {
        Objects.requireNonNull(genres);
        try {
            return eventRepo.getAllUpcomingEventsByGenres(genres);
        } catch (DataAccessException e) {
            throw new NotFoundException("There are no upcoming events by this genre");
        }
    }

    @Transactional(readOnly = true)
    public Event find(Integer id){
        Objects.requireNonNull(id);
        return eventRepo.findById(id).orElse(null);
    }
    @Transactional(readOnly = true)
    public Iterable<Event> findAll(){
        try {
            return eventRepo.findAll();
        } catch (DataAccessException e) {
            throw new NotFoundException("There are no events");
        }
    }

    @Transactional(readOnly = true)
    public Iterable<Event> getAllByGenre(Genre genre){
        try {
            return eventRepo.getAllByGenre(genre);
        } catch (DataAccessException e) {
            throw new NotFoundException("There are no events");
        }
    }

    @Transactional
    public void update(Event event){
        Objects.requireNonNull(event);
        if (exists(event.getId())) {
            eventRepo.save(event);
        }
    }

    @Transactional
    public void delete(Event event){
        Objects.requireNonNull(event);
        if (exists(event.getId())) {
            Club club = event.getClub();
            club.getEvents().remove(event);
            favoriteRepo.deleteByEvent(event);
            clubRepo.save(club);
            eventRepo.delete(event);
        }
    }

    public boolean exists(Integer id){
        Objects.requireNonNull(id);
        return  eventRepo.existsById(id);
    }
}
