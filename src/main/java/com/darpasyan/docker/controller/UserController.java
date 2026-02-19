package com.darpasyan.docker.controller;


import com.darpasyan.docker.model.User.User;
import com.darpasyan.docker.model.User.dto.UserRequestDto;
import com.darpasyan.docker.model.User.dto.UserResponseDto;
import com.darpasyan.docker.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/")
@AllArgsConstructor
@CrossOrigin
public class UserController {

    private final UserService service;



    @GetMapping("/me")
    public UserResponseDto getMe(){
        return service.getMe();
    }


    @GetMapping("users")
    public List<UserResponseDto> getUsers(){
        return service.getUsers();
    }

    @PostMapping("createUser")
    public UserRequestDto addUser(@RequestBody User user){
        return service.createUser(user);
    }

    @PutMapping("updateUser/{id}")
    public UserRequestDto updateUser(@PathVariable int id, @RequestBody User user){
        return service.updateUser(id, user);
    }

    @DeleteMapping("deleteUser/{id}")
    public void deleteUser(@PathVariable int id){
        service.deleteUser(id);
    }

}
