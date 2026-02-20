package com.darpasyan.docker.model.direct.message.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DirectMessageRequestDto {
    private int directId;
    private String content;
}
