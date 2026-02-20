package com.darpasyan.docker.model.direct.message.image.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DirectMessageImageResponseDto {
    private int id;
    private byte[] imageData;
    private int messageId;
}
