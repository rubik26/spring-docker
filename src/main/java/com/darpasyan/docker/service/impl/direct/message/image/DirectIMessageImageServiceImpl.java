package com.darpasyan.docker.service.impl.direct.message.image;

import com.darpasyan.docker.model.User.UserPrincipial;
import com.darpasyan.docker.model.direct.message.DirectMessage;
import com.darpasyan.docker.model.direct.message.image.DirectMessageImage;
import com.darpasyan.docker.repo.direct.message.DirectMessageRepo;
import com.darpasyan.docker.repo.direct.message.image.DirectMessageImageRepo;
import com.darpasyan.docker.service.direct.message.image.DirectMessageImageService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@AllArgsConstructor
public class DirectIMessageImageServiceImpl implements DirectMessageImageService {
    private final DirectMessageImageRepo directMessageImageRepo;


    public final DirectMessageRepo directMessageRepo;


    @Override
    public List<DirectMessageImage> getImages() {
        return directMessageImageRepo.findAll();
    }

    @Override
    public DirectMessageImage createImage(int messageId, MultipartFile imageFile) throws IOException {

        DirectMessage directMessage = directMessageRepo.findById(messageId).orElseThrow(() ->
                new RuntimeException("Message not found"));

        DirectMessageImage directMessageImage = new DirectMessageImage();
        directMessageImage.setImageName(imageFile.getOriginalFilename());
        directMessageImage.setImageData(imageFile.getBytes());
        directMessageImage.setImageType(imageFile.getContentType());
        directMessageImage.setMessage(directMessage);


        System.out.println("File size: " + imageFile.getSize());
        System.out.println("Bytes length: " + imageFile.getBytes().length);
        return directMessageImageRepo.save(directMessageImage);
    }

    @Override
    public DirectMessageImage editImage(int id, MultipartFile imageFile) throws IOException {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        DirectMessageImage directMessageImage = directMessageImageRepo.findById(id).orElseThrow(() ->
                new RuntimeException("Image not found"));

        UserPrincipial currentUser = (UserPrincipial) authentication.getPrincipal();

        if(currentUser.getId() != directMessageImage.getMessage().getUser().getId()) {
            throw new RuntimeException("Access denied");
        }

        directMessageImage.setImageName(imageFile.getOriginalFilename());
        directMessageImage.setImageData(imageFile.getBytes());
        directMessageImage.setImageType(imageFile.getContentType());


        return directMessageImageRepo.save(directMessageImage);
    }

    @Override
    public void deleteImage(int id) {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        DirectMessageImage directMessageImage = directMessageImageRepo.findById(id).orElseThrow(() ->
                new RuntimeException("Image not found"));

        UserPrincipial currentUser = (UserPrincipial) authentication.getPrincipal();

        if(currentUser.getId() != directMessageImage.getMessage().getUser().getId()) {
            throw new RuntimeException("Access denied");
        }

        directMessageImageRepo.deleteById(id);
    }
}
