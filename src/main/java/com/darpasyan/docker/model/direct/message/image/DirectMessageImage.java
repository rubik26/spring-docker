package com.darpasyan.docker.model.direct.message.image;

import com.darpasyan.docker.model.direct.message.DirectMessage;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "direct_message_images")
@Data
public class DirectMessageImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String imageName;

    @Column(columnDefinition = "bytea")
    private byte[] imageData;
    private String imageType;


    @ManyToOne
    @JoinColumn(name = "message_id")
    @JsonIgnore
    private DirectMessage message;
}
