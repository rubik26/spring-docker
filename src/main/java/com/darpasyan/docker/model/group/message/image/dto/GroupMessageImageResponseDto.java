package com.darpasyan.docker.model.group.message.image.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupMessageImageResponseDto {
    private int id;
    private byte[] imageData;
    private int messageId;
}
