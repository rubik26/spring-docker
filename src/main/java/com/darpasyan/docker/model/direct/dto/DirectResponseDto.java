package com.darpasyan.docker.model.direct.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DirectResponseDto {
    private int id;

    private int senderId;
    private String senderUsername;

    private int recipientId;
    private String recipientUsername;
}
