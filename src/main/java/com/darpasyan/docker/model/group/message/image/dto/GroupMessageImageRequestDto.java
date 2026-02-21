package com.darpasyan.docker.model.group.message.image.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupMessageImageRequestDto {
    private String imageName;
    private byte[] imageData;
    private String imageType;
}
