package com.example.semestralka.controllers;

import com.example.semestralka.config.SecurityConfig;
import com.example.semestralka.environment.Environment;
import com.example.semestralka.environment.Generator;
import com.example.semestralka.environment.TestConfiguration;
import com.example.semestralka.model.Comment;
import com.example.semestralka.model.Event;
import com.example.semestralka.model.Role;
import com.example.semestralka.model.User;
import com.example.semestralka.security.model.UserDetails;
import com.example.semestralka.services.CommentService;
import com.example.semestralka.services.EventService;
import com.example.semestralka.services.security.UserDetailsService;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ContextConfiguration(
        classes = {CommentControllerSecurityTest.TestConfig.class, SecurityConfig.class})
public class CommentControllerSecurityTest extends BaseControllerTestRunner{

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CommentService commentService;

    @Autowired
    private EventService eventService;

    @Autowired
    private UserDetailsService userDetailsService;

    private User user;

    @BeforeEach
    public void setUp() {
        this.objectMapper = Environment.getObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.user = Generator.generateUser();
    }

    @AfterEach
    public void tearDown() {
        Environment.clearSecurityContext();
        Mockito.reset(userDetailsService, eventService, commentService);
    }

    @Configuration
    @TestConfiguration
    public static class TestConfig {

        @MockBean
        private UserDetailsService userDetailsService;

        @MockBean
        private CommentService commentService;

        @MockBean
        private EventService eventService;

        @Bean
        public CommentController commentController() {
            return new CommentController(userDetailsService, eventService, commentService);
        }
    }

    @WithAnonymousUser
    @Test
    public void addCommentThrowsUnauthorizedForAnonymousAccess() throws Exception {
        final Event event = Generator.generateUpcomingEvent();
        event.setId(1337);
        final Comment comment = Generator.generateComment();

        Authentication authMock = mock(Authentication.class);
        UserDetails userDetailsMock = mock(UserDetails.class);
        when(authMock.getPrincipal()).thenReturn(userDetailsMock);
        when(userDetailsMock.getUser()).thenReturn(user);
        when(eventService.find(event.getId())).thenReturn(event);

        mockMvc.perform(post("/rest/events/" + event.getId() + "/comments")
                        .content(toJson(comment))
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(authMock))
                .andExpect(status().isUnauthorized());
        verify(commentService, never()).save(any(), any(), any());
    }

    //TODO does not work
    @WithMockUser(roles = "USER")
    @WithUserDetails("testUsername")
    @Test
    public void addCommentWorksWithAuthorizedUser() throws Exception {
        user.setId(228);
        user.setRole(Role.USER);
        user.setUsername("testUsername");
        Environment.setCurrentUser(user);
        final Event event = Generator.generateUpcomingEvent();
        event.setId(1337);
        final Comment comment = Generator.generateComment();

        Authentication authMock = mock(Authentication.class);
        UserDetails userDetailsMock = mock(UserDetails.class);


        //todo WAY 2 (new)
//        when(authMock.getName()).thenReturn(user.getUsername());
//        when(userDetailsService.loadUserByUsername(user.getUsername())).thenReturn(userDetailsMock);
//        when(userDetailsMock.getUser()).thenReturn(user);

        //todo WAY 1 (old)
        when(authMock.getPrincipal()).thenReturn(userDetailsMock);
        when(userDetailsMock.getUser()).thenReturn(user);

        when(eventService.find(event.getId())).thenReturn(event);
        mockMvc.perform(post("/rest/events/" + event.getId() + "/comments")
                        .content(toJson(comment))
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(authMock))
                        .andExpect(status().isCreated());
        final ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);
        verify(commentService).save(captor.capture(), any(User.class), any(Event.class));
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void addCommentWorksWithAdmin() throws Exception {
        //todo
    }

    @WithAnonymousUser
    @Test
    public void removeCommentByEventIdAndCommentIdThrowsUnauthorizedForAnonymousAccess() throws Exception {
        Environment.setCurrentUser(user);
        final Event event = Generator.generateUpcomingEvent();
        event.setId(228);
        final Comment comment = Generator.generateComment();
        comment.setId(1337);

        mockMvc.perform(delete("/rest/events/" + event.getId() + "/comments/" + comment.getId()))
                .andExpect(status().isUnauthorized());
        verify(commentService, never()).delete(any());
    }

    @WithMockUser(roles = "USER")
    @Test
    public void removeCommentByEventIdAndCommentIdWorksWithAuthorizedUser() throws Exception {
        user.setRole(Role.USER);
        Environment.setCurrentUser(user);
        final Event event = Generator.generateUpcomingEvent();
        event.setId(228);
        final Comment comment = Generator.generateComment();
        comment.setId(1337);
        comment.setEvent(event);

        when(eventService.find(event.getId())).thenReturn(event);
        when(commentService.find(comment.getId())).thenReturn(comment);
        mockMvc.perform(delete("/rest/events/" + event.getId() + "/comments/" + comment.getId()))
                .andExpect(status().isNoContent());
        verify(commentService).delete(comment);
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void removeCommentByEventIdAndCommentIdWorksWithAdmin() throws Exception {
        user.setRole(Role.ADMIN);
        Environment.setCurrentUser(user);
        final Event event = Generator.generateUpcomingEvent();
        event.setId(228);
        final Comment comment = Generator.generateComment();
        comment.setId(1337);
        comment.setEvent(event);

        when(eventService.find(event.getId())).thenReturn(event);
        when(commentService.find(comment.getId())).thenReturn(comment);
        mockMvc.perform(delete("/rest/events/" + event.getId() + "/comments/" + comment.getId()))
                .andExpect(status().isNoContent());
        verify(commentService).delete(comment);
    }

    @WithAnonymousUser
    @Test
    public void editCommentThrowsUnauthorizedForAnonymousAccess() throws Exception {
        Environment.setCurrentUser(user);
        final Event event = Generator.generateUpcomingEvent();
        event.setId(228);
        final Comment existingComment = Generator.generateComment();
        existingComment.setId(1337);
        final Comment updatedComment = Generator.generateComment();

        mockMvc.perform(put("/rest/events/" + event.getId() + "/comments/" + existingComment.getId())
                        .content(toJson(updatedComment)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
        verify(commentService, never()).update(any());
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void editCommentThrowsForbiddenForAdmin() throws Exception {
        user.setRole(Role.ADMIN);
        Environment.setCurrentUser(user);
        final Event event = Generator.generateUpcomingEvent();
        event.setId(228);
        final Comment existingComment = Generator.generateComment();
        existingComment.setId(1337);
        final Comment updatedComment = Generator.generateComment();

        mockMvc.perform(put("/rest/events/" + event.getId() + "/comments/" + existingComment.getId())
                        .content(toJson(updatedComment)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
        verify(commentService, never()).update(any());
    }

    @WithMockUser(roles = "USER")
    @Test
    public void editCommentWorksWithAuthorizedUser() throws Exception {
        user.setRole(Role.USER);
        Environment.setCurrentUser(user);
        final Event event = Generator.generateUpcomingEvent();
        event.setId(228);
        final Comment existingComment = Generator.generateComment();
        existingComment.setId(1337);
        existingComment.setEvent(event);
        event.addComment(existingComment);
        final Comment updatedComment = Generator.generateComment();

        when(eventService.find(event.getId())).thenReturn(event);
        when(commentService.find(existingComment.getId())).thenReturn(existingComment);
        mockMvc.perform(put("/rest/events/" + event.getId() + "/comments/" + existingComment.getId())
                        .content(toJson(updatedComment)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        verify(commentService).update(existingComment);
    }
}
