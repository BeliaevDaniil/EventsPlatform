package com.example.semestralka.data;

import com.example.semestralka.Application;
import com.example.semestralka.environment.TestConfiguration;
import com.example.semestralka.model.Club;
import com.example.semestralka.model.Event;
import com.example.semestralka.model.Genre;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJson;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.ActiveProfiles;


import java.util.ArrayList;
import java.util.List;

import static com.example.semestralka.environment.Generator.*;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ComponentScan(basePackageClasses = Application.class, excludeFilters = {@ComponentScan.Filter(type = FilterType.ANNOTATION, classes = TestConfiguration.class)})
@AutoConfigureJson
@ActiveProfiles("test")
public class EventRepositoryTest {

    @Autowired
    public EventRepository eventRepository;

    @Autowired
    public GenreRepository genreRepository;

    @Autowired
    public ClubRepository clubRepository;

    @Test
    public void createEventTest() {
        final Club club = generateClub();

        final Event event = generateUpcomingEvent();
        event.setClub(club);

        eventRepository.save(event);
        assertNotNull(event.getId());
    }

    @Test
    public void findUpcomingEventsByGenresTest() {
        final Event event1 = generateUpcomingEvent();

        final Event event2 = generateUpcomingEvent();

        final Genre genre1 = generateGenre();
        final Genre genre2 = generateGenre();
        genreRepository.save(genre1);
        genreRepository.save(genre2);

        List<Genre> genres = new ArrayList<>();
        genres.add(genre1);
        genres.add(genre2);

        event1.addGenre(genre1);

        eventRepository.save(event1);
        eventRepository.save(event2);

        //test with 1 upcoming event
        List<Event> eventsByGenres = eventRepository.getAllUpcomingEventsByGenres(genres);
        assertEquals(eventsByGenres.size(), 1);
        assertEquals(eventsByGenres.get(0).getId(), event1.getId());

        //test with 2 upcoming events
        event2.addGenre(genre2);
        eventRepository.save(event2);

        eventsByGenres = eventRepository.getAllUpcomingEventsByGenres(genres);
        assertEquals(eventsByGenres.size(), 2);
        assertEquals(eventsByGenres.get(0).getId(), event1.getId());
        assertEquals(eventsByGenres.get(1).getId(), event2.getId());
    }

    @Test
    public void getAllByClubTest() {
        Event event1 = generateUpcomingEvent();
        Event event2 = generateUpcomingEvent();
        Event event3 = generateFinishedEvent();

        Club club = generateClub();

        event1.setClub(club);
        event2.setClub(club);
        event3.setClub(club);

        eventRepository.save(event1);
        eventRepository.save(event2);
        eventRepository.save(event3);

        clubRepository.save(club);

        List<Event> eventsByClub = eventRepository.getAllByClub(club);
        assertEquals(eventsByClub.size(), 3);
    }

    @Test
    public void getAllUpcomingByClubTest() {
        Event event1 = generateUpcomingEvent();
        Event event2 = generateUpcomingEvent();
        Event event3 = generateFinishedEvent();

        Club club = generateClub();

        event1.setClub(club);
        event2.setClub(club);
        event3.setClub(club);

        eventRepository.save(event1);
        eventRepository.save(event2);
        eventRepository.save(event3);

        clubRepository.save(club);

        List<Event> eventsByClub = eventRepository.getAllUpcomingByClub(club);
        assertEquals(eventsByClub.size(), 2);
    }
}
