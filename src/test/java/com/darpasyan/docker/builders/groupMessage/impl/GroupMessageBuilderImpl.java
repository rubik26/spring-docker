package com.darpasyan.docker.builders.groupMessage.impl;

import com.darpasyan.docker.builders.groupMessage.GroupMessageBuilder;
import com.darpasyan.docker.model.group.Group;
import com.darpasyan.docker.model.group.message.GroupMessage;
import com.darpasyan.docker.model.user.User;

import java.time.LocalDateTime;

public class GroupMessageBuilderImpl implements GroupMessageBuilder {
    private int id;
    private  String content;
    private LocalDateTime dateOfSend;
    private boolean isEdited;
    private User sender;
    private Group group;

    @Override
    public GroupMessageBuilderImpl setId(int id) {
        this.id = id;
        return this;
    }

    @Override
    public GroupMessageBuilderImpl setContent(String content) {
        this.content = content;
        return this;
    }

    @Override
    public GroupMessageBuilderImpl setDateOfSend(LocalDateTime dateOfSend) {
        this.dateOfSend = dateOfSend;
        return this;
    }

    @Override
    public GroupMessageBuilderImpl setIsEdited(boolean isEdited) {
        this.isEdited = isEdited;
        return this;
    }

    @Override
    public GroupMessageBuilderImpl setSender(User sender) {
        this.sender = sender;
        return this;
    }

    @Override
    public GroupMessageBuilderImpl setGroup(Group group) {
        this.group = group;
        return this;
    }

    public GroupMessage build(){
        id++;
        return new GroupMessage(id, content, dateOfSend, isEdited, sender, group);
    }
}
