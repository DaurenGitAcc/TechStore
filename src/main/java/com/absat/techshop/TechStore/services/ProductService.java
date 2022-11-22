package com.absat.techshop.TechStore.services;

import com.absat.techshop.TechStore.models.Category;
import com.absat.techshop.TechStore.models.Product;
import com.absat.techshop.TechStore.repositories.CategoriesRepository;
import com.absat.techshop.TechStore.repositories.ProductsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ProductService {
    private final ProductsRepository productsRepository;
    private final CategoriesRepository categoriesRepository;

    @Autowired
    public ProductService(ProductsRepository productsRepository, CategoriesRepository categoriesRepository) {
        this.productsRepository = productsRepository;
        this.categoriesRepository = categoriesRepository;
    }

    public List<Product> findAll() {
        return productsRepository.findAll();
    }

    public List<Product> findByName(String substring) {
        return productsRepository.findByNameContaining(substring);
    }

    public Optional<Product> findOne(int id) {
        return productsRepository.findById(id);
    }

    @Transactional
    public void save(Product product) {
        if (product.getPhoto().isEmpty())
            product.setPhoto("https://via.placeholder.com/100");
        productsRepository.save(product);
    }

    @Transactional
    public void update(Product product) {
        if (product.getPhoto().isEmpty())
            product.setPhoto("https://via.placeholder.com/100");
        productsRepository.save(product);
    }

    @Transactional
    public void delete(int id) {
        productsRepository.deleteById(id);
    }

    @Transactional
    public void changeCount(int id, int count) {
        Optional<Product> productOptional = productsRepository.findById(id);
        Product product = null;
        if (productOptional.isPresent()) {
            product = productOptional.get();
            product.setCount(count);
        }
        productsRepository.save(product);
    }

    @Transactional
    public List<Product> findByCategory(int category_id) {
        Optional<Category> category = categoriesRepository.findById(category_id);
        return productsRepository.findByCategoryEquals(category.get());
    }

    public int getProductCount(int product_id) {
        return productsRepository.findById(product_id).get().getCount();
    }
}
