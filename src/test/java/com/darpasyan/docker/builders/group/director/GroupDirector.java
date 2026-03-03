package com.darpasyan.docker.builders.group.director;

import com.darpasyan.docker.builders.group.GroupBuilder;
import com.darpasyan.docker.model.user.User;

import java.time.LocalDate;
import java.util.Set;

@Deprecated
public class GroupDirector {

    public void DefaultGroup(GroupBuilder groupBuilder){
        groupBuilder.setId(1);
        groupBuilder.setName("Default Group");
        groupBuilder.setDescription("Default description");
        groupBuilder.setAvatar(new byte[1]);
        groupBuilder.setAvatarFileName("Default avatar file name");
        groupBuilder.setAvatarFileType("Default avatar file type");
        groupBuilder.setDateOfCreate(LocalDate.now());
        groupBuilder.setAdmin(new User());
        groupBuilder.setModerators(Set.of());
        groupBuilder.setParticipants(Set.of());
    }
}
