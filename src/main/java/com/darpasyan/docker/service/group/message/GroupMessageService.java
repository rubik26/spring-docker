package com.darpasyan.docker.service.group.message;

import com.darpasyan.docker.model.group.message.dto.GroupMessageRequestDto;
import com.darpasyan.docker.model.group.message.dto.GroupMessageResponseDto;

import java.util.List;

public interface GroupMessageService {

    List<GroupMessageResponseDto> getGroupMessagesByGroup(int groupId);
    GroupMessageResponseDto createGroupMessage(int groupId, GroupMessageRequestDto fromDto);
    GroupMessageResponseDto editGroupMessage(int id, GroupMessageRequestDto fromDto);
    void deleteGroupMessage(int id);

    List<GroupMessageResponseDto> getGroupMessagesByUser(int groupId, String username);

    GroupMessageResponseDto getGroupMessageById(int groupId, int messageId);
}
