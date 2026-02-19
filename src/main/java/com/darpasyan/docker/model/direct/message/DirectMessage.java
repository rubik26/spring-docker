package com.darpasyan.docker.model.direct.message;


import com.darpasyan.docker.model.User.User;
import com.darpasyan.docker.model.direct.Direct;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "direct_messages")
public class DirectMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private  String content;
    private LocalDateTime dateOfSend;
    private boolean isEdited;
    private boolean isWatched;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "direct_id")
    private Direct direct;
}
