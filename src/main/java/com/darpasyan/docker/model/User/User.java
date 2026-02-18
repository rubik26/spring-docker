package com.darpasyan.docker.model.User;


import com.darpasyan.docker.model.group.Group;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;


@Entity
@Table(name = "users")


@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id"
)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String username;

    private String password;


    @ManyToMany(mappedBy = "participants")
    private Set<Group> groups;



    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_blacklist",
            joinColumns = @JoinColumn(name = "user_id"),

            inverseJoinColumns = @JoinColumn(name = "blocked_user_id")
    )
    private Set<User> blacklist;


    @ManyToMany(mappedBy = "blacklist")
    private Set<User> blockedBy;


}
