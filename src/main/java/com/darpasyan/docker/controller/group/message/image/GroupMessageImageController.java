package com.darpasyan.docker.controller.group.message.image;

import com.darpasyan.docker.model.group.message.image.GroupMessageImage;
import com.darpasyan.docker.model.group.message.image.dto.GroupMessageImageRequestDto;
import com.darpasyan.docker.model.group.message.image.dto.GroupMessageImageResponseDto;
import com.darpasyan.docker.service.group.message.image.GroupMessageImageService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@CrossOrigin
@AllArgsConstructor
@RequestMapping("api/")
public class GroupMessageImageController {

    private final GroupMessageImageService groupMessageImageService;


    @GetMapping("/groupimages")
    public List<GroupMessageImageResponseDto> getImages(){
        return groupMessageImageService.getImages();
    }


    @PostMapping("uploadGroupImage/{messageId}")
    public GroupMessageImageResponseDto createImage(@PathVariable int messageId, @RequestBody GroupMessageImageRequestDto fromDto) throws IOException {
        return groupMessageImageService.createImage(messageId, fromDto);
    }


    @PutMapping("editGroupImage/{id}")
    public GroupMessageImageResponseDto editImage(@PathVariable int id, @RequestBody GroupMessageImageRequestDto fromDto) throws IOException {
        return groupMessageImageService.editImage(id, fromDto);
    }


    @DeleteMapping("deleteGroupImage/{id}")
    public void deleteImage(@PathVariable int id){
        groupMessageImageService.deleteImage(id);
    }
}
