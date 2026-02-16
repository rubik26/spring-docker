package com.darpasyan.docker.service;

import com.darpasyan.docker.model.Group;

import java.util.List;

public interface GroupService {

    List<Group> getGroups();

    Group createGroup(Group group);

    Group updateGroup(int id, Group group);

    void deleteGroup(int id);


    List<Group> findGroupByName(String name);
}
