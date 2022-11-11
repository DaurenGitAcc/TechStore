package com.absat.techshop.TechStore.repositories;

import com.absat.techshop.TechStore.models.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchasesRepository extends JpaRepository<Purchase,Integer> {
}
