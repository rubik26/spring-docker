package com.darpasyan.docker.builders.user;

import com.darpasyan.docker.model.group.Group;
import com.darpasyan.docker.model.user.User;

import java.util.Set;

public interface UserBuilder {
    UserBuilder setId(int id);
    UserBuilder setUsername(String username);
    UserBuilder setPassword(String password);
    UserBuilder setGroups(Set<Group> groups);
    UserBuilder blackList(Set<User> blacklist);
    UserBuilder blockedBy(Set<User> blockedBy);
}
