package com.darpasyan.docker.model.group.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupResponseDto {
    private int id;
    private String name;
    private String description;
    private byte[] avatar;
    private LocalDate dateOfCreate;
    private int adminId;
    private Set<Integer> moderatorsId;
    private Set<Integer> participantsId;
}
