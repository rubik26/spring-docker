package com.darpasyan.docker.model.direct.message.image.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DirectMessageImageRequestDto {
    private String imageName;
    private byte[] imageData;
    private String imageType;
    private int messageId;
}
