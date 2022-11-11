package com.absat.techshop.TechStore.repositories;

import com.absat.techshop.TechStore.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriesRepository extends JpaRepository<Category,Integer> {
}
