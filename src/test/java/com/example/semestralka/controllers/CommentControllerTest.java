package com.example.semestralka.controllers;

import com.example.semestralka.controllers.handler.ErrorInfo;
import com.example.semestralka.data.EventRepository;
import com.example.semestralka.data.UserRepository;
import com.example.semestralka.environment.Generator;
import com.example.semestralka.model.Comment;
import com.example.semestralka.model.Event;
import com.example.semestralka.model.User;
import com.example.semestralka.security.model.UserDetails;
import com.example.semestralka.services.CommentService;
import com.example.semestralka.services.EventService;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MvcResult;

import java.security.Principal;
import java.util.List;
import java.util.stream.IntStream;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class CommentControllerTest extends BaseControllerTestRunner{

    @Mock
    private CommentService commentServiceMock;

    @Mock
    private EventService eventServiceMock;

    @InjectMocks
    private CommentController sut;

    @BeforeEach
    public void setUp() {
        super.setUp(sut);
    }

    @Test
    public void getAllCommentsByEventIdReturnsCommentsWithMatchingEventId() throws Exception {
        final List<Comment> comments = IntStream.range(0, 5).mapToObj(i -> {
            final Comment comment = Generator.generateComment();
            comment.setId(Generator.randomInt());
            return comment;
        }).toList();
        final Event event = Generator.generateUpcomingEvent();
        event.setId(1337);
        event.setComments(comments);

        when(eventServiceMock.find(event.getId())).thenReturn(event);
        final MvcResult mvcResult = mockMvc.perform(get("/rest/events/" + event.getId() + "/comments/all")).andReturn();
        final List<Comment> result = readValue(mvcResult, new TypeReference<>() {});

        for (Comment comment : result){
            assertEquals(comment.getEvent().getId(), event.getId());
        }
    }

    @Test
    public void getAllCommentsByEventIdThrowsNotFoundForUnknownEventId() throws Exception {
        final int id = 111;
        final MvcResult mvcResult = mockMvc.perform(get("/rest/events/" + id + "/comments/all")).andReturn();
        final ErrorInfo result = readValue(mvcResult, ErrorInfo.class);
        assertNotNull(result);
        assertThat(result.getMessage(), containsString("Event identified by "));
        assertThat(result.getMessage(), containsString(Integer.toString(id)));
    }

    @Test
    public void getAllCommentsByEventIdFromFirstReturnsCommentsByMatchingEventId() throws Exception {
        final List<Comment> comments = IntStream.range(0, 5).mapToObj(i -> {
            final Comment comment = Generator.generateComment();
            comment.setId(Generator.randomInt());
            return comment;
        }).toList();
        final Event event = Generator.generateUpcomingEvent();
        event.setId(1337);
        event.setComments(comments);

        when(eventServiceMock.find(event.getId())).thenReturn(event);
        final MvcResult mvcResult = mockMvc.perform(get("/rest/events/" + event.getId() + "/comments/from_first")).andReturn();
        final List<Comment> result = readValue(mvcResult, new TypeReference<>() {});

        for (Comment comment : result){
            assertEquals(comment.getEvent().getId(), event.getId());
        }
    }

    @Test
    public void getAllCommentsByEventIdFromFirstThrowsNotFoundForUnknownEventId() throws Exception {
        final int id = 111;
        final MvcResult mvcResult = mockMvc.perform(get("/rest/events/" + id + "/comments/from_first")).andReturn();
        final ErrorInfo result = readValue(mvcResult, ErrorInfo.class);
        assertNotNull(result);
        assertThat(result.getMessage(), containsString("Event identified by "));
        assertThat(result.getMessage(), containsString(Integer.toString(id)));
    }

    @Test
    public void getAllCommentsByEventIdFromFirstGetsAllUsingCommentService() throws Exception {
        final List<Comment> comments = IntStream.range(0, 5).mapToObj(i -> {
            final Comment comment = Generator.generateComment();
            comment.setId(Generator.randomInt());
            return comment;
        }).toList();
        final Event event = Generator.generateUpcomingEvent();
        event.setId(1337);
        event.setComments(comments);
        when(eventServiceMock.find(event.getId())).thenReturn(event);
        when(commentServiceMock.getAllByEventFromFirst(event)).thenReturn(comments);
        final MvcResult mvcResult = mockMvc.perform(get("/rest/events/" + event.getId() + "/comments/from_first")).andReturn();
        final List<Comment> result = readValue(mvcResult, new TypeReference<>() {});
        final ArgumentCaptor<Event> captor = ArgumentCaptor.forClass(Event.class);
        assertEquals(result.size(), comments.size());
        verify(commentServiceMock).getAllByEventFromFirst(captor.capture());
    }

    @Test
    public void getAllCommentsByEventIdFromLastReturnsCommentsByMatchingEventId() throws Exception {
        final List<Comment> comments = IntStream.range(0, 5).mapToObj(i -> {
            final Comment comment = Generator.generateComment();
            comment.setId(Generator.randomInt());
            return comment;
        }).toList();
        final Event event = Generator.generateUpcomingEvent();
        event.setId(1337);
        event.setComments(comments);

        when(eventServiceMock.find(event.getId())).thenReturn(event);
        final MvcResult mvcResult = mockMvc.perform(get("/rest/events/" + event.getId() + "/comments/from_last")).andReturn();
        final List<Comment> result = readValue(mvcResult, new TypeReference<>() {});

        for (Comment comment : result){
            assertEquals(comment.getEvent().getId(), event.getId());
        }
    }

    @Test
    public void getAllCommentsByEventIdFromLastThrowsNotFoundForUnknownEventId() throws Exception {
        final int id = 111;
        final MvcResult mvcResult = mockMvc.perform(get("/rest/events/" + id + "/comments/from_last")).andReturn();
        final ErrorInfo result = readValue(mvcResult, ErrorInfo.class);
        assertNotNull(result);
        assertThat(result.getMessage(), containsString("Event identified by "));
        assertThat(result.getMessage(), containsString(Integer.toString(id)));
    }

    @Test
    public void getAllCommentsByEventIdFromLastGetsAllUsingCommentService() throws Exception {
        final List<Comment> comments = IntStream.range(0, 5).mapToObj(i -> {
            final Comment comment = Generator.generateComment();
            comment.setId(Generator.randomInt());
            return comment;
        }).toList();
        final Event event = Generator.generateUpcomingEvent();
        event.setId(1337);
        event.setComments(comments);
        when(eventServiceMock.find(event.getId())).thenReturn(event);
        when(commentServiceMock.getAllByEventFromLast(event)).thenReturn(comments);
        final MvcResult mvcResult = mockMvc.perform(get("/rest/events/" + event.getId() + "/comments/from_last")).andReturn();
        final List<Comment> result = readValue(mvcResult, new TypeReference<>() {});
        final ArgumentCaptor<Event> captor = ArgumentCaptor.forClass(Event.class);
        assertEquals(result.size(), comments.size());
        verify(commentServiceMock).getAllByEventFromLast(captor.capture());
    }

    @Test
    public void addCommentSavesCommentByUsingCommentService() throws Exception {
        final Event event = Generator.generateUpcomingEvent();
        event.setId(228);
        final Comment comment = Generator.generateComment();
        final User user = Generator.generateUser();
        user.setId(111);
        Authentication authMock = mock(Authentication.class);
        UserDetails userDetailsMock = mock(UserDetails.class);
        when(authMock.getPrincipal()).thenReturn(userDetailsMock);
        when(userDetailsMock.getUser()).thenReturn(user);
        when(eventServiceMock.find(event.getId())).thenReturn(event);

        mockMvc.perform(post("/rest/events/" + event.getId() + "/comments")
                        .content(toJson(comment))
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(authMock))
                        .andExpect(status().isCreated());
        final ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);
        verify(commentServiceMock).save(captor.capture(), any(User.class), any(Event.class));
        assertEquals(comment.getText(), captor.getValue().getText());
    }

    @Test
    public void removeCommentDeletesCommentByUsingCommentService() throws Exception {
        final Event event = Generator.generateUpcomingEvent();
        event.setId(228);
        final Comment comment = Generator.generateComment();
        comment.setId(1337);
        comment.setEvent(event);
        final User user = Generator.generateUser();
        user.setId(111);
        Authentication authMock = mock(Authentication.class);
        UserDetails userDetailsMock = mock(UserDetails.class);
        when(authMock.getPrincipal()).thenReturn(userDetailsMock);
        when(userDetailsMock.getUser()).thenReturn(user);
        when(eventServiceMock.find(event.getId())).thenReturn(event);
        when(commentServiceMock.find(comment.getId())).thenReturn(comment);
        mockMvc.perform(delete("/rest/events/" + event.getId() + "/comments/" + comment.getId())
                    .principal(authMock))
                .andExpect(status().isNoContent());
        verify(commentServiceMock).delete(comment, user);
    }

    @Test
    public void editCommentUpdatesCommentByUsingCommentService() throws Exception {
        final User user = Generator.generateUser();
        user.setId(111);
        final Event event = Generator.generateUpcomingEvent();
        event.setId(228);
        final Comment existingComment = Generator.generateComment();
        existingComment.setId(1337);
        existingComment.setEvent(event);
        existingComment.setUser(user);
        event.addComment(existingComment);

        Authentication authMock = mock(Authentication.class);
        UserDetails userDetailsMock = mock(UserDetails.class);
        when(authMock.getPrincipal()).thenReturn(userDetailsMock);
        when(userDetailsMock.getUser()).thenReturn(user);
        final Comment updatedComment = Generator.generateComment();
        when(eventServiceMock.find(event.getId())).thenReturn(event);
        when(commentServiceMock.find(existingComment.getId())).thenReturn(existingComment);
        mockMvc.perform(put("/rest/events/" + event.getId() + "/comments/" + existingComment.getId())
                        .principal(authMock)
                        .content(toJson(updatedComment)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        verify(commentServiceMock).update(existingComment);
    }
}
