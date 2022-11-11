package com.absat.techshop.TechStore.models;


import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "user_store")
public class User {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "name")
    @NotEmpty(message = "Name field should not be empty")
    private String name;
    @Column(name = "surname")
    @NotEmpty(message = "Surname field should not be empty")
    private String surname;
    @Email
    @Column(name = "email")
    @NotEmpty(message = "Email field should not be empty")
    private String email;
    @Column(name = "password")
    @NotEmpty(message = "Password field should not be empty")
    private String password;
    @Column(name = "tel_number")
   // @NotEmpty(message = "Tel. number field should not be empty")
    private String telNumber;
    @Column(name = "avatar")
    private String avatar;
    @Column(name = "role")
    private String role;

    public User() {
    }

    public User(String name, String surname, String email, String password, String telNumber) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.telNumber = telNumber;
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

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTelNumber() {
        return telNumber;
    }

    public void setTelNumber(String telNumber) {
        this.telNumber = telNumber;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "{" +
                "\"id\":" + id +
                ", \"name\":\"" + name +
                "\", \"surname\":\"" + surname +
                "\", \"email\":\"" + email +
                "\", \"password\":\"" + password +
                "\", \"telNumber\":\"" + telNumber +
                "\", \"avatar\":\"" + avatar +
                "\", \"role\":\"" + role +
                "\"}";
    }

}
