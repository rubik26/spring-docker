package com.darpasyan.docker.controller.direct.message;

import com.darpasyan.docker.model.direct.message.dto.DirectMessageRequestDto;
import com.darpasyan.docker.model.direct.message.dto.DirectMessageResponseDto;
import com.darpasyan.docker.service.direct.message.DirectMessageService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@AllArgsConstructor
@RequestMapping("api/")
public class DirectMessageController {

    private final DirectMessageService directMessageService;

    @GetMapping("directs/{directId}/messages")
    public List<DirectMessageResponseDto> getDirectMessagesByDirect(@PathVariable int directId){
        return directMessageService.getMessagesByDirect(directId);
    }

    @GetMapping("directs/{directId}/{username}/messages")
    public List<DirectMessageResponseDto> getDirectMessageByUsername(@PathVariable int directId, @PathVariable String username){

        return directMessageService.getDirectMessagesByUsername(directId, username);
    }

    @GetMapping("directs/{directId}/message/{id}")
    public DirectMessageResponseDto getDirectMessageById(@PathVariable int directId, @PathVariable int id){
        return directMessageService.getDirectMessageById(directId, id);

    }

    @PostMapping("directs/{directId}/createDirectMessage")
    public DirectMessageResponseDto createDirectMessage(@RequestBody DirectMessageRequestDto directMessageRequestDto, @PathVariable int directId){
        return directMessageService.createDirectMessage(directMessageRequestDto, directId);
    }

    @PutMapping("editDirectMessage/{id}")
    public DirectMessageResponseDto editDirectMessage(@RequestBody DirectMessageRequestDto directMessageRequestDto, @PathVariable int id){
        return directMessageService.editDirectMessage(directMessageRequestDto, id);
    }

    @DeleteMapping("deleteDirectMessage/{id}")
    public void deleteDirectMessage(@PathVariable int id){
        directMessageService.deleteDirectMessage(id);
    }
}
