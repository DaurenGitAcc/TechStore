package com.absat.techshop.TechStore.repositories;

import com.absat.techshop.TechStore.models.Purchase;
import com.absat.techshop.TechStore.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchasesRepository extends JpaRepository<Purchase, Integer> {
    List<Purchase> findByBuyerEquals(User user);
}
