package com.darpasyan.docker.model.group.message.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupMessageResponseDto {
    private int id;
    private String content;
    private LocalDateTime dateOfSend;
    private boolean isEdited;
    private int senderId;
    private int groupId;

}
