package com.darpasyan.docker.model.Messages;


import com.darpasyan.docker.model.User.User;
import com.darpasyan.docker.model.Direct;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

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
    private LocalDate dateOfSend;
    private boolean isEdited;
    private boolean isWatched;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "direct_id")
    private Direct direct;
}
