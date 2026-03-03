package com.darpasyan.docker.builders.user.director;

import com.darpasyan.docker.builders.user.UserBuilder;
import com.darpasyan.docker.model.group.Group;
import com.darpasyan.docker.model.user.User;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


@Deprecated
public class UserDirector {

    public void DefaultUser(UserBuilder userBuilder){
        userBuilder.setId(1);
        userBuilder.setUsername("Default");
        userBuilder.setPassword("defaultPassword");
        userBuilder.setGroups(Set.of(new Group(
                1, "Test Group", "Test description",
                new byte[1],
                "Test file name",
                "Test file type",
                LocalDate.now(),
                new User(),
                new HashSet<>(),
                new HashSet<>()
            )
        ));
        userBuilder.blackList(Set.of());
        userBuilder.blockedBy(Set.of());

    }
}
