package com.darpasyan.docker.model;


import jakarta.persistence.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private  String name;
    private  String description;
    private BigDecimal price;
    private Date dateOfCreate;
    private MultipartFile avatar;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
