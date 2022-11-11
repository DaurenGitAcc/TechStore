package com.absat.techshop.TechStore.services;

import com.absat.techshop.TechStore.models.Category;
import com.absat.techshop.TechStore.models.Product;
import com.absat.techshop.TechStore.repositories.CategoriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class CategoryService {
    private final CategoriesRepository categoriesRepository;

    @Autowired
    public CategoryService(CategoriesRepository categoriesRepository) {
        this.categoriesRepository = categoriesRepository;
    }
    public List<Category> findAll(){
        return categoriesRepository.findAll();
    }
    public Optional<Category> findOne(int id){
        return categoriesRepository.findById(id);
    }
    @Transactional
    public void save(Category category){
        categoriesRepository.save(category);
    }
    @Transactional
    public void update(Category category){
        categoriesRepository.save(category);
    }
    @Transactional
    public void delete(int id){
        categoriesRepository.deleteById(id);
    }
}
