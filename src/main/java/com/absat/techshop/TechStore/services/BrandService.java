package com.absat.techshop.TechStore.services;

import com.absat.techshop.TechStore.models.Brand;
import com.absat.techshop.TechStore.models.Category;
import com.absat.techshop.TechStore.repositories.BrandsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class BrandService {
    private final BrandsRepository brandsRepository;

    @Autowired
    public BrandService(BrandsRepository brandsRepository) {
        this.brandsRepository = brandsRepository;
    }
    public List<Brand> findAll(){
        return brandsRepository.findAll();
    }
    public Optional<Brand> findOne(int id){
        return brandsRepository.findById(id);
    }

    @Transactional
    public void save(Brand brand){
        brandsRepository.save(brand);
    }
    @Transactional
    public void update(Brand brand){
        brandsRepository.save(brand);
    }
    @Transactional
    public void delete(int id){
        brandsRepository.deleteById(id);
    }
}
