package com.darpasyan.docker.controller.group.message;


import com.darpasyan.docker.model.group.message.GroupMessage;
import com.darpasyan.docker.service.group.message.GroupMessageService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/")
@AllArgsConstructor
@CrossOrigin
public class GroupMessageController {

    private final GroupMessageService groupMessageService;



    @GetMapping("groups/{groupId}/messages")
    public List<GroupMessage> getGroupMessages( @PathVariable int groupId){
        return groupMessageService.getGroupMessagesByGroup(groupId);
    }

    @PostMapping("groups/{groupId}/createMessage")
    public GroupMessage createGroupMessage(@PathVariable int groupId, @RequestBody GroupMessage message){
        return groupMessageService.createGroupMessage(groupId, message);
    }

    @PutMapping("editGroupMessage/{id}")
    public GroupMessage editGroupMessage(@PathVariable int id, @RequestBody GroupMessage message){
        return groupMessageService.editGroupMessage(id, message);
    }


    @DeleteMapping("deleteGroupMessage/{id}")
    public void deleteMGroupMessage(@PathVariable int id){
        groupMessageService.deleteGroupMessage(id);
    }

    @GetMapping("groups/{groupId}/{username}/messages/")
    public List<GroupMessage> getGroupMessagesByUser(@PathVariable int groupId, @PathVariable String username){

        return groupMessageService.getGroupMessagesByUser(groupId, username);
    }

}
