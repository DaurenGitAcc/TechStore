package com.absat.techshop.TechStore.models;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.persistence.*;
import java.io.IOException;
import java.time.LocalDateTime;

@Entity
@Table(name = "purchase")
public class Purchase {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "count")
    private int count;
    @ManyToOne
    @JoinColumn(name = "buyer_id",referencedColumnName = "id")
    private User buyer;
    @ManyToOne
    @JoinColumn(name = "product_id",referencedColumnName = "id")
    private Product product;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Purchase() {
    }

    public Purchase(int count, User buyer, Product product, LocalDateTime createdAt) {
        this.count = count;
        this.buyer = buyer;
        this.product = product;
        this.createdAt = createdAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public User getBuyer() {
        return buyer;
    }

    public void setBuyer(User buyer) {
        this.buyer = buyer;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    @Override
    public String toString() {
        return "{" +
                "\"id\":" + id +
                ",\"counts\":\"" + count +
                "\",\"price\":\"" + count*product.getPrice() +
                "\",\"buyer\":\"" + buyer.getName()+" "+buyer.getTelNumber() +
                "\",\"product\":\"" + product.getName() +
                "\",\"createdAt\":\"" + createdAt +
                "\"}";
    }
    // convert object to Json (not sure)
    public String Jsons(){
        ObjectMapper Obj = new ObjectMapper();
        String jsonStr="";
        try {
            // Converting the Java object into a JSON string
            jsonStr = Obj.writeValueAsString(this);
            // Displaying Java object into a JSON string
            System.out.println(jsonStr);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return jsonStr;
    }
}
