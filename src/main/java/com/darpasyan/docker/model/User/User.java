package com.darpasyan.docker.model.User;


import com.darpasyan.docker.model.group.Group;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;


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


    @ManyToMany(mappedBy = "participants")
    private Set<Group> groups;



    @ManyToMany
    @JoinTable(
            name = "user_blacklist",
            joinColumns = @JoinColumn(name = "user_id"),

            inverseJoinColumns = @JoinColumn(name = "blocked_user_id")
    )
    private Set<User> blacklist;


    @ManyToMany(mappedBy = "blacklist")
    private Set<User> blockedBy;

}
