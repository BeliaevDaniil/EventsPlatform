package com.example.semestralka.services;

import com.example.semestralka.data.CommentRepository;
import com.example.semestralka.data.EventRepository;
import com.example.semestralka.data.UserRepository;
import com.example.semestralka.exceptions.NotFoundException;
import com.example.semestralka.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class CommentService {

    private final CommentRepository commentRepo;
    private final EventRepository eventRepo;
    private final UserRepository userRepo;

    @Autowired
    public CommentService(CommentRepository commentRepo, EventRepository eventRepo, UserRepository userRepo) {
        this.commentRepo = commentRepo;
        this.eventRepo = eventRepo;
        this.userRepo = userRepo;
    }

    @Transactional
    public void save(Comment comment, User user, Event event){
        Objects.requireNonNull(comment);
        Objects.requireNonNull(user);
        if (userRepo.existsById(user.getId()) && eventRepo.existsById(event.getId())) {
            comment.setUser(user);
            comment.setEvent(event);
            user.addComment(comment);
            event.addComment(comment);
            commentRepo.save(comment);
            userRepo.save(user);
            eventRepo.save(event);
        }
    }

    @Transactional(readOnly = true)
    public Comment find(Integer id){
        Objects.requireNonNull(id);
        return commentRepo.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @PostFilter("filterObject.user.username == principal.username")
    public Iterable<Comment> findAll(){
        try {
            return commentRepo.findAll();
        } catch (DataAccessException e) {
            throw new NotFoundException("There are no comments");
        }
    }

    @Transactional
    public void update(Comment comment){
        Objects.requireNonNull(comment);
        if (exists(comment.getId())) {
            commentRepo.save(comment);
        }
    }

    @Transactional
    public void delete(Comment comment, User user){
        Objects.requireNonNull(comment);
        if (exists(comment.getId()) && comment.getUser().equals(user)) {
            comment.getUser().removeComment(comment);
            comment.getEvent().removeComment(comment);
            userRepo.save(comment.getUser());
            eventRepo.save(comment.getEvent());
            commentRepo.delete(comment);
        }
    }

    public boolean exists(Integer id){
        Objects.requireNonNull(id);
        return commentRepo.existsById(id);
    }

    @Transactional(readOnly = true)
    public List<Comment> getAllByUser(User user){
        if (userRepo.existsById(user.getId())){
            List<Comment> comments = user.getComments();
            if (comments.isEmpty()){
                throw new NotFoundException("There are no comments");
            }
            return comments;
        }
        throw new NotFoundException("User does not exist");
    }

    @Transactional(readOnly = true)
    public List<Comment> getAllByEvent(Event event){
        Objects.requireNonNull(event);
        if (eventRepo.existsById(event.getId())){
            List<Comment> comments = event.getComments();
            if (comments.isEmpty()){
                throw new NotFoundException("There are no comments");
            }
            return comments;
        }
        throw new NotFoundException("Event does not exist");
    }

    @Transactional(readOnly = true)
    public List<Comment> getAllByEventFromFirst(Event event){
        Objects.requireNonNull(event);
        try {
            return commentRepo.getAllByEventFromFirst(event);
        } catch (DataAccessException e) {
            throw new NotFoundException("Event does not exist");
        }
    }

    @Transactional(readOnly = true)
    public List<Comment> getAllByEventFromLast(Event event){
        Objects.requireNonNull(event);
        if (eventRepo.existsById(event.getId())){
            List<Comment> comments = commentRepo.getAllByEventFromLast(event);
            if (comments.isEmpty()){
                throw new NotFoundException("There are no comments");
            }
            return comments;
        }
        throw new NotFoundException("Event does not exist");
    }
}
