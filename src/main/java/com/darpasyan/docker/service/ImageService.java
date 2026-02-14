package com.darpasyan.docker.service;

import com.darpasyan.docker.model.Image;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ImageService {

    List<Image> getImages();
    Image createImage(int messageId, MultipartFile imageFile) throws IOException;
    Image editImage(int id, MultipartFile imageFile) throws IOException;
    void deleteImage(int id);
}
