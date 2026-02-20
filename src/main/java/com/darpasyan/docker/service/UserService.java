package com.darpasyan.docker.service;

import com.darpasyan.docker.model.user.User;
import com.darpasyan.docker.model.user.dto.UserRequestDto;
import com.darpasyan.docker.model.user.dto.UserResponseDto;

import java.util.List;

public interface UserService {

    List<UserResponseDto> getUsers();
    UserRequestDto createUser(User user);
    UserRequestDto updateUser(int id, User user);
    void deleteUser(int id);

    UserResponseDto getMe();

}
