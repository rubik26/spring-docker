package com.darpasyan.docker.model.group.message.image;

import com.darpasyan.docker.model.group.message.GroupMessage;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "group_message_images")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupMessageImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String imageName;

    @Column(columnDefinition = "bytea")
    private byte[] imageData;
    private String imageType;


    @ManyToOne
    @JoinColumn(name = "message_id")
    private GroupMessage message;
}
