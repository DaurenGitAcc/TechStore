package com.absat.techshop.TechStore.repositories;

import com.absat.techshop.TechStore.models.Comment;
import com.absat.techshop.TechStore.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findByProduct(Product product);
}
