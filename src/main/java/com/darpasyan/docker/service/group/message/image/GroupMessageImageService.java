package com.darpasyan.docker.service.group.message.image;

import com.darpasyan.docker.model.group.message.image.dto.GroupMessageImageRequestDto;
import com.darpasyan.docker.model.group.message.image.dto.GroupMessageImageResponseDto;

import java.io.IOException;
import java.util.List;

public interface GroupMessageImageService {

    List<GroupMessageImageResponseDto> getImages(int groupId);
    GroupMessageImageResponseDto createImage(int messageId, GroupMessageImageRequestDto fromDto) throws IOException;
    GroupMessageImageResponseDto editImage(int id, GroupMessageImageRequestDto fromDto) throws IOException;
    void deleteImage(int id);

    GroupMessageImageResponseDto getImageById(int groupId, int id);


}
