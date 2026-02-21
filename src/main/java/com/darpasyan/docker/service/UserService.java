package com.darpasyan.docker.service;

import com.darpasyan.docker.model.user.dto.UserRequestDto;
import com.darpasyan.docker.model.user.dto.UserResponseDto;

import java.util.List;

public interface UserService {

    List<UserResponseDto> getUsers();
    UserResponseDto createUser(UserRequestDto fromDto);
    UserResponseDto updateUser(int id, UserRequestDto fromDto);
    void deleteUser(int id);

    UserResponseDto getMe();

    UserResponseDto getUserById(int id);

}
