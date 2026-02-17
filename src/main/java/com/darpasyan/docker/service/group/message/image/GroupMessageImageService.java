package com.darpasyan.docker.service.group.message.image;

import com.darpasyan.docker.model.group.message.image.GroupMessageImage;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface GroupMessageImageService {

    List<GroupMessageImage> getImages();
    GroupMessageImage createImage(int messageId, MultipartFile imageFile) throws IOException;
    GroupMessageImage editImage(int id, MultipartFile imageFile) throws IOException;
    void deleteImage(int id);


}
