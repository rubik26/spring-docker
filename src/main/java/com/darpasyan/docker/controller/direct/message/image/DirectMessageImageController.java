package com.darpasyan.docker.controller.direct.message.image;

import com.darpasyan.docker.model.direct.message.image.DirectMessageImage;
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
    public List<DirectMessageImage> getImages(){
        return directMessageImageService.getImages();
    }


    @PostMapping("uploadDirectImage/{messageId}")
    public DirectMessageImage createImage(@PathVariable int messageId, @RequestBody MultipartFile imageFile) throws IOException {

        return directMessageImageService.createImage(messageId, imageFile);
    }


    @PutMapping("editDirectImage/{id}")
    public DirectMessageImage editImage(@PathVariable int id, @RequestBody MultipartFile imageFile) throws IOException {
        return directMessageImageService.editImage(id, imageFile);
    }


    @DeleteMapping("deleteDirectImage/{id}")
    public void deleteImage(@PathVariable int id){
        directMessageImageService.deleteImage(id);
    }
}
