package com.absat.techshop.TechStore.repositories;

import com.absat.techshop.TechStore.models.Category;
import com.absat.techshop.TechStore.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductsRepository extends JpaRepository<Product,Integer> {
    List<Product> findByCategoryEquals(Category category);
}
