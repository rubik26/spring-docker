package com.darpasyan.docker.service.group.message.image;

import com.darpasyan.docker.model.group.message.image.GroupMessageImage;
import com.darpasyan.docker.model.group.message.image.dto.GroupMessageImageRequestDto;
import com.darpasyan.docker.model.group.message.image.dto.GroupMessageImageResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface GroupMessageImageService {

    List<GroupMessageImageResponseDto> getImages();
    GroupMessageImageResponseDto createImage(int messageId, GroupMessageImageRequestDto fromDto) throws IOException;
    GroupMessageImageResponseDto editImage(int id, GroupMessageImageRequestDto fromDto) throws IOException;
    void deleteImage(int id);


}
