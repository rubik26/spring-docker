package com.darpasyan.docker.builders.group.impl;

import com.darpasyan.docker.builders.group.GroupBuilder;
import com.darpasyan.docker.model.group.Group;
import com.darpasyan.docker.model.user.User;

import java.time.LocalDate;
import java.util.Set;

public class GroupBuilderImpl implements GroupBuilder {
    private int id;
    private String name;
    private String description;
    private byte[] avatar;
    private String avatarFileName;
    private String avatarFileType;
    private LocalDate dateOfCreate;
    private User admin;
    private Set<User> moderators;
    private Set<User> participants;

    @Override
    public GroupBuilderImpl setId(int id) {
        this.id = id;
        return this;
    }

    @Override
    public GroupBuilderImpl setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public GroupBuilderImpl setDescription(String description) {
        this.description = description;
        return this;
    }

    @Override
    public GroupBuilderImpl setAvatar(byte[] avatar) {
        this.avatar = avatar;
        return this;
    }

    @Override
    public GroupBuilderImpl setAvatarFileName(String avatarFileName) {
        this.avatarFileName = avatarFileName;
        return this;
    }

    @Override
    public GroupBuilderImpl setAvatarFileType(String avatarFileType) {
        this.avatarFileType = avatarFileType;
        return this;
    }

    @Override
    public GroupBuilderImpl setDateOfCreate(LocalDate dateOfCreate) {
        this.dateOfCreate = dateOfCreate;
        return this;
    }

    @Override
    public GroupBuilderImpl setAdmin(User admin) {
        this.admin = admin;
        return this;
    }

    @Override
    public GroupBuilderImpl setModerators(Set<User> moderators) {
        this.moderators = moderators;
        return this;
    }

    @Override
    public GroupBuilderImpl setParticipants(Set<User> participants) {
        this.participants = participants;
        return this;
    }

    public Group build(){
        id++;

        if(moderators == null){
            moderators = Set.of();
        }

        if(participants == null){
            participants = Set.of();
        }

        return new Group(id, name, description, avatar, avatarFileName, avatarFileType, dateOfCreate, admin, moderators, participants);
    }
}
