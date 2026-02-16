package com.darpasyan.docker.service;

import com.darpasyan.docker.model.Group;
import com.darpasyan.docker.model.Messages.GroupMessage;

import java.util.List;

public interface GroupMessageService {

    List<GroupMessage> getGroupMessagesByGroup(int groupId);
    GroupMessage createGroupMessage(int groupId, GroupMessage message);
    GroupMessage editGroupMessage(int id, GroupMessage message);
    void deleteGroupMessage(int id);

    List<GroupMessage> getGroupMessagesByUser(int groupId, String username);
}
