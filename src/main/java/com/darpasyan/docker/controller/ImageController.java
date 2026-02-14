package com.darpasyan.docker.controller;

import com.darpasyan.docker.model.Image;
import com.darpasyan.docker.service.ImageService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@CrossOrigin
@AllArgsConstructor
@RequestMapping("api/")
public class ImageController {

    private final ImageService imageService;


    @GetMapping("/images")
    public List<Image> getImages(){
        return imageService.getImages();
    }


    @PostMapping("uploadImage/{messageId}")
    public Image createImage(@PathVariable int messageId, @RequestBody MultipartFile imageFile) throws IOException {

        return imageService.createImage(messageId, imageFile);
    }


    @PutMapping("editImage/{id}")
    public Image editImage(@PathVariable int id, @RequestBody MultipartFile imageFile) throws IOException {
        return imageService.editImage(id, imageFile);
    }


    @DeleteMapping("deleteImage/{id}")
    public void deleteImage(@PathVariable int id){
        imageService.deleteImage(id);
    }
}
