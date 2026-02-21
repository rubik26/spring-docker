package com.darpasyan.docker.service.impl.direct.message.image;

import com.darpasyan.docker.model.direct.Direct;
import com.darpasyan.docker.model.direct.message.DirectMessage;
import com.darpasyan.docker.model.direct.message.image.DirectMessageImage;
import com.darpasyan.docker.model.direct.message.image.dto.DirectMessageImageRequestDto;
import com.darpasyan.docker.model.direct.message.image.dto.DirectMessageImageResponseDto;
import com.darpasyan.docker.model.user.User;
import com.darpasyan.docker.model.user.UserPrincipial;
import com.darpasyan.docker.repo.UserRepo;
import com.darpasyan.docker.repo.direct.DirectRepo;
import com.darpasyan.docker.repo.direct.message.DirectMessageRepo;
import com.darpasyan.docker.repo.direct.message.image.DirectMessageImageRepo;
import com.darpasyan.docker.service.direct.message.image.DirectMessageImageService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@AllArgsConstructor
public class DirectIMessageImageServiceImpl implements DirectMessageImageService {
    private final DirectMessageImageRepo directMessageImageRepo;


    public final DirectMessageRepo directMessageRepo;

    public final UserRepo userRepo;

    public final DirectRepo directRepo;


    private DirectMessageImageResponseDto toDto(DirectMessageImage directMessageImage){
        return new DirectMessageImageResponseDto(
                directMessageImage.getId(),
                directMessageImage.getImageData(),
                directMessageImage.getMessage().getId()
        );
    }

    private void getAccessTest(int directId){
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        UserPrincipial currentUser =
                (UserPrincipial) authentication.getPrincipal();

        User user = userRepo.findById(currentUser.getId()).orElseThrow(
                () -> new RuntimeException("User not found")
        );

        Direct direct = directRepo.findById(directId).orElseThrow(
                () -> new RuntimeException("Direct not found")
        );

        if(direct.getSender().getId() != user.getId() && direct.getRecipient().getId() != user.getId()){
            throw new RuntimeException("Access denied");
        }
    }

    private DirectMessageImage editAndDeleteAccessTest(int id){
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        DirectMessageImage directMessageImage = directMessageImageRepo.findById(id).orElseThrow(() ->
                new RuntimeException("Image not found"));

        UserPrincipial currentUser = (UserPrincipial) authentication.getPrincipal();

        if(currentUser.getId() != directMessageImage.getMessage().getUser().getId()) {
            throw new RuntimeException("Access denied");
        }

        return directMessageImage;
    }

    @Override
    public List<DirectMessageImageResponseDto> getImages(int directId) {
        getAccessTest(directId);

        return directMessageImageRepo.findDirectMessageImagesByMessage_Direct_Id(directId).
                stream().
                map(this::toDto).
                toList();
    }

    @Override
    public DirectMessageImageResponseDto createImage(int messageId, DirectMessageImageRequestDto fromDto) throws IOException {

        DirectMessage directMessage = directMessageRepo.findById(messageId).orElseThrow(() ->
                new RuntimeException("Message not found"));

        DirectMessageImage directMessageImage = new DirectMessageImage();
        directMessageImage.setImageName(fromDto.getImageName());
        directMessageImage.setImageData(fromDto.getImageData());
        directMessageImage.setImageType(fromDto.getImageType());
        directMessageImage.setMessage(directMessage);

        directMessageImageRepo.save(directMessageImage);

        return toDto(directMessageImage);
    }

    @Override
    public DirectMessageImageResponseDto editImage(int id, DirectMessageImageRequestDto fromDto) throws IOException {
        DirectMessageImage directMessageImage = editAndDeleteAccessTest(id);

        directMessageImage.setImageName(fromDto.getImageName());
        directMessageImage.setImageData(fromDto.getImageData());
        directMessageImage.setImageType(fromDto.getImageType());


        directMessageImageRepo.save(directMessageImage);
        return toDto(directMessageImage);
    }

    @Override
    public void deleteImage(int id) {
        editAndDeleteAccessTest(id);

        directMessageImageRepo.deleteById(id);
    }

    @Override
    public DirectMessageImageResponseDto getImageById(int directId, int id) {
        getAccessTest(directId);

        DirectMessageImage image = directMessageImageRepo.findById(id).orElseThrow(
                () -> new RuntimeException("Image not found")
        );

        return toDto(image);
    }
}
