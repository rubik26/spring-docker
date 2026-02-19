package com.darpasyan.docker.model.direct.message.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class DirectMessageResponseDto {
    private int id;
    private String content;
    private LocalDateTime dateOfSend;
    private boolean isEdited;
    private boolean isWatched;
    private int  senderId;
    private String senderUsername;

}
