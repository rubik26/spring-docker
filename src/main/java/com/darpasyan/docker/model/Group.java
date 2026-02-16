package com.darpasyan.docker.model;

import com.darpasyan.docker.model.User.User;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "groups")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;


    private String name;
    private String description;
    private byte[] avatar;
    private String avatarFileName;
    private String avatarFileType;
    private LocalDate dateOfCreate;

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private User admin;

    @ManyToMany
    @JoinTable(
            name = "group_moderators",
            joinColumns = @JoinColumn(name = "group_id"), // ключ этой сущности
            inverseJoinColumns = @JoinColumn(name = "user_id") // ключ другой сущности
    )
    private List<User> moderators;

    @ManyToMany()
    @JoinTable(
            name = "group_participants",
            joinColumns = @JoinColumn(name = "group_id"), // ключ этой сущности
            inverseJoinColumns = @JoinColumn(name = "user_id") // ключ другой сущности
    )
    @JsonManagedReference
    private Set<User> participants;




}
