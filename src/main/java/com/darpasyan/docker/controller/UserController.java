package com.darpasyan.docker.controller;


import com.darpasyan.docker.model.user.dto.UserRequestDto;
import com.darpasyan.docker.model.user.dto.UserResponseDto;
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


    @GetMapping("users/{id}")
    public UserResponseDto getUserById(@PathVariable int id){
        return service.getUserById(id);
    }

    @PostMapping("createUser")
    public UserResponseDto addUser(@RequestBody UserRequestDto fromDto){
        return service.createUser(fromDto);
    }

    @PutMapping("updateUser/{id}")
    public UserResponseDto updateUser(@PathVariable int id, @RequestBody UserRequestDto fromDto){
        return service.updateUser(id, fromDto);
    }

    @DeleteMapping("deleteUser/{id}")
    public void deleteUser(@PathVariable int id){
        service.deleteUser(id);
    }

}
