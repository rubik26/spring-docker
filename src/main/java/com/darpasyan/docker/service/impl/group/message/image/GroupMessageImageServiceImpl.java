package com.darpasyan.docker.service.impl.group.message.image;

import com.darpasyan.docker.model.group.message.image.GroupMessageImage;
import com.darpasyan.docker.model.group.message.GroupMessage;
import com.darpasyan.docker.model.User.UserPrincipial;
import com.darpasyan.docker.repo.group.message.image.GroupMessageImageRepo;
import com.darpasyan.docker.repo.group.message.GroupMessageRepo;
import com.darpasyan.docker.service.group.message.image.GroupMessageImageService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@AllArgsConstructor
public class GroupMessageImageServiceImpl implements GroupMessageImageService {

    private final GroupMessageImageRepo groupMessageImageRepo;


    public final GroupMessageRepo groupMessageRepo;


    @Override
    public List<GroupMessageImage> getImages() {
        return groupMessageImageRepo.findAll();
    }

    @Override
    public GroupMessageImage createImage(int messageId, MultipartFile imageFile) throws IOException {

        GroupMessage groupMessage = groupMessageRepo.findById(messageId).orElseThrow(() ->
                new RuntimeException("Message not found"));

        GroupMessageImage groupMessageImage = new GroupMessageImage();
        groupMessageImage.setImageName(imageFile.getOriginalFilename());
        groupMessageImage.setImageData(imageFile.getBytes());
        groupMessageImage.setImageType(imageFile.getContentType());
        groupMessageImage.setMessage(groupMessage);


        System.out.println("File size: " + imageFile.getSize());
        System.out.println("Bytes length: " + imageFile.getBytes().length);
        return groupMessageImageRepo.save(groupMessageImage);
    }

    @Override
    public GroupMessageImage editImage(int id, MultipartFile imageFile) throws IOException {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        GroupMessageImage groupMessageImage = groupMessageImageRepo.findById(id).orElseThrow(() ->
                new RuntimeException("Image not found"));

        UserPrincipial currentUser = (UserPrincipial) authentication.getPrincipal();

        if(currentUser.getId() != groupMessageImage.getMessage().getUser().getId()) {
            throw new RuntimeException("Access denied");
        }

        groupMessageImage.setImageName(imageFile.getOriginalFilename());
        groupMessageImage.setImageData(imageFile.getBytes());
        groupMessageImage.setImageType(imageFile.getContentType());


        return groupMessageImageRepo.save(groupMessageImage);
    }

    @Override
    public void deleteImage(int id) {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        GroupMessageImage groupMessageImage = groupMessageImageRepo.findById(id).orElseThrow(() ->
                new RuntimeException("Image not found"));

        UserPrincipial currentUser = (UserPrincipial) authentication.getPrincipal();

        if(currentUser.getId() != groupMessageImage.getMessage().getUser().getId()) {
            throw new RuntimeException("Access denied");
        }

        groupMessageImageRepo.deleteById(id);
    }
}
