package com.darpasyan.docker.model;


import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "users")


@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String username;

    private String password;

}
