package com.darpasyan.docker.builders.groupMessage.director;

import com.darpasyan.docker.builders.groupMessage.GroupMessageBuilder;
import com.darpasyan.docker.model.group.Group;
import com.darpasyan.docker.model.user.User;

import java.time.LocalDateTime;

@Deprecated
public class GroupMessageDirector {
    public void DefaultMessage(GroupMessageBuilder groupMessageBuilder){
        groupMessageBuilder.setId(1);
        groupMessageBuilder.setContent("Default content");
        groupMessageBuilder.setDateOfSend(LocalDateTime.now());
        groupMessageBuilder.setIsEdited(false);
        groupMessageBuilder.setSender(new User());
        groupMessageBuilder.setGroup(new Group());
    }
}
