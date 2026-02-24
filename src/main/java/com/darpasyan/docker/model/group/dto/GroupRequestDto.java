package com.darpasyan.docker.model.group.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupRequestDto {
    private String name;
    private String description;
    private byte[] avatar;
    private String avatarFileName;
    private String avatarFileType;
    private Set<Integer> moderatorsId;
    private Set<Integer> participantsId;
}
