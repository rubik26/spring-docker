package com.darpasyan.docker.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "images")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String imageName;

    @Column(columnDefinition = "bytea")
    private byte[] imageData;
    private String imageType;


    @ManyToOne
    @JoinColumn(name = "message_id")
    private Message message;
}
