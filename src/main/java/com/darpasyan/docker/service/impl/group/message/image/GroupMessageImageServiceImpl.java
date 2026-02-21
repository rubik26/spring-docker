package com.darpasyan.docker.service.impl.group.message.image;

import com.darpasyan.docker.model.group.message.GroupMessage;
import com.darpasyan.docker.model.group.message.image.GroupMessageImage;
import com.darpasyan.docker.model.group.message.image.dto.GroupMessageImageRequestDto;
import com.darpasyan.docker.model.group.message.image.dto.GroupMessageImageResponseDto;
import com.darpasyan.docker.model.user.UserPrincipial;
import com.darpasyan.docker.repo.group.message.GroupMessageRepo;
import com.darpasyan.docker.repo.group.message.image.GroupMessageImageRepo;
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



    private GroupMessageImageResponseDto toDto(GroupMessageImage groupMessageImage){
        return new GroupMessageImageResponseDto(
                groupMessageImage.getId(),
                groupMessageImage.getImageData(),
                groupMessageImage.getMessage().getId()
        );
    }

    private GroupMessageImage getAccess(int id){
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        GroupMessageImage groupMessageImage = groupMessageImageRepo.findById(id).orElseThrow(() ->
                new RuntimeException("Image not found"));

        UserPrincipial currentUser = (UserPrincipial) authentication.getPrincipal();

        if(currentUser.getId() != groupMessageImage.getMessage().getUser().getId()) {
            throw new RuntimeException("Access denied");
        }

        return groupMessageImage;
    }


    @Override
    public List<GroupMessageImageResponseDto> getImages() {
        return groupMessageImageRepo.findAll().
                stream().
                map(this::toDto).
                toList();
    }

    @Override
    public GroupMessageImageResponseDto createImage(int messageId, GroupMessageImageRequestDto fromDto) {

        GroupMessage groupMessage = groupMessageRepo.findById(messageId).orElseThrow(() ->
                new RuntimeException("Message not found"));

        GroupMessageImage groupMessageImage = new GroupMessageImage();
        groupMessageImage.setImageName(fromDto.getImageName());
        groupMessageImage.setImageData(fromDto.getImageData());
        groupMessageImage.setImageType(fromDto.getImageType());
        groupMessageImage.setMessage(groupMessage);


        groupMessageImageRepo.save(groupMessageImage);

        return toDto(groupMessageImage);
    }

    @Override
    public GroupMessageImageResponseDto editImage(int id, GroupMessageImageRequestDto fromDto) {
        GroupMessageImage groupMessageImage = getAccess(id);

        groupMessageImage.setImageName(fromDto.getImageName());
        groupMessageImage.setImageData(fromDto.getImageData());
        groupMessageImage.setImageType(fromDto.getImageType());


        groupMessageImageRepo.save(groupMessageImage);

        return toDto(groupMessageImage);
    }

    @Override
    public void deleteImage(int id) {
       getAccess(id);

       groupMessageImageRepo.deleteById(id);
    }
}
