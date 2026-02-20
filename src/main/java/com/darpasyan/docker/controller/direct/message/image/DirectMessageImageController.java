package com.darpasyan.docker.controller.direct.message.image;

import com.darpasyan.docker.model.direct.message.image.DirectMessageImage;
import com.darpasyan.docker.model.direct.message.image.dto.DirectMessageImageRequestDto;
import com.darpasyan.docker.model.direct.message.image.dto.DirectMessageImageResponseDto;
import com.darpasyan.docker.service.direct.message.image.DirectMessageImageService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@CrossOrigin
@AllArgsConstructor
@RequestMapping("api/")
public class DirectMessageImageController {

    private final DirectMessageImageService directMessageImageService;


    @GetMapping("directImages")
    public List<DirectMessageImageResponseDto> getImages(){
        return directMessageImageService.getImages();
    }


    @PostMapping("uploadDirectImage/{messageId}")
    public DirectMessageImageResponseDto createImage(@PathVariable int messageId, @RequestBody MultipartFile imageFile) throws IOException {
        DirectMessageImageRequestDto toDto = new DirectMessageImageRequestDto();

        toDto.setImageName(imageFile.getOriginalFilename());
        toDto.setImageData(imageFile.getBytes());
        toDto.setImageType(imageFile.getContentType());
        toDto.setMessageId(messageId);

        return directMessageImageService.createImage(messageId, toDto);
    }


    @PutMapping("editDirectImage/{id}")
    public DirectMessageImageResponseDto editImage(@PathVariable int id, @RequestBody MultipartFile imageFile) throws IOException {
        DirectMessageImageRequestDto toDto = new DirectMessageImageRequestDto();

        toDto.setImageName(imageFile.getOriginalFilename());
        toDto.setImageData(imageFile.getBytes());
        toDto.setImageType(imageFile.getContentType());
        return directMessageImageService.editImage(id, toDto);
    }


    @DeleteMapping("deleteDirectImage/{id}")
    public void deleteImage(@PathVariable int id){
        directMessageImageService.deleteImage(id);
    }
}
