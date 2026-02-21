package com.darpasyan.docker.model.group.message;


import com.darpasyan.docker.model.group.Group;
import com.darpasyan.docker.model.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Entity
@Table(name = "group_messages")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private  String content;
    private LocalDateTime dateOfSend;
    private boolean isEdited;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;
}
