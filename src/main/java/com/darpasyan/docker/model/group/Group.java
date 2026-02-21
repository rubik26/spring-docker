package com.darpasyan.docker.model.group;

import com.darpasyan.docker.model.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "groups")
@Getter
@Setter
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
    private Set<User> moderators;

    @ManyToMany
    @JoinTable(
            name = "group_participants",
            joinColumns = @JoinColumn(name = "group_id"), // ключ этой сущности
            inverseJoinColumns = @JoinColumn(name = "user_id") // ключ другой сущности
    )
    private Set<User> participants;






    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Group group = (Group) o;
        return id == group.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }


    @Override
    public String toString() {
        return "Group{" +
                "id=" + id +
                '}';
    }
}
