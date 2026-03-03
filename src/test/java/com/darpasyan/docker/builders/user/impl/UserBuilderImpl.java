package com.darpasyan.docker.builders.user.impl;

import com.darpasyan.docker.builders.user.UserBuilder;
import com.darpasyan.docker.model.group.Group;
import com.darpasyan.docker.model.user.User;

import java.util.Set;

public class UserBuilderImpl implements UserBuilder {
    private int id;
    private String username;
    private String password;
    private Set<Group> groups;
    private Set<User> blacklist;
    private Set<User> blockedBy;

    @Override
    public UserBuilderImpl setId(int id) {
        this.id = id;
        return this;
    }

    @Override
    public UserBuilderImpl setUsername(String username) {
        this.username = username;
        return this;
    }

    @Override
    public UserBuilderImpl setPassword(String password) {
        this.password = password;
        return this;
    }

    @Override
    public UserBuilderImpl setGroups(Set<Group> groups) {
        this.groups = groups;
        return this;
    }

    @Override
    public UserBuilderImpl blackList(Set<User> blacklist) {
        this.blacklist = blacklist;
        return this;
    }

    @Override
    public UserBuilderImpl blockedBy(Set<User> blockedBy) {
        this.blockedBy = blockedBy;
        return this;
    }

    public User build(){
        id++;

        if(blacklist == null){
            blacklist = Set.of();
        }

        if (blockedBy == null){
            blockedBy = Set.of();
        }

        if(groups == null){
            groups = Set.of();
        }

        return new User(id, username, password, groups, blacklist, blockedBy);
    }
}
