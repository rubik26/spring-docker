package com.darpasyan.docker.controller.group.message.image;

import com.darpasyan.docker.model.group.message.image.GroupMessageImage;
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
    public List<GroupMessageImage> getImages(){
        return groupMessageImageService.getImages();
    }


    @PostMapping("uploadGroupImage/{messageId}")
    public GroupMessageImage createImage(@PathVariable int messageId, @RequestBody MultipartFile imageFile) throws IOException {

        return groupMessageImageService.createImage(messageId, imageFile);
    }


    @PutMapping("editGroupImage/{id}")
    public GroupMessageImage editImage(@PathVariable int id, @RequestBody MultipartFile imageFile) throws IOException {
        return groupMessageImageService.editImage(id, imageFile);
    }


    @DeleteMapping("deleteGroupImage/{id}")
    public void deleteImage(@PathVariable int id){
        groupMessageImageService.deleteImage(id);
    }
}
