package com.darpasyan.docker.service.impl.direct.message.image;

import com.darpasyan.docker.model.user.UserPrincipial;
import com.darpasyan.docker.model.direct.message.DirectMessage;
import com.darpasyan.docker.model.direct.message.image.DirectMessageImage;
import com.darpasyan.docker.model.direct.message.image.dto.DirectMessageImageRequestDto;
import com.darpasyan.docker.model.direct.message.image.dto.DirectMessageImageResponseDto;
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




    private DirectMessageImageResponseDto toDto(DirectMessageImage directMessageImage){
        return new DirectMessageImageResponseDto(
                directMessageImage.getId(),
                directMessageImage.getImageData(),
                directMessageImage.getMessage().getId()
        );
    }

    private DirectMessageImage accessTest(int id){
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
    public List<DirectMessageImageResponseDto> getImages() {
        return directMessageImageRepo.findAll().
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
        DirectMessageImage directMessageImage = accessTest(id);

        directMessageImage.setImageName(fromDto.getImageName());
        directMessageImage.setImageData(fromDto.getImageData());
        directMessageImage.setImageType(fromDto.getImageType());


        directMessageImageRepo.save(directMessageImage);
        return toDto(directMessageImage);
    }

    @Override
    public void deleteImage(int id) {
        accessTest(id);

        directMessageImageRepo.deleteById(id);
    }
}
