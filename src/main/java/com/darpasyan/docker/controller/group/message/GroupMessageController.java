package com.darpasyan.docker.controller.group.message;


import com.darpasyan.docker.model.group.message.GroupMessage;
import com.darpasyan.docker.model.group.message.dto.GroupMessageRequestDto;
import com.darpasyan.docker.model.group.message.dto.GroupMessageResponseDto;
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
    public List<GroupMessageResponseDto> getGroupMessages( @PathVariable int groupId){
        return groupMessageService.getGroupMessagesByGroup(groupId);
    }

    @PostMapping("groups/{groupId}/createMessage")
    public GroupMessageResponseDto createGroupMessage(@PathVariable int groupId, @RequestBody GroupMessageRequestDto fromDto){
        return groupMessageService.createGroupMessage(groupId, fromDto);
    }

    @PutMapping("editGroupMessage/{id}")
    public GroupMessageResponseDto editGroupMessage(@PathVariable int id, @RequestBody GroupMessageRequestDto fromDto){
        return groupMessageService.editGroupMessage(id, fromDto);
    }


    @DeleteMapping("deleteGroupMessage/{id}")
    public void deleteMGroupMessage(@PathVariable int id){
        groupMessageService.deleteGroupMessage(id);
    }

    @GetMapping("groups/{groupId}/{username}/messages/")
    public List<GroupMessageResponseDto> getGroupMessagesByUser(@PathVariable int groupId, @PathVariable String username){

        return groupMessageService.getGroupMessagesByUser(groupId, username);
    }

}
