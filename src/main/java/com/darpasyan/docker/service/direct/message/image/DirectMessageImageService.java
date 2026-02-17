package com.darpasyan.docker.service.direct.message.image;

import com.darpasyan.docker.model.direct.message.image.DirectMessageImage;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface DirectMessageImageService {
    List<DirectMessageImage> getImages();
    DirectMessageImage createImage(int messageId, MultipartFile imageFile) throws IOException;
    DirectMessageImage editImage(int id, MultipartFile imageFile) throws IOException;
    void deleteImage(int id);
}
