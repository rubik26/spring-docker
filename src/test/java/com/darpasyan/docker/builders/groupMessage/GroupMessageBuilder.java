package com.darpasyan.docker.builders.groupMessage;

import com.darpasyan.docker.model.group.Group;
import com.darpasyan.docker.model.user.User;

import java.time.LocalDateTime;

public interface GroupMessageBuilder {
    GroupMessageBuilder setId(int id);
    GroupMessageBuilder setContent(String content);
    GroupMessageBuilder setDateOfSend(LocalDateTime dateOfSend);
    GroupMessageBuilder setIsEdited(boolean isEdited);
    GroupMessageBuilder setSender(User sender);
    GroupMessageBuilder setGroup(Group group);
}
