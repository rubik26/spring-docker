package com.darpasyan.docker.builders.group;

import com.darpasyan.docker.model.user.User;

import java.time.LocalDate;
import java.util.Set;

public interface GroupBuilder {
    GroupBuilder setId(int id);
    GroupBuilder setName(String name);
    GroupBuilder setDescription(String description);
    GroupBuilder setAvatar(byte[] avatar);
    GroupBuilder setAvatarFileName(String avatarFileName);
    GroupBuilder setAvatarFileType(String avatarFileType);
    GroupBuilder setDateOfCreate(LocalDate dateOfCreate);
    GroupBuilder setAdmin(User admin);
    GroupBuilder setModerators(Set<User> moderators);
    GroupBuilder setParticipants(Set<User> participants);
}
