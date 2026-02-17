package com.darpasyan.docker.controller.direct.message;

import com.darpasyan.docker.model.direct.message.DirectMessage;
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
    public List<DirectMessage> getDirectMessagesByDirect(@PathVariable int directId){
        return directMessageService.getMessagesByDirect(directId);
    }

    @GetMapping("directs/{directId}/{username}/messages")
    public List<DirectMessage> getDirectMessageByUsername(@PathVariable int directId, @PathVariable String username){
        return directMessageService.getDirectMessagesByUsername(directId, username);
    }

    @PostMapping("directs/{directId}/createMessage")
    public DirectMessage createDirectMessage(@PathVariable int directId, @RequestBody DirectMessage directMessage){
        return directMessageService.createDirectMessage(directId, directMessage);
    }

    @PutMapping("editDirectMessage{id}")
    public DirectMessage editDirectMessage(@PathVariable int id, @RequestBody DirectMessage directMessage){
        return directMessageService.editDirectMessage(id, directMessage);
    }

    @DeleteMapping("deleteDirectMessage/{id}")
    public void deleteDirectMessage(@PathVariable int id){
        directMessageService.deleteDirectMessage(id);
    }
}
