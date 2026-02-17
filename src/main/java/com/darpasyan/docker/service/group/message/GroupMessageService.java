package com.darpasyan.docker.service.group.message;

import com.darpasyan.docker.model.group.message.GroupMessage;

import java.util.List;

public interface GroupMessageService {

    List<GroupMessage> getGroupMessagesByGroup(int groupId);
    GroupMessage createGroupMessage(int groupId, GroupMessage message);
    GroupMessage editGroupMessage(int id, GroupMessage message);
    void deleteGroupMessage(int id);

    List<GroupMessage> getGroupMessagesByUser(int groupId, String username);
}
