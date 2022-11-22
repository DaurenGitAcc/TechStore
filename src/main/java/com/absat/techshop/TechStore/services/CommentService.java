package com.absat.techshop.TechStore.services;

import com.absat.techshop.TechStore.models.Comment;
import com.absat.techshop.TechStore.models.Product;
import com.absat.techshop.TechStore.repositories.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class CommentService {
    private final CommentRepository commentRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public List<Comment> findAll() {
        return commentRepository.findAll();
    }

    public List<Comment> findByProduct(Product product) {
        return commentRepository.findByProduct(product);
    }

    public Optional<Comment> findOne(int id) {
        return commentRepository.findById(id);
    }

    @Transactional
    public void save(Comment comment) {
        comment.setCreatedAt(LocalDateTime.now());
        commentRepository.save(comment);
    }

    @Transactional
    public void update(Comment comment) {
        commentRepository.save(comment);
    }

}
