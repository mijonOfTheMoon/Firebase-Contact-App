package com.example.firebasedemo;

import com.google.firebase.database.Exclude;

public class Contact {

    @Exclude
    private String id;
    private String name;
    private String email;
    private String phone;
    
    public Contact() {
    }

    public Contact(String name, String email, String phone) {
        this.name = name;
        this.phone = phone;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}