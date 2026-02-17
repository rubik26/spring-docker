package com.darpasyan.docker.service.group;

import com.darpasyan.docker.model.group.Group;

import java.util.List;

public interface GroupService {

    List<Group> getGroups();

    Group createGroup(Group group);

    Group updateGroup(int id, Group group);

    void deleteGroup(int id);


    List<Group> findGroupByName(String name);
}
