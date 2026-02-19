package com.darpasyan.docker.model.User.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDto {
    private int id;
    private String username;
    private Set<Integer> groupsId;
    private Set<Integer> blacklist;
    private Set<Integer> blockedBy;

}
