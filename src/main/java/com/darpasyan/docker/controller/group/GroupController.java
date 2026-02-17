package com.darpasyan.docker.controller.group;

import com.darpasyan.docker.model.group.Group;
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
    public List<Group> getGroups(){
        return groupService.getGroups();
    }

    @PostMapping("createGroup")
    public Group createGroup(@RequestBody Group group){
        return groupService.createGroup(group);
    }

    @PutMapping("updateGroup/{id}")
    public Group updateGroup(@PathVariable int id, @RequestBody Group group){
        return groupService.updateGroup(id, group);
    }

    @DeleteMapping("deleteGroup/{id}")
    public void deleteGroup(@PathVariable int id){
        groupService.deleteGroup(id);
    }


    @GetMapping("groups/{name}")
    public List<Group> getGroupsByName(@PathVariable String name){
        return groupService.findGroupByName(name);
    }
}
