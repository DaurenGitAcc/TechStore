package com.absat.techshop.TechStore.models;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

@Entity
@Table(name = "product")
public class Product {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "name")
    @NotEmpty(message = "Name field should not be empty")
    private String name;
    @Column(name = "price")
    private int price;
    @Column(name = "description")
    @NotEmpty(message = "Description field should not be empty")
    private String description;
    @Column(name = "count")
    @Min(value = 0,message = "Count should be greater than 0")
    private int count;
    @Column(name = "photo")
    private String photo;
    @ManyToOne
    @JoinColumn(name = "brand_id",referencedColumnName = "id")
    private Brand brand;
    @ManyToOne
    @JoinColumn(name = "category_id",referencedColumnName = "id")
    private Category category;

    public Product() {
    }

    public Product(String name, int price, String description, int count, String photo, Brand brand, Category category) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.count = count;
        this.photo = photo;
        this.brand = brand;
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "{" +
                "\"id\":" + id +
                ",\"name\":\"" + name  +
                "\",\"price\":\"" + price +
                "\",\"description\":\"" + description  +
                "\",\"count\":\"" + count +
                "\",\"photo\":\"" + photo  +
                "\",\"brand\":\"" + brand.getName()  +
                "\",\"category\":\"" + category.getName()  +
                "\"}";
    }
}
