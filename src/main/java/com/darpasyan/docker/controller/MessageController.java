package com.darpasyan.docker.controller;


import com.darpasyan.docker.model.Message;
import com.darpasyan.docker.service.MessageService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/")
@AllArgsConstructor
public class MessageController {

    private final MessageService messageService;



    @GetMapping("messages")
    public List<Message> getMessages(){
        return messageService.getMessages();
    }

    @PostMapping("createMessage")
    public Message createMessage(@RequestBody Message message){
        return messageService.createMessage(message);
    }

    @PutMapping("editMessage/{id}")
    public Message editMessage(@PathVariable int id, @RequestBody Message message){
        return messageService.editMessage(id, message);
    }


    @DeleteMapping("deleteMessage/{id}")
    public void deleteMessage(@PathVariable int id){
        messageService.deleteMessage(id);
    }

    @GetMapping("messages/{username}")
    public List<Message> getMessagesByUser(@PathVariable String username){
        return messageService.getMessagesByUser(username);
    }

}
