package com.darpasyan.docker.service.impl;

import com.darpasyan.docker.model.Image;
import com.darpasyan.docker.model.Message;
import com.darpasyan.docker.model.UserPrincipial;
import com.darpasyan.docker.repo.ImageRepo;
import com.darpasyan.docker.repo.MessageRepo;
import com.darpasyan.docker.service.ImageService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@AllArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final ImageRepo imageRepo;


    public final MessageRepo messageRepo;


    @Override
    public List<Image> getImages() {
        return imageRepo.findAll();
    }

    @Override
    public Image createImage(int messageId, MultipartFile imageFile) throws IOException {

        Message message = messageRepo.findById(messageId).orElseThrow(() ->
                new RuntimeException("Message not found"));

        Image image = new Image();
        image.setImageName(imageFile.getOriginalFilename());
        image.setImageData(imageFile.getBytes());
        image.setImageType(imageFile.getContentType());
        image.setMessage(message);


        System.out.println("File size: " + imageFile.getSize());
        System.out.println("Bytes length: " + imageFile.getBytes().length);
        return imageRepo.save(image);
    }

    @Override
    public Image editImage(int id, MultipartFile imageFile) throws IOException {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        Image image = imageRepo.findById(id).orElseThrow(() ->
                new RuntimeException("Image not found"));

        UserPrincipial currentUser = (UserPrincipial) authentication.getPrincipal();

        if(currentUser.getId() != image.getMessage().getUser().getId()) {
            throw new RuntimeException("Access denied");
        }

        image.setImageName(imageFile.getOriginalFilename());
        image.setImageData(imageFile.getBytes());
        image.setImageType(imageFile.getContentType());


        return imageRepo.save(image);
    }

    @Override
    public void deleteImage(int id) {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        Image image = imageRepo.findById(id).orElseThrow(() ->
                new RuntimeException("Image not found"));

        UserPrincipial currentUser = (UserPrincipial) authentication.getPrincipal();

        if(currentUser.getId() != image.getMessage().getUser().getId()) {
            throw new RuntimeException("Access denied");
        }

        imageRepo.deleteById(id);
    }
}
