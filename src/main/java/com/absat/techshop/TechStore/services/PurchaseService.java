package com.absat.techshop.TechStore.services;

import com.absat.techshop.TechStore.models.Purchase;
import com.absat.techshop.TechStore.repositories.PurchasesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PurchaseService {
    private final PurchasesRepository purchasesRepository;

    @Autowired
    public PurchaseService(PurchasesRepository purchasesRepository) {
        this.purchasesRepository = purchasesRepository;
    }

    public List<Purchase> findAll(){
        return purchasesRepository.findAll();
    }
    public Optional<Purchase> findOne(int id){
        return purchasesRepository.findById(id);
    }
}
