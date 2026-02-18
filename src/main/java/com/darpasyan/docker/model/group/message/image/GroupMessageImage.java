package com.darpasyan.docker.model.group.message.image;

import com.darpasyan.docker.model.group.message.GroupMessage;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "group_message_images")
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id"
)
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
    @JsonIgnore
    private GroupMessage message;
}
