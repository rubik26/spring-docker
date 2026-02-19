package com.darpasyan.docker.service.direct.message;

import com.darpasyan.docker.model.direct.message.dto.DirectMessageRequestDto;
import com.darpasyan.docker.model.direct.message.dto.DirectMessageResponseDto;


import java.util.List;

public interface DirectMessageService {
    List<DirectMessageResponseDto> getMessagesByDirect(int directId);

    List<DirectMessageResponseDto> getDirectMessagesByUsername(int directId, String username);
    DirectMessageResponseDto createDirectMessage(DirectMessageRequestDto directMessageRequestDto);
    DirectMessageResponseDto editDirectMessage(DirectMessageRequestDto directMessageRequestDto);
    void deleteDirectMessage(DirectMessageRequestDto directMessageRequestDto);
}
