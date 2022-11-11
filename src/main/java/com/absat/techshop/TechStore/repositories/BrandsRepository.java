package com.absat.techshop.TechStore.repositories;

import com.absat.techshop.TechStore.models.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BrandsRepository extends JpaRepository<Brand,Integer> {
}
