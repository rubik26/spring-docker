package com.darpasyan.docker.controller.group;

import com.darpasyan.docker.model.group.dto.GroupRequestDto;
import com.darpasyan.docker.model.group.dto.GroupResponseDto;
import com.darpasyan.docker.service.group.GroupService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@CrossOrigin
@RequestMapping("api/")
public class GroupController {

    private final GroupService groupService;


    @GetMapping("groups")
    public List<GroupResponseDto> getGroups(){
        return groupService.getGroups();
    }

    @PostMapping("createGroup")
    public GroupResponseDto createGroup(@RequestBody GroupRequestDto fromDto){
        return groupService.createGroup(fromDto);
    }

    @PutMapping("updateGroup/{id}")
    public GroupResponseDto updateGroup(@PathVariable int id, @RequestBody GroupRequestDto fromDto){
        return groupService.updateGroup(id, fromDto);
    }

    @DeleteMapping("deleteGroup/{id}")
    public void deleteGroup(@PathVariable int id){
        groupService.deleteGroup(id);
    }


    @GetMapping("groups/{name}")
    public List<GroupResponseDto> getGroupsByName(@PathVariable String name){
        return groupService.findGroupByName(name);
    }

    @GetMapping("group/{id}")
    public GroupResponseDto getGroupById(@PathVariable int id){
        return groupService.getGroupById(id);
    }
}
