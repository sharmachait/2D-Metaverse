package com.sharmachait.ws.models.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
//@Entity
public class User {
//    @Id
//    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String username;

    private Status status;
}
