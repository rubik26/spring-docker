package com.darpasyan.docker.controller;


import com.darpasyan.docker.model.User;
import com.darpasyan.docker.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/")
public class UserController {

    private final UserService service;

    UserController(UserService service){
        this.service = service;
    }

    @GetMapping("users")
    public List<User> getUsers(){
        return service.getUsers();
    }

    @PostMapping("createUser")
    public User addUser(@RequestBody User user){
        return service.createUser(user);
    }

    @PutMapping("updateUser/{id}")
    public User updateUser(@PathVariable int id, @RequestBody User user){
        return service.updateUser(id, user);
    }

    @DeleteMapping("deleteUser/{id}")
    public void deleteUser(@PathVariable int id){
        service.deleteUser(id);
    }

}
