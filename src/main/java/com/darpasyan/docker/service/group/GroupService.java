package com.darpasyan.docker.service.group;

import com.darpasyan.docker.model.group.Group;
import com.darpasyan.docker.model.group.dto.GroupRequestDto;
import com.darpasyan.docker.model.group.dto.GroupResponseDto;

import java.util.List;

public interface GroupService {

    List<GroupResponseDto> getGroups();

    GroupResponseDto createGroup(GroupRequestDto fromDto);

    GroupResponseDto updateGroup(int id, GroupRequestDto fromDto);

    void deleteGroup(int id);


    List<GroupResponseDto> findGroupByName(String name);
}
