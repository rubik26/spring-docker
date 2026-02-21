package com.darpasyan.docker.service.direct.message.image;

import com.darpasyan.docker.model.direct.message.image.dto.DirectMessageImageRequestDto;
import com.darpasyan.docker.model.direct.message.image.dto.DirectMessageImageResponseDto;

import java.io.IOException;
import java.util.List;

public interface DirectMessageImageService {
    List<DirectMessageImageResponseDto> getImages(int directId);
    DirectMessageImageResponseDto createImage(int messageId, DirectMessageImageRequestDto fromDto) throws IOException;
    DirectMessageImageResponseDto editImage(int id, DirectMessageImageRequestDto fromDto) throws IOException;
    void deleteImage(int id);

    DirectMessageImageResponseDto getImageById(int directId, int id);
}
